package com.bthuillier.catalog.models


final case class Artist(id: String, name: String, albums: List[Album])

final case class Album(id: String, name: String, tracks: List[Track], artist: String) {

  val totalDuration: Long = tracks.foldLeft(0L)(_ + _.duration)

}

final case class Track(id: String, title: String, duration: Long, artists: Seq[String], album: String)
