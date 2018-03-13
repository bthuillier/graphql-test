package com.bthuillier.music.graphql

import com.bthuillier.music.models.{Album, Artist}
import com.bthuillier.music.service.MusicService
import sangria.execution.deferred.{Fetcher, HasId}

import scala.concurrent.Future

case class MusicServiceContext(musicService: MusicService, fetchers: MusicServiceFetchers.type)

case object MusicServiceFetchers {

  val artists: Fetcher[MusicServiceContext, Artist, Artist, String] =
    Fetcher((ctx: MusicServiceContext, ids: Seq[String]) ⇒
      Future.successful(ids.map(ctx.musicService.getArtist).map(_.get))
    )(HasId(_.id))

  val albums: Fetcher[MusicServiceContext, Album, Album, String] =
    Fetcher((ctx: MusicServiceContext, ids: Seq[String]) ⇒
      Future.successful(ids.map(ctx.musicService.getAlbum).map(_.get))
    )(HasId(_.id))

}
