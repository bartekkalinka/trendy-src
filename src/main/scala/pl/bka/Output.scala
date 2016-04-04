package pl.bka

import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.TableQuery

case class DbWordCount(hash: String, word: String, count: Int)

class WordCountsTable(tag: Tag) extends Table[DbWordCount](tag, "wordcounts") {
  def hash = column[String]("hash")
  def word = column[String]("word")
  def count = column[Int]("count")
  def * = (hash, word, count) <> ((DbWordCount.apply _).tupled, DbWordCount.unapply)
}

object Output {
  def write(data: Seq[WordCount]): Future[Unit] = {
    val db = Database.forConfig("db")
    val wcTable = TableQuery[WordCountsTable]
    val recordsToInsert = data.map(wc => DbWordCount(wc.commit.hash.value, wc.word.value, wc.count))
    val deleteAction = sqlu"""DELETE FROM wordcounts"""
    val insertAction = wcTable ++= recordsToInsert
    db.run(
      for {
        _ <- deleteAction
        _ <- insertAction
      } yield ()
    )
  }
}

