package pl.bka

import java.io.File

case class Hash(value: String)
case class Commit(hash: Hash)
case class Word(value: String)

object Api {
  def read(dir: File, take: Int = 10): Map[Commit, Map[Word, Int]] =
    Counter(RealInput(dir, ".scala")).read(take)

  def write(data: Map[String, Map[String, Int]], outputPath: String) =
    Output.write(data, outputPath)
}