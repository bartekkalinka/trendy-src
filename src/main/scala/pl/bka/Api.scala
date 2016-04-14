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
  def read(path: String, take: Int = 10): Seq[WordCount] =
    Await.result(
      for {
        min <- Db.minCommit
      } yield Counter(RealInput(path, ".scala")).read(min, take)
    , Duration.Inf)

  def write(data: Seq[WordCount]) =
    Await.result(Db.write(data), Duration.Inf)

  def clearDb() = Await.result(Db.delete, Duration.Inf)
}
