package pl.bka

case class Counter(input: Input) {
  def stream: Stream[Seq[WordCount]] = {
    def wc(lines: Seq[LineOfText]): Seq[(Word, Int)] =
      lines
        .flatMap(_.value.split("\\W+"))
        .filterNot(_ == "")
        .groupBy(Word)
        .mapValues(_.size).toSeq
    input.listCommits.toStream.map { commit =>
      wc(input.checkout(commit.hash)).map { case (word, count) =>
        WordCount(commit, word, count)
      }
    }
  }

  def stream(chunkSize: Int): ChunksStream = {
    def chunkStream(stream: Stream[Seq[WordCount]]): Stream[Chunk] =
      Chunk(stream.take(chunkSize).toList.flatten) #:: chunkStream(stream.drop(chunkSize))
    ChunksStream(chunkStream(stream), { x: Unit => input.cleanup })
  }

  def read(take: Int): List[WordCount] = stream.take(take).toList.flatten
}

