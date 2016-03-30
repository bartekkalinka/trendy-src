package pl.bka

import org.scalatest._

case object StubInput extends Input {
  def listCommits: Seq[Commit] = Seq(Commit(Hash("2kc98sdf")))
  def checkout(hash: Hash) = Seq(LineOfText("abcabc"), LineOfText("eghegh"))
  def cleanup = ()
}

class CounterSpec extends FlatSpec with Matchers {
  it should "correctly calculate wordcounts" in {
    Counter(StubInput).read() should be (Map(
      Commit(Hash("2kc98sdf")) -> Map(
        Word("abcabc") -> 1,
        Word("eghegh") -> 1
       )
    ))
  }
}
