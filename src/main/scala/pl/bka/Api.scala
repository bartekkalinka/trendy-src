package pl.bka

import java.io.{File, FileWriter, PrintWriter}

import scala.io.{Codec, Source}
import sys.process._
import scala.collection.immutable.ListMap

object Api {
  implicit val codec = Codec.UTF8

  def read(dir: File, take: Int = 10): Map[String, Map[String, Int]] = {
    def listFiles(dir: File): Array[File] = {
      val files = dir.listFiles
      val scalas = files.filter(_.getName.endsWith(".scala"))
      scalas ++ files.filter(_.isDirectory).flatMap(listFiles(_))
    }

    def wc(file: File): Map[String, Int] = {
      using(Source.fromFile(file)) { source =>
        source.getLines
          .flatMap(_.split("\\W+"))
          .filterNot(_ == "")
          .foldLeft(Map.empty[String, Int]) {
          (count, word) => count + (word -> (count.getOrElse(word, 0) + 1))
        }
      }
    }

    def wcFiles(files: Array[File]): Map[String, Int] = {
      files.map(wc(_)).foldLeft(Map.empty[String, Int]) {
        (left, right) => (left.keySet ++ right.keySet).map(i => (i, left.getOrElse(i, 0) + right.getOrElse(i, 0))).toMap
      }
    }

    def listHashes(dir: File): Array[String] = {
      val lines = Seq("git", "-C", dir.getAbsolutePath, "log", "--pretty=format:\"%h\"").!!
      lines.split("\n").map(_.replace("\"", "")).reverse
    }

    def checkout(dir: File, hash: String) = {
      val lines = Seq("git", "-C", dir.getAbsolutePath, "checkout", hash).!!
      true
    }

    listHashes(dir).take(take).map(hash => {
      checkout(dir, hash)
      (hash, wcFiles(listFiles(dir)))
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