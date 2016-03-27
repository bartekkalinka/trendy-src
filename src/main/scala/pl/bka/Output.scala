package pl.bka

import java.io.FileWriter
import scala.collection.immutable.ListMap
import scala.io.Codec

object Output extends Using {
  implicit val codec = Codec.UTF8

  def write(data: Map[String, Map[String, Int]], outputPath: String) = {
    data.toSeq.foreach { case (hash, wcs) =>
      val sorted = ListMap(wcs.toSeq.sortWith(_._2 > _._2): _*)
      using(new FileWriter(s"$outputPath/$hash")) { pw =>
        pw.write(sorted.mkString("\n"))
      }
    }
  }
}

