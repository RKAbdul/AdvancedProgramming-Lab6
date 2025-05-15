import lab6.*

import java.util.concurrent.Semaphore
import scala.util.Random

class Cauldron(R:Int){
  //CS-cannibal i: cannot take a portion from the Cauldron, if empty
  //CS-cook: cannot cook a ner explorer until the Cauldron is empty


  @volatile private var numAvailablePortions = R // Initially full


  /*
    MUTEX - MANAGE THE ACCESS TO THE VARIABLE MUTEX
    COOK - MANAGES THE COOK

  HINT: GENERAL SEMAPHORES, A CAULDRON CAN BE A GENERAL SEMAPHORE

    TO REPRESENT A GENERAL SEMAPHORE WE ARE GONNA USE 2 BINARY SEMAPHORES:
      - MUTEX
      - CANNSEM
      - COOKSEM
*/

  private val mutex = new Semaphore(1, true)
  private val cannSem = new Semaphore(0, true) // SHOULD BE INTIALIZED WITH 0, REMEMBER HOW ACCQUIRE WORKS
  private val cookSem = new Semaphore(0, true)

  def takeAPortion(i:Int)={
    //cannibal i takes portion from the Cauldron
    mutex.acquire()

    if (numAvailablePortions == 0) then {
      cookSem.release()
      cannSem.acquire()
    }

    numAvailablePortions -= 1
    log(s"cannibal $i takes portion from the Cauldron. Remaining $numAvailablePortions portions.")
    mutex.release();
  }

  def dormir = {
    //cook waits for the Cauldron to be empty
    cookSem.acquire()
  }
  def fillCauldron = {
    
    log(s"The cook fills the Cauldron. Remaining $numAvailablePortions portions.")
  }
}
object Exercise8 {

  def main(args:Array[String]):Unit = {
    val NCan = 20
    val Cauldron = new Cauldron(5)
    val cannibal = new Array[Thread](NCan)
    for (i<-cannibal.indices)
      cannibal(i) = thread {
        while (true){
          Thread.sleep(Random.nextInt(500))
          Cauldron.takeAPortion(i)
        }
      }
      val cook = thread{
        while (true){
          Cauldron.dormir
          Thread.sleep(500)//cocinando
          Cauldron.fillCauldron
        }
      }
  }
}
