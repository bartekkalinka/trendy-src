package pl.bka

import java.io.File
import org.joda.time.format.DateTimeFormat

import scala.io.Source
import sys.process._

case class LineOfText(value: String)

trait Input {
  def listCommits: Seq[Commit]
  def checkout(hash: Hash): Seq[LineOfText]
  def cleanup: Unit
}

case class RealInput(dirPath: String, fileExt: String) extends Input with CloseSupport {
  val dir = new File(dirPath)

  def listCommits: Seq[Commit] = {
    def parseCommit(gitLogString: String, index: Int): Commit = {
      val gitLogSplit = gitLogString.split(" ")
      val hash = Hash(gitLogSplit.head.replace("\"", ""))
      val date = gitLogSplit.tail.take(2).reduce(_ + " " + _).replace("\"", "")
      val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
      Commit(index, hash, formatter.parseDateTime(date))
    }
    val lines = Seq("git", "-C", dir.getAbsolutePath, "log", "--date=iso", "--pretty=format:\"%h %cd\"").!!
    lines.split("\n").reverse.zipWithIndex.map((parseCommit _).tupled).reverse
  }

  def checkout(hash: Hash): Seq[LineOfText] = {
    def gitCheckout(hash: Hash): Unit =
      Seq("git", "-C", dir.getAbsolutePath, "checkout", hash.value).!!
    def listFiles(dir: File): Seq[File] = {
      val files = dir.listFiles
      val scalas = files.filter(_.getName.endsWith(fileExt))
      scalas ++ files.filter(_.isDirectory).flatMap(listFiles)
    }
    def fileLines(file: File): Seq[LineOfText] =
      closeAfterRun(Source.fromFile(file)) { source => source.getLines().map(LineOfText).toList }
    gitCheckout(hash)
    listFiles(dir).flatMap(fileLines)
  }

  def cleanup: Unit =
    Seq("git", "-C", dir.getAbsolutePath, "checkout", "master").!!
}

