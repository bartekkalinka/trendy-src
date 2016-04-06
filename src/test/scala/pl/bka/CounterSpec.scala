package pl.bka

import org.scalatest._

case object StubInput extends Input {
  def listCommits: Seq[Commit] = Seq(Commit(1, Hash("2kc98sdf")))
  def checkout(hash: Hash) = Seq(LineOfText("abcabc"), LineOfText("eghegh"))
  def cleanup = ()
}

class CounterSpec extends FlatSpec with Matchers {
  it should "correctly calculate wordcounts" in {
    Counter(StubInput).read() should be (Seq(
      WordCount(Commit(1, Hash("2kc98sdf")), Word("abcabc"), 1),
      WordCount(Commit(1, Hash("2kc98sdf")), Word("eghegh"), 1)
    ))
  }
}
