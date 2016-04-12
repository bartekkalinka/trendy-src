package pl.bka

import java.time.LocalDateTime

import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.TableQuery
import com.github.tototoshi.slick.PostgresJodaSupport._

class WordCountsTable(tag: Tag) extends Table[WordCount](tag, "wordcounts") {
  def seqNum = column[Int]("seqnum")
  def hashColumn = column[String]("hash")
  def commitDateColumn = column[DateTime]("commit_date")
  def wordColumn = column[String]("word")
  def count = column[Int]("count")
  def commit = (seqNum, hashColumn, commitDateColumn) <> ( { (seqNum: Int, value: String, date: DateTime) => Commit(seqNum, Hash(value), date) }.tupled, { commit: Commit => Some((commit.seqNum, commit.hash.value, commit.date)) })
  def word = wordColumn <> (Word.apply, Word.unapply)

  def * = (commit, word, count) <> ((WordCount.apply _).tupled, WordCount.unapply)
}

object Output {
  lazy val db = Database.forConfig("db")
  val wcTable = TableQuery[WordCountsTable]
  val deleteAction = sqlu"""DELETE FROM wordcounts"""
  def insertAction(data: Seq[WordCount]) = wcTable ++= data

  private def insert(data: Seq[WordCount]): Future[Unit] = db.run(insertAction(data).map(x => ()))

  def delete: Future[Unit] = db.run(deleteAction).map(x => ())

  def write(data: Seq[WordCount]): Future[Unit] = for {
    _ <- delete
    _ <- insert(data)
  } yield ()
}

