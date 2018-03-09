package com.bthuillier.catalog

import cats.effect.IO
import fs2.StreamApp
import sangria.marshalling.circe._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import sangria.ast.Document
import sangria.parser.QueryParser
import cats.syntax.either._
import com.bthuillier.catalog.models.graphql
import com.bthuillier.catalog.service.MusicCatalogService
import sangria.execution.Executor

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

object MusicServer extends StreamApp[IO] with Http4sDsl[IO] {
  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "hello" / name =>
      Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))
    case req @ POST -> Root / "graphql" =>
      req.as[Json]
        .flatMap(json => IO.fromEither(handleGraphql(json)))
        .flatMap(f => IO.fromFuture(IO(f)))
        .flatMap(s => Ok(s))
  }


  private def handleGraphql(json: Json): Either[Throwable, Future[Json]] = {
    val parsingResult = for {
      fields <- json.asObject.map(_.toMap)
      query <- fields.get("query").flatMap(_.asString)
      operation = fields.get("operationName").flatMap(_.asString)
      vars = fields.getOrElse("variables", Json.obj())

    } yield QueryParser.parse(query).map { exc =>
      Executor
        .execute(graphql.MusicCatalogSchema, exc, new MusicCatalogService, variables = vars, operationName = operation)
    }

    Either.fromTry(parsingResult.getOrElse(Failure(new Exception("lol"))))

  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}