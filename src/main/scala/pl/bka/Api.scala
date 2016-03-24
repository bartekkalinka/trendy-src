package pl.bka

import java.io.{File, FileWriter, PrintWriter}

import scala.io.{Codec, Source}
import sys.process._
import scala.collection.immutable.ListMap

object Api {
  implicit val codec = Codec.UTF8

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

  def run(dir: File, take: Int = 10) = {
    listHashes(dir).take(take).foreach(hash => {
      checkout(dir, hash)
      val wcs = wcFiles(listFiles(dir))

      val sorted = ListMap(wcs.toSeq.sortWith(_._2 > _._2):_*)
      using(new FileWriter(s"/home/dp/projects/tmp/$hash")) { pw =>
        pw.write(sorted.mkString("\n"))
      }
    })
  }

  def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close()
    }
}