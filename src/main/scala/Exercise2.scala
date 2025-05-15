import java.util.concurrent.*
import scala.util.Random
import lab6.*

class Belt(n:Int) {
  // CS-Packager-i: waits for products of code i
  // CS-Robot: waits if there are n products in the belt (it is full)
  private val code = Array.fill(3)(0)
  @volatile private var total = 0

  private val waitRobot = new Semaphore(1) //CS- Colocalor

  private val packagerSem = Array.fill(3)(new Semaphore(0));

  private val mutex = new Semaphore(1, true);

  def takeProduct(p:Int)={

    packagerSem(p).acquire();
    mutex.acquire();

    code(p) -= 1;
    total += 1;

    if (code(p) > 0) packagerSem(p).release();
    if (code.sum == 4) waitRobot.release();

    log(s"Packager $p takes a product. Remaining ${code.mkString("[",",","]")}")

    mutex.release();
  }

  def newProduct(p:Int) = {
    waitRobot.acquire();
    mutex.acquire();

    code(p) += 1;

    if (code(p) == 1) packagerSem(p).release();
    if (code.sum < 5) waitRobot.release();

    log(s"Robot puts a product $p. Remaining ${code.mkString("[",",","]")}")

    log(s"Total of packaged products $total")

    mutex.release();
  }
}

object Exercise2 {
  def main(args:Array[String]) ={
    val belt = new Belt(6)
    val empaquetador = new Array[Thread](3)
    for (i <- empaquetador.indices)
      empaquetador(i) = thread {
        while (true) {
          belt.takeProduct(i)
          Thread.sleep(Random.nextInt(500)) //empaquetando
        }
      }

    val robot = thread {
      while (true) {
        Thread.sleep(Random.nextInt(100)) //recogiendo el producto
        belt.newProduct(Random.nextInt(3))
      }
    }
  }

}
