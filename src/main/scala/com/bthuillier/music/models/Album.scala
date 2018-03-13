package com.bthuillier.music.models

import com.bthuillier.music.graphql.{MusicServiceContext, MusicServiceFetchers}
import sangria.macros.derive._
import sangria.schema.{DeferredValue, Field, InputObjectType, ObjectType}

final case class Album(id: String, name: String, tracks: List[Track], artist: String) {

  val totalDuration: Long = tracks.foldLeft(0L)(_ + _.duration)

}

object Album {

  implicit val AlbumResponseType: ObjectType[MusicServiceContext, Album] =
    deriveObjectType[MusicServiceContext, Album](
      ObjectTypeDescription("a music album"),
      IncludeMethods("totalDuration"),
      ReplaceField("artist", Field("artist", Artist.ArtistResponseType, None,
        resolve = ctx => DeferredValue(ctx.ctx.fetchers.artists.defer(ctx.value.artist))
      ))
    )

  implicit val AlbumRequestType: InputObjectType[Album] =
    deriveInputObjectType[Album](
      InputObjectTypeName("AlbumInput")
    )

}