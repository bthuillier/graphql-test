package com.bthuillier.music.models

import sangria.macros.derive.{deriveInputObjectType, deriveObjectType, InputObjectTypeName, ObjectTypeDescription}
import sangria.schema.{InputObjectType, ObjectType}

final case class Artist(id: String, name: String, albums: List[Album])

object Artist {

  implicit val ArtistResponseType: ObjectType[Unit, Artist] =
    deriveObjectType[Unit, Artist](
      ObjectTypeDescription("an artist")
    )

  implicit val ArtistRequestType: InputObjectType[Artist] =
    deriveInputObjectType[Artist](
      InputObjectTypeName("ArtistInput")
    )

}