package pl.bka

case class Counter(input: Input) extends Using {
  def read(take: Int = 10): Map[Commit, Map[Word, Int]] = {
    def wc(lines: Seq[LineOfText]): Map[Word, Int] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size)

    input.listCommits.take(take).map(commit =>
      (commit, wc(input.checkout(commit.hash)))
    ).toMap
  }
}

