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
			QNode n3;
			while(true){
				try {
					n3 = n2.next;
					n2 = n3;
				} catch (Exception e){
					n2.next = n1;
					break;
				}
			}
		}
	}

	public boolean dequeue(int[] value){
		// Removes the specified node from the queue
		String x = printQueue();
		QNode n1;
		QNode n2;
		QNode prev = null;
		n1 = head;
		try {
			n2 = head.next;
		} catch (Exception e){
			n2 = null;
		}
		while (n2!=null){
			if (n1.value[0]!=value[0]){
				prev = n1;
				n1 = n2;
				try {
					n2 = n2.next;
				} catch (Exception e){
					n2 = null;
				}
			} else if (n1.value[1]==value[1]){
				prev.next = n2;
				return true;
			}
		}
		head = null;
	
		return true;
	}

	public String printQueue(){
		// Prints the order of teh queue
		QNode n;
		String que = "";
		if (head != null){
			n = head;
			que = que + head.getValue();
			QNode n2 = head;
			QNode n3;
			while (true){
				try {
					n3 = n2.next;
					n2 = n3;
					que = que + ", " + " " + n2.getValue();
				} catch (Exception e){
					break;
				}
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
		QNode n2 = null;
		QNode n3;
		try {
			if (head.next!=null){
				n2 = head.next;
			}
		} catch (Exception e){
		}
		if (n2==null){
			return next_node.value[1];
		}
		while (true){
			if (next_node.value[0]==n2.value[0]){
				if (next_node.value[1]>n2.value[1]){
					next_node = n2;
				}
			} else if(next_node.value[0]>n2.value[0]){
				next_node = n2;
			}
			try {
				n3 = n2.next;
				n2 = n3;
			} catch (Exception e){
				return next_node.value[1];
			}
		}
	}

	public class QNode{
	// A node in a queue
	
		int[] value;
		QNode next = null;

		public String getValue(){
			String v = "" + value[0] + ":" + value[1];
			return v;
		}
	}
}