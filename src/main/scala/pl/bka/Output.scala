package pl.bka

import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.TableQuery

class WordCountsTable(tag: Tag) extends Table[WordCount](tag, "wordcounts") {
  def hashColumn = column[String]("hash")
  def wordColumn = column[String]("word")
  def count = column[Int]("count")
  def commit = hashColumn <> ( { value: String => Commit(Hash(value)) }, { commit: Commit => Some(commit.hash.value) })
  def word = wordColumn <> (Word.apply, Word.unapply)

  def * = (commit, word, count) <> ((WordCount.apply _).tupled, WordCount.unapply)
}

object Output {
  def write(data: Seq[WordCount]): Future[Unit] = {
    val db = Database.forConfig("db")
    val wcTable = TableQuery[WordCountsTable]
    val deleteAction = sqlu"""DELETE FROM wordcounts"""
    val insertAction = wcTable ++= data
    db.run(
      for {
        _ <- deleteAction
        _ <- insertAction
      } yield ()
    )
  }
}

