package com.bthuillier.music

import cats.effect._
import cats.syntax.either._
import com.bthuillier.music.graphql.{MusicServiceContext, MusicServiceFetchers, Mutation}
import com.bthuillier.music.models.{schemadefinition, Album, Artist, Track}
import com.bthuillier.music.models.schemadefinition.Query
import com.bthuillier.music.service.MusicService
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import sangria.execution.Executor
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import sangria.schema.Schema

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure

object MusicServer extends StreamApp[IO] with Http4sDsl[IO] {

  val MusicCatalogSchema = Schema(Query, Some(Mutation.Mutation))

  private val artists: mutable.HashMap[String, Artist] = mutable.HashMap(
    "1" -> Artist("1", "Black Dahlia Murder", List(
      Album("1", "Nocturnal", List(
        Track("1", "Everything Went Black", 197, Seq("1"), "1"),
        Track("2", "What a Horrible Night to Have a Curse", 241, Seq("1"), "1"),
        Track("3", "Virally Yours", 183, Seq("1"), "1"),
        Track("4", "I Worship Only What You Bleed", 120, Seq("1"), "1"),
        Track("5", "Nocturnal", 193, Seq("1"), "1"),
        Track("6", "Deathmask Divine", 217, Seq("1"), "1"),
        Track("7", "Of Darkness Spawned", 202, Seq("1"), "1"),
        Track("8", "Climactic Degradation", 219, Seq("1"), "1"),
        Track("9", "To a Breathless Oblivion", 297, Seq("1"), "1"),
        Track("10", "Warborn", 280, Seq("1"), "1")
      ), "1")
    )
    )
  )


  val service: HttpService[IO] = HttpService[IO] {
    case req @ GET -> Root =>
      StaticFile.fromResource("/graphiql.html", Some(req)).getOrElseF(NotFound())
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
      vars: Json = fields.get("variables").flatMap(_.asObject).map(Json.fromJsonObject).getOrElse(Json.obj())

    } yield QueryParser.parse(query).map { exc =>
      Executor
        .execute(
          MusicCatalogSchema,
          exc,
          MusicServiceContext(new MusicService(artists), MusicServiceFetchers),
          variables = vars,
          operationName = operation,
          deferredResolver = schemadefinition.resolver)
    }

    Either.fromTry(parsingResult.getOrElse(Failure(new Exception("lol"))))

  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
