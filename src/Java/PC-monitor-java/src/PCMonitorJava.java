public class PCMonitorJava {
	
	// Production and consume count
	// Program will never end if it try to consume more than produce.
	public static int PRODUCE_COUNT = 10;
	public static int CONSUME_COUNT = 7;
	public static int BUFFER_SIZE = 5;
	
	// Buffer variables
	public int itemCount = 0;
	public Integer[] buffer = new Integer[BUFFER_SIZE];
	
	// Monitor conditions
	private Object fullLock = new Object();
	private Object emptyLock = new Object();
	
	/**
	 * Produce an item.
	 * @throws InterruptedException 
	 */
	public void produce(int id) throws InterruptedException {
		while (itemCount == BUFFER_SIZE) { // Full buffer
			synchronized(fullLock) {
				fullLock.wait();
			}
		}
		
		synchronized(this) {
			System.out.println("Produced at index : " + itemCount);
			buffer[itemCount] = id;
			itemCount++;
		}
		
		if (itemCount == 1) {
			synchronized(emptyLock) {
				emptyLock.notify();
			}
		}
	}
	
	/**
	 * Consume an item.
	 * @throws InterruptedException 
	 */
	public void consume() throws InterruptedException {
		while (itemCount == 0) { // Empty buffer
			synchronized(emptyLock) {
				emptyLock.wait();
			}
		}
		
		synchronized(this) {
			itemCount--;
			System.out.println("Consuming at : " + itemCount);
			buffer[itemCount] = null;
		}
		
		if (itemCount == BUFFER_SIZE-1) {
			synchronized(fullLock) {
				fullLock.notify();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		
		PCMonitorJava pdMonitor = new PCMonitorJava();
		
		// Produce function
		Runnable prod = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < PRODUCE_COUNT; i++) {
						pdMonitor.produce(i);
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
						pdMonitor.consume();
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
			System.out.print(pdMonitor.buffer[i] + ";");
		}
	}

}
