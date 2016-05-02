package pl.bka

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.jfree.chart.ChartFactory
import org.jfree.chart.axis.{DateAxis, SegmentedTimeline}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class WordPercentage(commit: Commit, percentage: Double)

object ReportApi {
  private def wordPercentageChart(word: Word, data: Seq[WordPercentage]) = {
    val xySeries = new XYSeries("percentage")
    data.foreach { percentage =>
      xySeries.add(percentage.commit.seqNum, percentage.percentage)
    }
    val xyDataSet = new XYSeriesCollection(xySeries)

    val xyChart = ChartFactory.createXYLineChart(
      word.value, "commits",
      "percentage",
      xyDataSet, PlotOrientation.VERTICAL,
      true, true, false)

    xyChart.createBufferedImage(1600, 1000)
  }

  def chart(wordStr: String, outputDirectoryPath: String, prefix: String = "") = {
    val word = Word(wordStr)
    val fileName = s"$prefix-${word.value}.png"
    def writeImage(img: BufferedImage) = {
      val outputFile = new File(outputDirectoryPath, fileName)
      ImageIO.write(img, "png", outputFile)
    }
    Await.result(
      for {
        data <- Db.wordHistory(word)
        img = wordPercentageChart(word, data)
        _ = writeImage(img)
      } yield (), Duration.Inf
    )
    println(s"$fileName done")
  }

  def charts(outputDirectoryPath: String, take: Option[Int] = Some(100)) =
    Await.result(
      for {
        words <- Db.allWords
        _ = words.take(take.getOrElse(words.length)).zipWithIndex.foreach { case (word, i) => chart(word.value, outputDirectoryPath, "%07d".format(i)) }
      } yield (), Duration.Inf
    )
}

