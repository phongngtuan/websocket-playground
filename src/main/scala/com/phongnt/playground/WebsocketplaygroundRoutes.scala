package com.phongnt.playground

import fs2._
import fs2.io._
import scala.concurrent.duration._
import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket._
import fs2.io.file.Files
import fs2.io.file.Path

object WebsocketplaygroundRoutes:

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }

  def sampleFilePath: Path = {
    Path("/tmp/sample.json")
  }

  def readFile[F[_]: Files]: Stream[F, Byte] =
    Files[F].readAll(sampleFilePath)

  def wsRoutes[F[_]: Async](ws: WebSocketBuilder[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "ws" =>
        val json = readFile[F]
          .through(text.utf8.decode)
          .foldMonoid
        val send = Stream.emit(json).flatten.map(WebSocketFrame.Text(_))
          .repeat
          .zipLeft(Stream.awakeEvery(5.seconds))
        val receive: Pipe[F, WebSocketFrame, Unit] =
          in => in.evalMap(frameIn => Sync[F].delay(println("in " + frameIn.length)))
        ws.build(send, receive)
    }
