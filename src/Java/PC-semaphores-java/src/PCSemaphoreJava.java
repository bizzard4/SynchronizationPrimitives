import java.util.concurrent.Semaphore;

/**
 * Producer-consumer problem solved in Java using Semaphores.
 * @author Francois Gingras
 *
 */
public class PCSemaphoreJava {
	
	// Production and consume count
	// Program will never end if it try to consume more than produce.
	public static int PRODUCE_COUNT = 10;
	public static int CONSUME_COUNT = 7;
	public static int BUFFER_SIZE = 5;
	
	// Buffer array, with current position
	public static int atIndex = 0;
	public static Integer[] buffer = new Integer[BUFFER_SIZE];
		
	/**
	 * Main.
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// Prepare buffer and initialize empty
		for (int i = 0; i < BUFFER_SIZE; i++) {
			buffer[i] = null;
		}
		
		// Prepare semaphore
		Semaphore mutex = new Semaphore(1);
		Semaphore fillCount = new Semaphore(BUFFER_SIZE, true);
		Semaphore emptyCount = new Semaphore(BUFFER_SIZE, true);
		fillCount.acquire(BUFFER_SIZE);
		
		// Produce function
		Runnable prod = new Runnable() {
			@Override
			public void run() {
				try {
					int count = 0; // Item
					for (int i = 0; i < PRODUCE_COUNT; i++) {
						count++; // Produce the item
						emptyCount.acquire();
						mutex.acquire();
						buffer[atIndex] = count;
						System.out.println("Produced at index : " + atIndex);
						atIndex++;	
						mutex.release();
						fillCount.release();
					}
				} catch (InterruptedException e) {
					System.err.println("Acquire exception " + e.getMessage());
				}
			}
		};
		
		// Consume function
		Runnable cons = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < CONSUME_COUNT; i++) {
						fillCount.acquire();
						mutex.acquire();
						atIndex--;
						System.out.println("Consuming at : " + atIndex);
						buffer[atIndex] = null;
						mutex.release();
						emptyCount.release();
					}
				} catch (InterruptedException e) {
					System.err.println("Acquire exception " + e.getMessage());
				}

			}
		};
		
		// Create and start threads
		Thread producer = new Thread(prod);
		Thread consumer =  new Thread(cons);
		producer.start();
		consumer.start();
		
		producer.join();
		consumer.join();
		
		// Show final state
		System.out.println("Done");
		for (int i = 0; i < BUFFER_SIZE; i++) {
			System.out.print(buffer[i] + ";");
		}
	}
}
