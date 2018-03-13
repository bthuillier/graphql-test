package com.bthuillier.music.graphql

import com.bthuillier.music.models.schemadefinition.arg
import sangria.schema.{fields, Field, ObjectType, OptionType, StringType}

object Mutation {

  val Mutation: ObjectType[MusicServiceContext, Unit] = ObjectType[MusicServiceContext, Unit](
    "Mutation", fields[MusicServiceContext, Unit](
      Field("addTrack", OptionType(StringType),
        arguments = arg.TrackInput :: Nil,
        resolve = ctx => ctx.ctx.musicService.addTrack(ctx.arg(arg.TrackInput))
      ),
      Field("addAlbum", OptionType(StringType),
        arguments = arg.AlbumInput :: Nil,
        resolve = ctx => ctx.ctx.musicService.addAlbum(ctx.arg(arg.AlbumInput))
      )
    ))

}
