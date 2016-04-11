package pl.bka

import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.{Database, DatabaseDef}
import slick.driver.PostgresDriver.api._
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.TableQuery
import com.github.tototoshi.slick.PostgresJodaSupport._

class WordCountsTable(tag: Tag) extends Table[WordCount](tag, "wordcounts") {
  def seqNum = column[Int]("seqnum")
  def hashColumn = column[String]("hash")
  def commitDateColumn = column[DateTime]("commit_date")
  def wordColumn = column[String]("word")
  def count = column[Int]("count")
  def commit = (seqNum, hashColumn, commitDateColumn) <> (
    { (seqNum: Int, value: String, date: DateTime) => Commit(seqNum, Hash(value), date) }.tupled,
    { commit: Commit => Some((commit.seqNum, commit.hash.value, commit.date)) }
  )
  def word = wordColumn <> (Word.apply, Word.unapply)

  def * = (commit, word, count) <> ((WordCount.apply _).tupled, WordCount.unapply)
}

object Output {

  val wcTable = TableQuery[WordCountsTable]

  val deleteAction = sqlu"""DELETE FROM wordcounts"""

  private def insertAction(data: Seq[WordCount]) = wcTable ++= data

  private def connect: DatabaseDef = Database.forConfig("db")

  def write(data: Seq[WordCount]): Future[Unit] = {
    val db = connect

    db.run(
      for {
        _ <- deleteAction
        _ <- insertAction(data)
      } yield ()
    )
  }

  def write(chunks: ChunksStream): Unit = {
    val db = connect
    def writeChunk(chunk: Chunk): Unit =
      Await.result(db.run(insertAction(chunk.data).map(x => ())), 10 seconds)

    try {
      Await.result(db.run(deleteAction), 10 seconds)
      chunks.stream.foreach(writeChunk)
    }
    finally {
      chunks.cleanup()
    }
  }
}

