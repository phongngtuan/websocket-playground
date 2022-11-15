// package com.phongnt.playground

// import cats.effect.{Async, Resource}
// import cats.syntax.all._
// import com.comcast.ip4s._
// import fs2.Stream
// import org.http4s.ember.client.EmberClientBuilder
// import org.http4s.ember.server.EmberServerBuilder
// import org.http4s.implicits._
// import org.http4s.server.middleware.Logger
// import org.http4s.client.Client
// import org.http4s.server.websocket.WebSocketBuilder
// import org.http4s.HttpApp

// object WebsocketplaygroundServer:

//   def stream[F[_]: Async]: Stream[F, Nothing] = {
//     for {
//       client <- Stream.resource(EmberClientBuilder.default[F].build)
//       exitCode <- Stream.resource(
//         EmberServerBuilder.default[F]
//           .withHost(ipv4"0.0.0.0")
//           .withPort(port"8080")
//           .withHttpWebSocketApp(ws => finalHttpApp(client, ws))
//           // .withHttpApp(finalHttpApp)
//           .build >>
//         Resource.eval(Async[F].never)
//       )
//     } yield exitCode
//   }.drain

//   def finalHttpApp[F[_]: Async]: HttpApp[F] =
//     Logger.httpApp(true, true)(httpApp)
  
//   def httpApp[F[_]: Async](client: Client[F], ws: WebSocketBuilder[F]): HttpApp[F] =
//     val helloWorldAlg = HelloWorld.impl[F]
//     val jokeAlg = Jokes.impl[F](client)
//     (
//       WebsocketplaygroundRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
//       WebsocketplaygroundRoutes.jokeRoutes[F](jokeAlg) <*>
//       WebsocketplaygroundRoutes.routes[F](ws)
//     ).orNotFound