package pl.bka

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class WordPercentage(commit: Commit, percentage: Double)

object ReportApi {
  private def wordPercentageChart(word: Word, data: Seq[WordPercentage]): BufferedImage = {
    val lineChartDataset = new DefaultCategoryDataset()
    data.foreach { percentage =>
      lineChartDataset.addValue(percentage.percentage, "words", percentage.commit.seqNum)
    }

    val lineChartObject = ChartFactory.createLineChart(
      "Wordcounts in commits", "Commit",
      "Wordcount",
      lineChartDataset, PlotOrientation.VERTICAL,
      true, true, false)

    lineChartObject.createBufferedImage(1600, 1000)
  }

  def chart(wordStr: String, outputDirectoryPath: String) = {
    val word = Word(wordStr)
    def writeImage(img: BufferedImage) = {
      val outputFile = new File(outputDirectoryPath, s"${word.value}.png")
      ImageIO.write(img, "png", outputFile)
    }
    Await.result(
      for {
        data <- Db.wordHistory(word)
        img = wordPercentageChart(word, data)
        _ = writeImage(img)
      } yield (), Duration.Inf
    )
  }
}

