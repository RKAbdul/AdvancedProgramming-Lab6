import lab6.*;
import java.util.concurrent.*;
import scala.util.Random
class Nest(B:Int) {
  //CS-baby i: cannot take a bug if the plate is empty
  //CS-father/mother: cannot put a bug if the plate is full

  @volatile private var numAvailBugs = 0

  private var mutex = new Semaphore(1, true);
  private var isSpace = new Semaphore(1, true);
  private var bugsLeft = new Semaphore(0, true);


  def takeBug(i: Int) = {
    // Baby i takes a bug from the plate
    bugsLeft.acquire();
    mutex.acquire();
    numAvailBugs -= 1;
    if (numAvailBugs == B - 1) isSpace.release()
    if (numAvailBugs > 0) bugsLeft.release()
    log(s"Baby $i takes a bug. Remaining $numAvailBugs bugs")
    mutex.release();
  }

  def putBug(i: Int) = {
  // The father/mother puts a bug on the plate (0=father, 1=mother)
    isSpace.acquire();
    mutex.acquire();
    numAvailBugs += 1 ;
    if (numAvailBugs > 0) bugsLeft.release();
    if (numAvailBugs < B) isSpace.release();
    log(s"Father $i puts a bug. Remaining $numAvailBugs bugs")
   mutex.release();
  }
}

object Exercise7 {

  def main(args:Array[String]):Unit = {
    val N = 10
    val Nest = new Nest(5)
    val baby = new Array[Thread](N)
    for (i<-baby.indices)
      baby(i) = thread{
        while (true){
          Nest.takeBug(i)
          Thread.sleep(Random.nextInt(600))
        }
      }
    val father = new Array[Thread](2)
    for (i<-father.indices){
      father(i) = thread{
        while (true){
          Thread.sleep(Random.nextInt(100))
          Nest.putBug(i)
        }
      }
    }
  }

}
