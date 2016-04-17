package pl.bka

import org.joda.time.LocalDate
import org.scalatest._

class CounterSpec extends FlatSpec with Matchers {
  def relativeDate(daysFrom: Int) = new LocalDate(2015, 1, 1 + daysFrom).toDateTimeAtStartOfDay

  case object StubInput1 extends Input {
    def listCommits: Seq[Commit] = Seq(
      Commit(2, Hash("345kljsd"), relativeDate(1)),
      Commit(1, Hash("2kc98sdf"), relativeDate(0))
    )
    def checkout(hash: Hash) = hash match {
      case Hash("345kljsd") => Seq(LineOfText("abc abc"), LineOfText("eghegh"))
      case Hash("2kc98sdf") => Seq(LineOfText("abc abc"), LineOfText("abc abc"), LineOfText("wer ojk"))
    }
    def cleanup = ()
  }

  it should "filter commits correctly" in {
    Counter(StubInput1, None).filteredCommits.map(_.seqNum).toSet should be (Set(1, 2))
    Counter(StubInput1, Some(StubInput1.listCommits.head)).filteredCommits.map(_.seqNum).toSet should be (Set(1))
  }

  it should "correctly calculate wordcounts" in {
    Counter(StubInput1, None).read.toSet should be (Set(
      WordCount(Commit(1, Hash("2kc98sdf"), relativeDate(0)), Word("abc"), 4),
      WordCount(Commit(1, Hash("2kc98sdf"), relativeDate(0)), Word("wer"), 1),
      WordCount(Commit(1, Hash("2kc98sdf"), relativeDate(0)), Word("ojk"), 1),
      WordCount(Commit(2, Hash("345kljsd"), relativeDate(1)), Word("abc"), 2),
      WordCount(Commit(2, Hash("345kljsd"), relativeDate(1)), Word("eghegh"), 1)
    ))
  }
}
