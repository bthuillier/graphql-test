package com.bthuillier.catalog.models


final case class Artist(id: String, name: String, albums: List[Album])

final case class Album(name: String, tracks: List[Track]) {

  val `total-duration`: Long = tracks.foldLeft(0L)(_ + _.duration)

}

final case class Track(title: String, duration: Long)
