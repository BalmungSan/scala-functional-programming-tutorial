package co.edu.eafit.dis.progfun.catscases

import cats.{Applicative, Id}
import cats.instances.list._ // Brings the implicit Traverse[List[_]] instance to scope.
import cats.syntax.functor._ // Provides the map method.
import cats.syntax.traverse._ // Provides the traverse method.
import scala.concurrent.{Future, ExecutionContext}
import scala.language.higherKinds // Enable the use of higher-kinded types, like F[_].

object AsyncTesting extends App {
  /** Abstract interface for fetching uptime of a server inside an effect type. */
  trait UptimeClient[F[_]] {
    def getUptime(hostname: String): F[Int]
  }

  /** Business logic, get the total uptime of a List of servers. */
  final class UptimeService[F[_]: Applicative] (client: UptimeClient[F]) {
    def getTotalUptime(hostnames: List[String]): F[Int] =
      hostnames.traverse(client.getUptime).map(_.sum)
  }

  /** Real implementation (asynchronous). */
  final class RealUptimeClient(implicit ec: ExecutionContext) extends UptimeClient[Future] {
    override def getUptime(hostname: String): Future[Int] =
      Future {
        Thread.sleep(1000)
      } map {
        _ => 10 // Dummy implementation.
      }
  }

  /** Test implementation (synchronous & in-memory). */
  final class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Id] {
    override def getUptime(hostname: String): Id[Int] =
      hosts.getOrElse(hostname, 0)
  }

  // Test the uptime service.
  val service = new UptimeService(
    client = new TestUptimeClient(
      hosts = Map("host1" -> 10, "host2" -> 6)
    )
  )
  val actual = service.getTotalUptime(List("host1", "host2"))
  val expected = 16
  println(s"Actual == Expected is ${actual == expected}")
}
