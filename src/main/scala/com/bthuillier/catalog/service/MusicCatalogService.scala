package com.bthuillier.catalog.service

import com.bthuillier.catalog.models.{Album, Artist, Track}

class MusicCatalogService {

  private var artists: Map[String, Artist] = Map(
    "1" -> Artist("1", "Black Dahlia Murder", List(
        Album("1", "Nocturnal", List(
          Track("1", "Everything Went Black", 197, Seq("1"), "1"),
          Track("2", "What a Horrible Night to Have a Curse", 241, Seq("1"), "1"),
          Track("3", "Virally Yours", 183, Seq("1"), "1"),
          Track("4", "I Worship Only What You Bleed", 120, Seq("1"), "1"),
          Track("5", "Nocturnal", 193, Seq("1"), "1"),
          Track("6", "Deathmask Divine", 217, Seq("1"), "1"),
          Track("7", "Of Darkness Spawned", 202, Seq("1"), "1"),
          Track("8", "Climactic Degradation", 219, Seq("1"), "1"),
          Track("9", "To a Breathless Oblivion", 297, Seq("1"), "1"),
          Track("10", "Warborn", 280, Seq("1"), "1")
        ), "1")
      )
    )
  )

  def getArtist(id: String): Option[Artist] = artists.get(id)

  def getAlbum(id: String): Option[Album] = artists.flatMap(_._2.albums).find(album => album.id == id)

  def getTrack(id: String): Option[Track] =
    artists.flatMap(_._2.albums).flatMap(_.tracks).find(track => track.id == id)

  def addTrack(albumId: String): Option[String] = {
    ???
  }

}
