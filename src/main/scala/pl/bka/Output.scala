package pl.bka

import java.io.FileWriter
import scala.collection.immutable.ListMap
import scala.io.Codec

object Output extends CloseSupport {
  implicit val codec = Codec.UTF8

  def write(data: Map[String, Map[String, Int]], outputPath: String) = {
    data.toSeq.foreach { case (hash, wcs) =>
      val sorted = ListMap(wcs.toSeq.sortWith(_._2 > _._2): _*)
      closeAfterRun(new FileWriter(s"$outputPath/$hash")) { pw =>
        pw.write(sorted.mkString("\n"))
      }
    }
  }
}

