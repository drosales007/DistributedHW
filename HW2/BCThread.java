import java.net.*;
import java.io.*;

public class BCThread extends Thread{

	int myID;
	int port;
	int servNum;
	String action;
	String[][] servers;
	
	public BCThread(String[][] servers, int servNum, String action, int myID){
		this.servers = servers;
		this.servNum = servNum;
		this.action = action;
		this.port = Integer.parseInt("4330" + servNum);
		this.myID = myID;
	}

	public void run(){
		try {
			String msg;
			Socket sock = new Socket(servers[servNum][0], Integer.parseInt(servers[servNum][1]));
	        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
	        if (action.equals("request")){
	        	BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	        	msg = "RequestCS ServerID: " + myID;
	        	out.println(msg);
	        	while (!in.ready()); {
	                //wait
	            }
	            msg = in.readLine();
	            System.out.println(in.readLine());
	            in.close();
	        } else if (action.equals("release")){
	        	msg = "ReleaseCS ServerID: " + myID;
	        	out.println(msg);
	        }
	        out.close();
	        sock.close();
	    } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
	}
}