package app.queue;

//Creating LinkedList class that implements Queue with generic type
public class LinkedList<R> implements Queue<R>{
	//creating head and tail of queue
	Node<R> head, tail;
	
	//Defining peek() method according to Queue.java. Returns value of head in queue.
	public R peek() {
		//if queue is empty and peek() is called, IllegalStateException is thrown
		if(head == null)
			return null;
		return head.value;
	}
	
	//Defining bool offer(Generic) according to Queue.java. Adds one element to end of queue.
	public boolean offer(R x) {
		//if parameter passed is null
		if(x == null)
			return false;
		//temp variable created for storing passed value
		Node<R> temp = new Node<R>(x);
		//if queue is empty then both head and tail are assigned passed value
		if (head == null) {
			head = temp;
			tail = temp;
		} 
		//else only tail is assigned the value
		else {
			tail.next=temp;
			tail=temp;
		}
		return true;
	}
	
	//Defining poll() according to Queue.java. Removes head from queue and returns it.
	public R poll() {
		//if queue is empty
		if(head == null)
			return null;
		//head value stored in temp variable
		R temp = head.value;
		//if queue is null without head then tail is also null
		if(head.next == null) {
			tail = null;
		}
		//head is assigned the next value in queue
		head = head.next;
		return temp;
	}
}

