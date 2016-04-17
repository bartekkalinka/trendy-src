package pl.bka

case class Counter(input: Input, startCommit: Option[Commit], take: Int = 10) {
  def filteredCommits: Seq[Commit] = {
    val allCommits = input.listCommits
    startCommit.map(start => allCommits.dropWhile(x => x != start).tail).getOrElse(allCommits).take(take)
  }

  def read: Seq[WordCount] = {
    def wc(lines: Seq[LineOfText]): Seq[(Word, Int)] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size).toSeq
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

