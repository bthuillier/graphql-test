package com.bthuillier.music.graphql

import com.bthuillier.music.models.schemadefinition.arg
import com.bthuillier.music.service.MusicService
import sangria.schema.{fields, Field, ObjectType, OptionType, StringType}

object Mutation {

  val Mutation: ObjectType[MusicService, Unit] = ObjectType[MusicService, Unit](
    "Mutation", fields[MusicService, Unit](
      Field("addTrack", OptionType(StringType),
        arguments = arg.TrackInput :: Nil,
        resolve = ctx => ctx.ctx.addTrack(ctx.arg(arg.TrackInput))
      ),
      Field("addAlbum", OptionType(StringType),
        arguments = arg.AlbumInput :: Nil,
        resolve = ctx => ctx.ctx.addAlbum(ctx.arg(arg.AlbumInput))
      )
    ))

}
