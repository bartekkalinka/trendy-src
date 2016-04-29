package pl.bka

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
  private def wordPercentageChart(word: Word, data: Seq[WordPercentage]) = {
    val lineChartDataset = new DefaultCategoryDataset()
    data.foreach { percentage =>
      lineChartDataset.addValue(percentage.percentage, "words" , percentage.commit.seqNum)
    }

    val lineChartObject = ChartFactory.createLineChart(
      "Wordcounts in commits", "Commit",
      "Wordcount",
      lineChartDataset, PlotOrientation.VERTICAL,
      true,true,false)

    val img = lineChartObject.createBufferedImage(1600, 1000)
    val outputfile = new File(s"${word.value}.png")
    ImageIO.write(img, "png", outputfile)
  }

  def chart(wordStr: String) = Await.result(
    for {
      word <- Future.successful(Word(wordStr))
      data <- Db.wordHistory(word)
      _ = wordPercentageChart(word, data)
    } yield (), Duration.Inf
  )
}

