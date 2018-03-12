package com.bthuillier.catalog.models

import com.bthuillier.catalog.service.MusicCatalogService
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.Future

object graphql {

  object fetcher {
    val artists: Fetcher[MusicCatalogService, Artist, Artist, String] =
      Fetcher((ctx: MusicCatalogService, ids: Seq[String]) ⇒
        Future.successful(ids.map(ctx.getArtist).map(_.get))
      )(HasId(_.id))

    val albums: Fetcher[MusicCatalogService, Album, Album, String] =
      Fetcher((ctx: MusicCatalogService, ids: Seq[String]) ⇒
        Future.successful(ids.map(ctx.getAlbum).map(_.get))
      )(HasId(_.id))
  }

  object models {
    implicit val ArtistType: ObjectType[Unit, Artist] =
      deriveObjectType[Unit, Artist](
        ObjectTypeDescription("an artist")
      )

    implicit val AlbumType: ObjectType[Unit, Album] =
      deriveObjectType[Unit, Album](
        ObjectTypeDescription("a music album"),
        IncludeMethods("totalDuration"),
        ReplaceField("artist", Field("artist", ArtistType, None,
          resolve = ctx => fetcher.artists.defer(ctx.value.artist))
        )
      )

    implicit val TrackType: ObjectType[Unit, Track] =
      deriveObjectType[Unit, Track](
        ObjectTypeDescription("a music track"),
        ReplaceField("artists", Field("artists", ListType(ArtistType), None,
          resolve = ctx => fetcher.artists.deferSeq(ctx.value.artists))
        ),
        ReplaceField("album", Field("album", AlbumType, None,
          resolve = ctx => fetcher.albums.defer(ctx.value.album))
        )
      )

  }

  object arg {
    val ArtistID = Argument("artistID", IDType, description = "id")
    val AlbumID = Argument("albumID", IDType, description = "id")
    val TrackID = Argument("trackID", IDType, description = "id")
  }

  val resolver: DeferredResolver[MusicCatalogService] =
    DeferredResolver.fetchers(fetcher.artists)

  val Query: ObjectType[MusicCatalogService, Unit] = ObjectType[MusicCatalogService, Unit](
    "Query", fields[MusicCatalogService, Unit](
      Field("artist", OptionType(models.ArtistType),
        arguments = arg.ArtistID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getArtist(ctx.arg(arg.ArtistID))),
      Field("album", OptionType(models.AlbumType),
        arguments = arg.AlbumID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getAlbum(ctx.arg(arg.AlbumID))),
      Field("track", OptionType(models.TrackType),
        arguments = arg.TrackID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getTrack(ctx.arg(arg.TrackID)))
    ))

  val Mutation: ObjectType[MusicCatalogService, Unit] = ObjectType[MusicCatalogService, Unit](
    "Mutation", fields[MusicCatalogService, Unit](
      Field("addTrack", OptionType(StringType),
        arguments = arg.ArtistID :: Nil,
        resolve = ctx => ctx.ctx.addTrack(ctx.arg(arg.ArtistID))
      )
    ))

  val MusicCatalogSchema = Schema(Query)


}