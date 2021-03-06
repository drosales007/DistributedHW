public class Queue{
	
	QNode head;

	public void enqueue(int[] value){
		// Adds a node to the queue
		// If head is null, there are no nodes in the queue
		QNode n1 = new QNode();
		n1.value = value;
		if (head == null){
			head = n1;
		} else {
			QNode n2 = head;
			while (n2.next != null){
				n2 = n2.next;
			}
			n2.next = n1;
		}
	}

	public boolean dequeue(int[] value){
		// Removes the specified node from the queue
		String x = printQueue();
		QNode n1;
		QNode n2;
		QNode prev = null;
		n1 = head;
		n2 = n1.next;
		if (n2==null){
			head = null;
		}
		while (n2!=null){
			if (n1.value[0]!=value[0]){
				prev = n1;
				n1 = n2;
				n2 = n2.next;
			} else if (n1.value[1]==value[1]){
				prev.next = n2;
				return true;
			}
		}
		return true;
	}

	public String printQueue(){
		// Prints the order of teh queue
		QNode n;
		String que = "";
		if (head != null){
			n = head;
			que = que + head.getValue();
			while (n.next != null){
				n = n.next;
				que = que + ", " + " " + n.getValue();
			}
		} else {
			que = "Empty";
		}
		return que;
	}

	public int getNextNode(){
		// Checks the queue and returns the process ID who is next
		QNode n1 = head;
		QNode next_node = n1;
		QNode n2;
		while (n1.next!=null){
			n2 = n1.next;
			if (n1.value[0]==n2.value[0]){
				if (n1.value[1]<n2.value[1]){
					next_node = n1;
				} else {
					next_node = n2;
				}
			} else if(n1.value[0]<n2.value[0]){
				next_node = n1;
			} else {
				next_node = n2;
			}
		}
		return next_node.value[1];
	}

	public class QNode{
	// A node in a queue
	
		int[] value;
		QNode next;

		public String getValue(){
			String v = "" + value[0] + ":" + value[1];
			return v;
		}
	}
}