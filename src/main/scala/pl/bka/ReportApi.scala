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

  def chart(wordStr: String, outputDirectoryPath: String, prefix: Option[Int] = None) = {
    val word = Word(wordStr)
    val fileNamePrefix = prefix.map(i => "%07d".format(i) + "-").getOrElse("")
    val fileName = s"$fileNamePrefix${word.value}.png"
    def writeImage(img: BufferedImage) = {
      val outputFile = new File(outputDirectoryPath, fileName)
      ImageIO.write(img, "png", outputFile)
    }
    val data = Await.result(Db.wordHistory(word), Duration.Inf)
    val img = wordPercentageChart(word, data)
    writeImage(img)
    println(s"$fileName done")
  }

  def charts(outputDirectoryPath: String, take: Option[Int] = Some(100)) = {
    val words = Await.result(Db.allWords, Duration.Inf)
    words.take(take.getOrElse(words.length)).zipWithIndex.foreach { case (word, i) => chart(word.value, outputDirectoryPath, Some(i)) }
  }
}

