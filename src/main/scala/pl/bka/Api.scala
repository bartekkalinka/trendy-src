package pl.bka

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import org.joda.time.DateTime

case class Hash(value: String)
case class Commit(seqNum: Int, hash: Hash, date: DateTime)
case class Word(value: String)
case class WordCount(commit: Commit, word: Word, count: Int)

object Api {
  def read(path: String, take: Int = 10): Seq[WordCount] =
    Counter(RealInput(path, ".scala")).read(take)

  def write(data: Seq[WordCount]) =
    Await.result(Output.write(data), 1000 seconds)

  def clearDb() = Await.result(Output.delete, 1000 seconds)
}
