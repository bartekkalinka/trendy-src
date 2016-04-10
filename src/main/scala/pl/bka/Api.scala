package pl.bka

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import org.joda.time.DateTime

case class Hash(value: String)
case class Commit(seqNum: Int, hash: Hash, date: DateTime)
case class Word(value: String)
case class WordCount(commit: Commit, word: Word, count: Int)
case class Chunk(data: List[WordCount])

object Api {
  def read(path: String, take: Int = 10): List[WordCount] =
    Counter(RealInput(path, ".scala")).read(take)

  def stream(path: String, chunkSize: Int): Stream[Chunk] =
    Counter(RealInput(path, ".scala")).stream(chunkSize)

  def write(data: List[WordCount]) =
    Await.result(Output.write(data), 1000 seconds)
}
