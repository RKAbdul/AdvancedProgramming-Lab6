import java.util.concurrent.*
import lab6.*;
import scala.util.Random

object waterManager {
  //CS-Hyd1: The hydrogen wants to create a molecule. Waits if there are already hydrogens
  //CS-Hyd2: An hydrogen should wait the other two atoms to create a molecule
  //CS-Ox1: The oxygen wants to create a molecule. Waits if there are already any oxygen
  //CS-Ox2: The oxygen should wait the other two atoms to create a molecule

  private val OInputDoor = new Semaphore(1, true);
  private val HInputDoor = new Semaphore(1, true);
  private val exitDoor = new Semaphore(0, true);
  private val mutex = new Semaphore(1, true);

  @volatile var numH = 0;
  @volatile var numO = 0;

  def oxygen(id:Int) = {
    // The oxygen wants to create a molecule

    log(s"oxygen $id wants to create a molecule")

    //log(s"      Molecule created!!!")
  }

  def hydrogen(id:Int)={
    // The hydrogen wants to create a molecule
    HInputDoor.acquire();
    mutex.acquire();

    numH += 1;

    if (numH < 2) HInputDoor.release();
    log(s"hydrogen $id wants to create a molecule")
    if (numH + numO == 3) {
      log(s"      Molecule created!!!");
      exitDoor.release();
    } else {
      mutex.release();
      exitDoor.acquire();
      mutex.acquire();
    }

    numH-=1;
    if (numH + numO > 0) {
      exitDoor.release();
    } else if (numH + numO == 0) {
      HInputDoor.release();
      OInputDoor.release();
    }

    mutex.release();
  }
}
object Exercise5 {

  def main(args:Array[String]) =
    val N = 5
    val hydrogen = new Array[Thread](2*N)
    for (i<-0 until hydrogen.length)
      hydrogen(i) = thread {
        Thread.sleep(Random.nextInt(500))
        waterManager.hydrogen(i)
      }
    val oxigeno = new Array[Thread](N)
    for(i <- 0 until oxigeno.length)
      oxigeno(i) = thread {
        Thread.sleep(Random.nextInt(500))
        waterManager.oxygen(i)
      }
    hydrogen.foreach(_.join())
    oxigeno.foreach(_.join())
    log("End of Program")
}
