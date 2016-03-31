package pl.bka

case class Counter(input: Input) {
  def read(take: Int = 10): Seq[WordCount] = {
    def wc(lines: Seq[LineOfText]): Seq[(Word, Int)] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size).toSeq
    try {
      input.listCommits.take(take).flatMap { commit =>
        wc(input.checkout(commit.hash)).map { case (word, count) =>
          WordCount(commit, word, count)
        }
      }
    }
    finally {
      input.cleanup
    }
  }
}

