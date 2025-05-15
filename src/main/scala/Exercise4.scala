import java.util.concurrent.*
import scala.util.Random
import lab6.*;

class Car(maxCapacity:Int) extends Thread {
  //CS-passenger1: if the Car is full, a passenger cannot board on it until the trip has finished
  // and all the car's passenger have get out
  //CS-passenger2: a passenger on the Car cannot get out until the trip has finished
  //CS-Car: the Car waits until C passengers are onboard to start a trip
  @volatile private var numPas = 0
  private var inputDoor = new Semaphore(1, true);
  private var outputDoor = new Semaphore(0, true);
  private var driver = new Semaphore(0, true);
  private var mutex = new Semaphore(1, true);

  def newTrip(id:Int)={
    // the passenger id wants to use the roller coaster
    inputDoor.acquire();
    mutex.acquire();
    numPas += 1;

    if (numPas < maxCapacity) inputDoor.release();
    if (numPas == maxCapacity) driver.release();

    log(s"The passenger $id boards on the Car. There are $numPas passengers.")
    mutex.release();

    outputDoor.acquire();
    mutex.acquire();
    numPas -= 1;

    if (numPas > 0) outputDoor.release();
    if (numPas == 0) inputDoor.release()
    log(s"The passenger $id gets out the Car. There are $numPas passengers.")

    mutex.release();
  }

  def waitsForFull = {
    // The Car waits to be full to start a trip
    driver.acquire();
    log(s"        Car full!!! Let's begin the trip....")
  }

  def endTrip = {
    outputDoor.release();
    log(s"        End of trip... :-(")
  }

  override def run = {
    while (true){
      waitsForFull
      Thread.sleep(Random.nextInt(Random.nextInt(500))) // The Car performs a trip
      endTrip
    }
  }
}
object Exercise4 {
  def main(args:Array[String])=
    val Car = new Car(5)
    val passenger = new Array[Thread](12)
    Car.start()
    for (i<-0 until passenger.length)
      passenger(i) = thread {
        while (true)
          Thread.sleep(Random.nextInt(500))
          Car.newTrip(i)
      }
}
