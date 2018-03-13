package com.bthuillier.music.service

import com.bthuillier.music.models.{Album, Artist, Track}

import scala.collection.mutable

class MusicService(artists: mutable.HashMap[String, Artist]) {

  def getArtist(id: String): Option[Artist] =
    artists.get(id)

  def getAlbum(id: String): Option[Album] = artists.flatMap(_._2.albums).find(album => album.id == id)

  def getTrack(id: String): Option[Track] =
    artists.flatMap(_._2.albums).flatMap(_.tracks).find(track => track.id == id)

  def addTrack(track: Track): Option[String] = {
    for {
      album <- getAlbum(track.album)
      newAlbum = album.copy(tracks = track :: album.tracks)
      artist <- getArtist(album.artist)
    } yield {
      val newArtist = artist.copy(albums = newAlbum :: artist.albums.filterNot(_.id == track.album))
      artists.update(artist.id, newArtist)
      "Ok"
    }
  }

  def addAlbum(album: Album): Option[String] = for {
    artist <- getArtist(album.artist)
    newArtist = artist.copy(albums = album :: artist.albums)
  } yield {
    artists.update(newArtist.id, newArtist)
    "Ok"
  }

  def addArtist(artist: Artist): Option[String] = Some {
    artists.update(artist.id, artist)
    "Ok"
  }

}
