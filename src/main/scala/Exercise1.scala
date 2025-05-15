import java.util.concurrent.*
import scala.util.Random
import lab6.*;

object sampling {
  // CS-Sensor-i:  sensor i cannot sample again until the worker has
  // finished to process previous samples
  // CS-Worker: it cannot start its tasks until the three samples
  // are available

  @volatile private var samplesGathered = 0;

  private val sensorSem = new Semaphore(1);

  private val workerSem = new Semaphore(0);

  private val mutex = new Semaphore(1, true);

  def newSample(id:Int) = {
    sensorSem.acquire();
    mutex.acquire();

    samplesGathered += 1;

    if samplesGathered < 3 then sensorSem.release()

    log(s"Sensor $id stores its sample" )

    if samplesGathered == 3 then workerSem.release()

    mutex.release();
  }

  def readSamples() = {
    workerSem.acquire();
    log(s"Worker gathers the three samples")
  }

  def endWork()={
    mutex.acquire();


    samplesGathered = 0;
    sensorSem.release()

    log(s"Worker has finished its tasks")

    mutex.release();
  }
}

object Exercise1 {

  def main(args:Array[String]) =
    val sensor=new Array[Thread](3)

    for (i<-0 until sensor.length)
      sensor(i) = thread {
        while (true) {
          Thread.sleep(Random.nextInt(100)) // measuring
          sampling.newSample(i)
        }
      }

    val worker = thread {
      while (true){
        sampling.readSamples()
        Thread.sleep(Random.nextInt(100)) // processing its task
        sampling.endWork()
      }
    }
}
