package com.bthuillier.music.models

import com.bthuillier.music.graphql.{MusicServiceContext, MusicServiceFetchers}
import com.bthuillier.music.service.MusicService
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.macros.derive._
import sangria.schema._
import sangria.marshalling.circe._
import io.circe.generic.auto._


object schemadefinition {

  object arg {
    val ArtistID = Argument("artistID", IDType, description = "id")
    val ArtistInput = Argument("newArtist", Artist.ArtistRequestType, description = "artist")

    val AlbumID = Argument("albumID", IDType, description = "id")
    val AlbumInput = Argument("newAlbum", Album.AlbumRequestType, description = "album")

    val TrackID = Argument("trackID", IDType, description = "id")
    val TrackInput = Argument("newTrack", Track.TrackRequestType, description = "track")

  }

  val resolver: DeferredResolver[MusicServiceContext] =
    DeferredResolver.fetchers(MusicServiceFetchers.artists)

  val Query: ObjectType[MusicServiceContext, Unit] = ObjectType[MusicServiceContext, Unit](
    "Query", fields[MusicServiceContext, Unit](
      Field("artist", OptionType(Artist.ArtistResponseType),
        arguments = arg.ArtistID :: Nil,
        resolve = ctx ⇒ ctx.ctx.musicService.getArtist(ctx.arg(arg.ArtistID))),
      Field("album", OptionType(Album.AlbumResponseType),
        arguments = arg.AlbumID :: Nil,
        resolve = ctx ⇒ ctx.ctx.musicService.getAlbum(ctx.arg(arg.AlbumID))),
      Field("track", OptionType(Track.TrackResponseType),
        arguments = arg.TrackID :: Nil,
        resolve = ctx ⇒ ctx.ctx.musicService.getTrack(ctx.arg(arg.TrackID)))
    ))


}