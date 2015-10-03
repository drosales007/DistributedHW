public class Queue{
	
	public static Node head;

	public static void addNode(Node node){
		// Adds a node to the queue
		if (head == null){
			head = node;
			System.out.println("fffff" + head.value);
			System.out.println("fffff" + head.next);
		} else {
			Node n = head;
			System.out.println("else");
			System.out.println("00000" + head.next);
			if(n.next != null){
				System.out.println("-----" + n.value);
				System.out.println("kkkkk" + n.next.value);
				n = n.next;
			}
			n.next = node;
			System.out.println("fffff" + head.next.value);
			System.out.println("fffff" + head.next.next.value);
		}
	}

	public static void removeNode(Node node){
		Node n = node;
		Node n2;
		n = head;
		n2 = null;
		while(n.value != node.value){
			n2 = n;
			n = n.next;
		}
		n2.next = n.next;
	}
}