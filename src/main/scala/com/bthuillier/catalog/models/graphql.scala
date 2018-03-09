package com.bthuillier.catalog.models

import com.bthuillier.catalog.service.MusicCatalogService
import sangria.macros.derive._
import sangria.schema._

object graphql {

  implicit val TrackType: ObjectType[Unit, Track] =
    deriveObjectType[Unit, Track](
      ObjectTypeDescription("a music track")
    )

  implicit val AlbumType: ObjectType[Unit, Album] =
    deriveObjectType[Unit, Album](
      ObjectTypeDescription("a music album")
    )

  implicit val ArtistType: ObjectType[Unit, Artist] =
    deriveObjectType[Unit, Artist](
      ObjectTypeDescription("an artist"))

  val ID = Argument("id", StringType, description = "id of the artist")

  val Query: ObjectType[MusicCatalogService, Unit] = ObjectType[MusicCatalogService, Unit](
    "Query", fields[MusicCatalogService, Unit](
      Field("artist", ArtistType,
        arguments = ID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getArtist(ctx.arg(ID)))
    ))

  val MusicCatalogSchema = Schema(Query)


}