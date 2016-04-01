package pl.bka

import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Output {
  def write(data: Seq[WordCount]): Future[Unit] = {
    val db = Database.forConfig("db")
    db.run(DBIO.sequence(data.map { wc =>
      sqlu"""INSERT INTO wordcounts(hash, word, count) VALUES (${wc.commit.hash.value}, ${wc.word.value}, ${wc.count})"""
    })).map(x => ())
  }
}

