package object lab6 {

  def thread(body: =>Unit):Thread = {
    val t = new Thread {
      override def run = body
    }
    t.start()
    t
  }

  def parallel[A,B](a: =>A,b: =>B):(A,B) = {
    var va:A = null.asInstanceOf[A]
    var vb:B = null.asInstanceOf[B]
    val h1 = thread{
      va = a
    }
    val h2 = thread{
      vb = b
    }
    h1.join()
    h2.join()
    (va,vb)
  }
  def log(msg:String) =
    println(s"${Thread.currentThread().getName}: $msg")
}