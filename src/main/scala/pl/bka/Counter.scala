package pl.bka

case class Counter(input: Input) {
  def read(startCommit: Option[Commit], take: Int = 10): Seq[WordCount] = {
    def wc(lines: Seq[LineOfText]): Seq[(Word, Int)] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size).toSeq
    def filteredCommits: Seq[Commit] =
      input.listCommits.dropWhile(x => x != startCommit.getOrElse(x)).tail.take(take)
    try {
      filteredCommits.flatMap { commit =>
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

