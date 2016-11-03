import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple message passing simulation using a blocking queue. Using any IPC or socket library would offer the same methods.
 * @author Francois Gingras
 *
 */
public class MessagePassing {
	
	private BlockingQueue<String> messageQueue = null;
	
	public MessagePassing(int size) {
		messageQueue = new ArrayBlockingQueue<>(size, true); 
	}
	
	public void send(String message) throws InterruptedException {
		messageQueue.put(message);
	}
	
	public String receive() throws InterruptedException {
		return messageQueue.take();
	}
}
