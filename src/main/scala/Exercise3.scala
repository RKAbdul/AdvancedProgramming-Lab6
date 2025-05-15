import scala.util.Random
import lab6.*

import java.util.concurrent.Semaphore

object toilette {
  // CS-customers: Wait if the cleaning team is in the toilette
  // CS-cleaningTeam: Waits if there are customers in the toilette
  @volatile private var numCustomers = 0

  // TO AVOID THREADS TO USE LOCAL COPIES OF VARIBALES , WE USE VOLATILE INSTEAD

  private val mutex = new Semaphore(1, true);
  private val cleaning = new Semaphore(1, true);

  def enterCustomer(id:Int)={
    mutex.acquire()
    numCustomers += 1
    if (numCustomers == 1) cleaning.acquire()
    log(s"Customer $id enters. Remaining $numCustomers customers.")
    mutex.release()
  }
  def exitCustomer(id:Int)={
    mutex.acquire()
    numCustomers -= 1
    if (numCustomers == 0) cleaning.release()
    log(s"Customer $id exits. Remaining $numCustomers customers.")
    mutex.release()
  }

  def enterCleaningTeam ={
    cleaning.acquire()
    log(s"        Enters the cleaning team.")
  }

  def exitCleaningTeam = {
    log(s"        Exits the cleaning team.")
    cleaning.release()
  }
}

object Exercise3 {

  def main(args:Array[String]) = {

    val customers = new Array[Thread](10)
    for (i<-0 until customers.length)
      customers(i) = thread {
        while (true){
          Thread.sleep(Random.nextInt(500))
          toilette.enterCustomer(i)
          Thread.sleep(Random.nextInt(50))
          toilette.exitCustomer(i)

        }
      }

    val cleaningTeam = thread {

      while (true){
        Thread.sleep(Random.nextInt(500))
        toilette.enterCleaningTeam
        Thread.sleep(Random.nextInt(100))
        toilette.exitCleaningTeam
      }
    }
  }
}
