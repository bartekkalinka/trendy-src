package pl.bka

case class Hash(value: String)
case class Commit(hash: Hash)
case class Word(value: String)
case class WordCount(commit: Commit, word: Word, count: Int)

object Api {
  def read(path: String, take: Int = 10): Seq[WordCount] =
    Counter(RealInput(path, ".scala")).read(take)

  def write(data: Map[String, Map[String, Int]], outputPath: String) =
    Output.write(data, outputPath)
}
