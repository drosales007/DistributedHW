public class Queue{
	
	QNode head;

	public void enqueue(int[] value){
		// Adds a node to the queue
		// If head is null, there are no nodes in the queue
		QNode node = new QNode();
		node.value = value;
		if (head == null){
			head = node;
		} else {
			QNode n = head;
			while (n.next != null){
				n = n.next;
			}
			n.next = node;
		}
	}

	public boolean dequeue(int[] value){
		// Removes the specified node from the queue
		QNode n = new QNode();
		n.value = value;
		QNode n2;
		n = head;
		n2 = null;
		while(!n.value.equals(value)){
			n2 = n;
			if (n.next == null){
				return false;
			}
			n = n.next;
		}
		n2.next = n.next;
		return true;
	}

	public void printQueue(){
		QNode n = new QNode();
		String q = "";
		if (head != null){
			n = head;
			q = q + head.getValue();
			while (n.next != null){
				n = n.next;
				q = q + ", " + " " + n.getValue();
			}
		}
		System.out.println(q);
	}

	public class QNode{
	// A node in a queue
	
		int[] value;
		QNode next;

		public String getValue(){
			String v = "" + value[0];
			for (int i=1; i<value.length; i++){
				v = v + " " + value[i];
			}
			return v;
		}
	}
}