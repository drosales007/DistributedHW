public class A{
	
	public static void main(String[] args){

		Queue q = new Queue();
		Node n = new Node(1);
		System.out.println("xxxxx");
		q.addNode(n);
		n = new Node(2);
		System.out.println("aaaaa");
		q.addNode(n);
		/*n = new Node(3);
		System.out.println("bbbbb");
		q.addNode(n);
		n = new Node(4);
		System.out.println("ccccc");
		q.addNode(n);
		n = new Node(5);
		System.out.println("ddddd");
		q.addNode(n);
		System.out.println("-----");
		n = new Node(6);
		q.addNode(n);
		n = q.head;
		while (n.next != null){
			System.out.println("-----" + n.value);
			n = n.next;
		}*/
	}
}