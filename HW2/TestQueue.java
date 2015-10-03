public class TestQueue{
	
	public static void main(String[] args){

		Queue q = new Queue();
		int[] n = {0,1};
		q.enqueue(n);
		int[] m = {2,3};
		q.enqueue(m);
		int[] l = {4,5};
		q.enqueue(l);
		int[] k = {6,7};
		q.enqueue(k);
		q.printQueue();
		q.dequeue(m);
		q.dequeue(l);
		q.printQueue();
	}
}