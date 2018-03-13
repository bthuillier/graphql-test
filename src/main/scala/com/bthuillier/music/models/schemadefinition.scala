package com.bthuillier.music.models

import com.bthuillier.music.service.MusicService
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.macros.derive._
import sangria.schema._
import sangria.marshalling.circe._
import io.circe.generic.auto._

import scala.concurrent.Future

object schemadefinition {

  object fetcher {
    val artists: Fetcher[MusicService, Artist, Artist, String] =
      Fetcher((ctx: MusicService, ids: Seq[String]) ⇒
        Future.successful(ids.map(ctx.getArtist).map(_.get))
      )(HasId(_.id))

    val albums: Fetcher[MusicService, Album, Album, String] =
      Fetcher((ctx: MusicService, ids: Seq[String]) ⇒
        Future.successful(ids.map(ctx.getAlbum).map(_.get))
      )(HasId(_.id))
  }

  object models {
    implicit val ArtistResponseType: ObjectType[Unit, Artist] =
      deriveObjectType[Unit, Artist](
        ObjectTypeDescription("an artist")
      )

    implicit val AlbumResponseType: ObjectType[Unit, Album] =
      deriveObjectType[Unit, Album](
        ObjectTypeDescription("a music album"),
        IncludeMethods("totalDuration"),
        ReplaceField("artist", Field("artist", ArtistResponseType, None,
          resolve = ctx => fetcher.artists.defer(ctx.value.artist))
        )
      )

    implicit val TrackResponseType: ObjectType[Unit, Track] =
      deriveObjectType[Unit, Track](
        ObjectTypeDescription("a music track"),
        ReplaceField("artists", Field("artists", ListType(ArtistResponseType), None,
          resolve = ctx => fetcher.artists.deferSeq(ctx.value.artists))
        ),
        ReplaceField("album", Field("album", AlbumResponseType, None,
          resolve = ctx => fetcher.albums.defer(ctx.value.album))
        )
      )

    implicit val TrackRequestType: InputObjectType[Track] =
      deriveInputObjectType[Track](
        InputObjectTypeName("TrackInput")
      )

    implicit val AlbumRequestType: InputObjectType[Album] =
      deriveInputObjectType[Album](
        InputObjectTypeName("AlbumInput")
      )

    implicit val ArtistRequestType: InputObjectType[Artist] =
      deriveInputObjectType[Artist](
        InputObjectTypeName("ArtistInput")
      )


  }
  object arg {
    val ArtistID = Argument("artistID", IDType, description = "id")
    val ArtistInput = Argument("newArtist", models.ArtistRequestType, description = "artist")

    val AlbumID = Argument("albumID", IDType, description = "id")
    val AlbumInput = Argument("newAlbum", models.AlbumRequestType, description = "album")

    val TrackID = Argument("trackID", IDType, description = "id")
    val TrackInput = Argument("newTrack", models.TrackRequestType, description = "track")

  }

  val resolver: DeferredResolver[MusicService] =
    DeferredResolver.fetchers(fetcher.artists)

  val Query: ObjectType[MusicService, Unit] = ObjectType[MusicService, Unit](
    "Query", fields[MusicService, Unit](
      Field("artist", OptionType(models.ArtistResponseType),
        arguments = arg.ArtistID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getArtist(ctx.arg(arg.ArtistID))),
      Field("album", OptionType(models.AlbumResponseType),
        arguments = arg.AlbumID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getAlbum(ctx.arg(arg.AlbumID))),
      Field("track", OptionType(models.TrackResponseType),
        arguments = arg.TrackID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getTrack(ctx.arg(arg.TrackID)))
    ))


}