package com.phongnt.playground

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    WebsocketplaygroundServer.stream[IO].compile.drain
