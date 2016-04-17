package pl.bka

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.postfixOps
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global

case class Hash(value: String)
case class Commit(seqNum: Int, hash: Hash, date: DateTime)
case class Word(value: String)
case class WordCount(commit: Commit, word: Word, count: Int)

object Api {
  private val extension = ".scala"

  def hasNext: Boolean =
    Await.result(
      for {
        min <- Db.minCommit
      } yield min.map(minCommit => minCommit.seqNum > 1).getOrElse(true)
      , Duration.Inf)

  def readNext(path: String, take: Int = 10): Seq[WordCount] =
    Await.result(
      for {
        min <- Db.minCommit
      } yield Counter(RealInput(path, extension), min, take).read
    , Duration.Inf)

  def write(data: Seq[WordCount]) = Await.result(Db.write(data), Duration.Inf)

  def loadNext(path: String, take: Int = 10) = write(readNext(path, take))

  def loadAll(path: String, chunkSize: Int) = while(hasNext) loadNext(path, chunkSize)

  def clearDb() = Await.result(Db.delete, Duration.Inf)
}
