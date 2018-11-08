package app.queue;

/**
 * queue interface implements FIFO design.
 */
public interface Queue<T>{

	/**
	 * add one element to the end of the queue
	 * @param t - none null element to be added
	 * @return true of element has been added
	 * @throws NullPointerException if t is null
	 */
	boolean offer(T t);
	
	/**
	 * remove the first element in the queue and return it
	 * @return removed head element
	 * @throws IllegalStateException if queue is empty
	 */
	T poll();
	
	/**
	 * return the first element in the queue, do not remove
	 * @return head element
	 * @throws IllegalStateException if queue is empty
	 */
	T peek();
}