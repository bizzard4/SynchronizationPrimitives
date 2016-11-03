/**
 * Producer-consumer problem solved in Java using message passing.
 * @author Francois Gingras
 *
 */
public class PCMessagePassingJava {
	
	// Production and consume count
	// Program will never end if it try to consume more than produce.
	public static int PRODUCE_COUNT = 10;
	public static int CONSUME_COUNT = 7;
	public static int BUFFER_SIZE = 5;
	
	// The message buffer is the message queue

	/**
	 * Main.
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// Message passing system
		MessagePassing message = new MessagePassing(BUFFER_SIZE);
		
		// Produce function
		Runnable prod = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < PRODUCE_COUNT; i++) {
						System.out.println("Producing " + i);
						message.send(Integer.toString(i)); // Block if full, will wait.
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
						int val = Integer.parseInt(message.receive()); // Will block until message can be received
						System.out.println("Consuming " + val);
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
	}

}
