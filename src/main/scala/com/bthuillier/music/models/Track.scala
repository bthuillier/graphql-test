package com.bthuillier.music.models

import com.bthuillier.music.graphql.MusicServiceContext
import sangria.macros.derive._
import sangria.schema.{Field, InputObjectType, ListType, ObjectType}

final case class Track(id: String, title: String, duration: Long, artists: Seq[String], album: String)


object Track {

  implicit val TrackResponseType: ObjectType[MusicServiceContext, Track] =
    deriveObjectType[MusicServiceContext, Track](
      ObjectTypeDescription("a music track"),
      ReplaceField("artists", Field("artists", ListType(Artist.ArtistResponseType), None,
        resolve = ctx => ctx.ctx.fetchers.artists.deferSeq(ctx.value.artists))
      ),
      ReplaceField("album", Field("album", Album.AlbumResponseType, None,
        resolve = ctx => ctx.ctx.fetchers.albums.defer(ctx.value.album))
      )
    )

  implicit val TrackRequestType: InputObjectType[Track] =
    deriveInputObjectType[Track](
      InputObjectTypeName("TrackInput")
    )

}

