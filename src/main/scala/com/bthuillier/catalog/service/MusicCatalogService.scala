package com.bthuillier.catalog.service

import com.bthuillier.catalog.models.{Album, Artist, Track}

class MusicCatalogService {

  private val artists: Map[String, Artist] = Map(
    "1" -> Artist("1", "black dahlia murder", List(
        Album("Nocturnal", List(
          Track("Everything Went Black", 197),
          Track("What a Horrible Night to Have a Curse", 241),
          Track("Virally Yours", 183),
          Track("I Worship Only What You Bleed", 120),
          Track("Nocturnal", 193),
          Track("Deathmask Divine", 217),
          Track("Of Darkness Spawned", 202),
          Track("Climactic Degradation", 219),
          Track("To a Breathless Oblivion", 297),
          Track("Warborn", 280)
        ))
      )
    )
  )

  def getArtist(id: String): Artist = ???

}
