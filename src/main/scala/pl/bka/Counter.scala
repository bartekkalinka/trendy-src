package pl.bka

case class Counter(input: Input) extends Using {
  def read(take: Int = 10): Map[Commit, Map[Word, Int]] = {
    def wc(lines: Seq[LineOfText]): Map[Word, Int] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size)

    def wcFiles(filesLines: Seq[Seq[LineOfText]]): Map[Word, Int] =
      filesLines.map(wc).foldLeft(Map.empty[Word, Int]) { (left, right) =>
        (left.keySet ++ right.keySet)
          .map(i =>
            (i, left.getOrElse(i, 0) + right.getOrElse(i, 0))
          ).toMap
        }

    input.listCommits.take(take).map(commit =>
      (commit, wcFiles(input.checkout(commit.hash)))
    ).toMap
  }
}

