package pl.bka

trait CloseSupport {
  def closeAfterRun[A <: {def close() : Unit}, B](param: A)(run: A => B): B =
    try {
      run(param)
    } finally {
      param.close()
    }
}

