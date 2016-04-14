package pl.bka

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

object Db {
  lazy val db = Database.forConfig("db")
  val wcTable = TableQuery[WordCountsTable]
  val deleteAction = sqlu"""DELETE FROM wordcounts"""
  def insertAction(data: Seq[WordCount]) = wcTable ++= data

  private def insert(data: Seq[WordCount]): Future[Unit] = db.run(insertAction(data).map(x => ()))

  def delete: Future[Unit] = db.run(deleteAction).map(x => ())

  def minCommit: Future[Option[Commit]] =
    db.run(sql"""SELECT seqnum, hash, commit_date FROM wordcounts WHERE seqnum = (SELECT MIN(seqnum) FROM wordcounts)"""
      .as[(Int, String, DateTime)]
    ).map(_.headOption).map(_.map(row => Commit(row._1, Hash(row._2), row._3)))

  def write(data: Seq[WordCount]): Future[Unit] = for {
    _ <- insert(data)
  } yield ()
}

