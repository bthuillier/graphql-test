package com.bthuillier.catalog.models

import com.bthuillier.catalog.service.MusicCatalogService
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.Future

object graphql {

  val artists: Fetcher[MusicCatalogService, Artist, Artist, String] =
    Fetcher((ctx: MusicCatalogService, ids: Seq[String]) ⇒
      Future.successful(ids.map(ctx.getArtist).map(_.get))
    )(HasId(_.id))

  val albums: Fetcher[MusicCatalogService, Album, Album, String] =
    Fetcher((ctx: MusicCatalogService, ids: Seq[String]) ⇒
      Future.successful(ids.map(ctx.getAlbum).map(_.get))
    )(HasId(_.id))

  val resolver: DeferredResolver[MusicCatalogService] =
    DeferredResolver.fetchers(artists)

  implicit val ArtistType: ObjectType[Unit, Artist] =
    deriveObjectType[Unit, Artist](
      ObjectTypeDescription("an artist")
    )

  implicit val AlbumType: ObjectType[Unit, Album] =
    deriveObjectType[Unit, Album](
      ObjectTypeDescription("a music album"),
      IncludeMethods("totalDuration"),
      ReplaceField("artist", Field("artist", ArtistType, None,
        resolve = ctx => artists.defer(ctx.value.artist))
      )
    )

  implicit val TrackType: ObjectType[Unit, Track] =
    deriveObjectType[Unit, Track](
      ObjectTypeDescription("a music track"),
      ReplaceField("artists", Field("artists", ListType(ArtistType), None,
        resolve = ctx => artists.deferSeq(ctx.value.artists))
      ),
      ReplaceField("album", Field("album", AlbumType, None,
        resolve = ctx => albums.defer(ctx.value.album))
      )
    )

  val IDArg = Argument("id", IDType, description = "id")

  val Query: ObjectType[MusicCatalogService, Unit] = ObjectType[MusicCatalogService, Unit](
    "Query", fields[MusicCatalogService, Unit](
      Field("artist", OptionType(ArtistType),
        arguments = IDArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getArtist(ctx.arg(IDArg))),
      Field("album", OptionType(AlbumType),
        arguments = IDArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getAlbum(ctx.arg(IDArg))),
      Field("track", OptionType(TrackType),
        arguments = IDArg :: Nil,
        resolve = ctx ⇒ ctx.ctx.getTrack(ctx.arg(IDArg)))
    ))

  val Mutation: ObjectType[MusicCatalogService, Unit] = ObjectType[MusicCatalogService, Unit](
    "Mutation", fields[MusicCatalogService, Unit](
      Field("addTrack", OptionType(StringType),
        arguments = IDArg :: Nil,
        resolve = ctx => ctx.ctx.addTrack(ctx.arg(IDArg))
      )
    ))

  val MusicCatalogSchema = Schema(Query)


}