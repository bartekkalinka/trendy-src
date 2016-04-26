package pl.bka

import java.io.File
import javax.imageio.ImageIO

import org.jfree.chart.ChartFactory
import org.jfree.data.general.DefaultPieDataset

object ReportApi {
  def demo = {
    val objDataset = new DefaultPieDataset()
    objDataset.setValue("Apple", 29)
    objDataset.setValue("HTC", 15)
    objDataset.setValue("Samsung", 24)
    objDataset.setValue("LG", 7)
    objDataset.setValue("Motorola", 10)
    val objChart = ChartFactory.createPieChart (
      "Demo Pie Chart",   //Chart title
      objDataset,          //Chart Data
      true,               // include legend?
      true,               // include tooltips?
      false               // include URLs?
    )
    val img = objChart.createBufferedImage(200, 200)
    val outputfile = new File("saved.png")
    ImageIO.write(img, "png", outputfile)
  }
}

