import java.net.*;
import java.io.*;

public class BCThread extends Thread{

	int myID;
	int port;
	int servNum;
	String action;
	int[] c;
	String[] seating;
	String[][] servers;
	
	public BCThread(String[][] servers, int servNum, String action, int myID, int[] clk){
		this.servers = servers;
		this.servNum = servNum;
		this.action = action;
		this.port = Integer.parseInt("4330" + servNum);
		this.myID = myID;
		this.c = clk;
	}

	public BCThread(String[][] servers, int servNum, String action, int myID, String[] seats, int[] clk){
		this.servers = servers;
		this.servNum = servNum;
		this.action = action;
		this.port = Integer.parseInt("4330" + servNum);
		this.myID = myID;
		this.seating = seats;
		this.c = clk;
	}

	public void run(){
		try {
			String msg;
			Socket sock = new Socket(servers[servNum][0], Integer.parseInt(servers[servNum][1]));
	        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
	        if (action.equals("request")){
	        	BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	        	msg = "RequestCS ServerID: " + myID + " " + c[0] + "," + c[1];
	        	out.println(msg);
	        	while (!in.ready()); {
	                //wait
	            }
	            msg = in.readLine();
	            System.out.println(in.readLine());
	            in.close();
	        } else if (action.equals("release")){
	        	String s = "" + seating[0];
	        	for (int i=1; i<seating.length; i++){
	        		s = s + "," + seating[i];
	        	}
	        	msg = "ReleaseCS ServerID: " + myID + " " + s + " " + c[0] + "," + c[1];
	        	out.println(msg);
	        }
	        out.close();
	        sock.close();
	    } catch (Exception e) {
        }
	}
}