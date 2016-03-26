package pl.bka

import java.io.{File, FileWriter, PrintWriter}

import scala.io.{Codec, Source}
import sys.process._
import scala.collection.immutable.ListMap

case class Hash(value: String)
case class Commit(hash: Hash)
case class Word(value: String)

object Api {
  implicit val codec = Codec.UTF8

  def read(dir: File, take: Int = 10): Map[Commit, Map[Word, Int]] = {
    def listFiles(dir: File): Seq[File] = {
      val files = dir.listFiles
      val scalas = files.filter(_.getName.endsWith(".scala"))
      scalas ++ files.filter(_.isDirectory).flatMap(listFiles)
    }

    def wc(file: File): Map[Word, Int] = {
      using(Source.fromFile(file)) { source =>
        source.getLines()
          .flatMap(_.split("\\W+"))
          .filterNot(_ == "")
          .map(Word)
          .foldLeft(Map.empty[Word, Int]) {
          (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
        }
      }
    }

    def wcFiles(files: Seq[File]): Map[Word, Int] = {
      files.map(wc).foldLeft(Map.empty[Word, Int]) {
        (left, right) => (left.keySet ++ right.keySet).map(i => (i, left.getOrElse(i, 0) + right.getOrElse(i, 0))).toMap
      }
    }

    def listCommits(dir: File): Seq[Commit] = {
      val lines = Seq("git", "-C", dir.getAbsolutePath, "log", "--pretty=format:\"%h\"").!!
      lines.split("\n").map(str => Commit(Hash(str.replace("\"", "")))).reverse
    }

    def checkout(dir: File, hash: Hash) = {
      val lines = Seq("git", "-C", dir.getAbsolutePath, "checkout", hash.value).!!
      true
    }

    listCommits(dir).take(take).map(commit => {
      checkout(dir, commit.hash)
      (commit, wcFiles(listFiles(dir)))
    }).toMap
  }

  def write(data: Map[String, Map[String, Int]], outputPath: String) = {
    data.toSeq.foreach { case (hash, wcs) =>
      val sorted = ListMap(wcs.toSeq.sortWith(_._2 > _._2): _*)
      using(new FileWriter(s"$outputPath/$hash")) { pw =>
        pw.write(sorted.mkString("\n"))
      }
    }
  }

  private def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }

}