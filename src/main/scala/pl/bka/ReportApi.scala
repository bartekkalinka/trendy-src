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
  private def drawChart(word: Word, data: Seq[WordPercentage]) = {
    val xySeries = new XYSeries("percentage")
    data.foreach { percentage =>
      xySeries.add(percentage.commit.date.getMillis, percentage.percentage)
    }
    val xyDataSet = new XYSeriesCollection(xySeries)

    val xyChart = ChartFactory.createXYLineChart(
      word.value, "commits",
      "percentage",
      xyDataSet, PlotOrientation.VERTICAL,
      true, true, false)

    val plot = xyChart.getXYPlot
    val axis = new DateAxis()
    plot.setDomainAxis(axis)

    xyChart.createBufferedImage(1600, 1000)
  }

  def chart(wordStr: String, outputDirectoryPath: String, prefix: Option[Int] = None, dropCommits: Option[Int] = None) = {
    val word = Word(wordStr)
    def writeImage(img: BufferedImage) = {
      val fileNamePrefix = prefix.map(i => "%07d".format(i) + "-").getOrElse("")
      val fileName = s"$fileNamePrefix${word.value}.png"
      val outputFile = new File(outputDirectoryPath, fileName)
      ImageIO.write(img, "png", outputFile)
      println(s"$fileName done")
    }
    val data = Await.result(Db.wordHistory(word), Duration.Inf).drop(dropCommits.getOrElse(0))
    val img = drawChart(word, data)
    writeImage(img)
  }

  def charts(outputDirectoryPath: String, takeWords: Option[Int] = Some(100), dropCommits: Option[Int] = None) = {
    val words = Await.result(Db.allWords, Duration.Inf)
    words.take(takeWords.getOrElse(words.length)).zipWithIndex.foreach { case (word, i) => chart(word.value, outputDirectoryPath, Some(i), dropCommits) }
  }
}

