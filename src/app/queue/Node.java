package app.queue;

//Defining Node class with generic type 
class Node<T>{
	//created variable with generic type T 
	T value;
	//next value from current Node
	Node<T> next;
	//constructor
	public Node(T t){
		value = t;
	}
}
