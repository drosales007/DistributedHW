import java.util.Scanner;
import java.util.*;
import java.net.*;
import java.io.*;

public class WorkerThread extends Thread{

	public static void IncrementClock(){
        Server.clock[0]++;
    }

    public static void UpdateClock(String c){
        String[] clk = c.split(",");
        Server.clock[0] = Math.max(Integer.parseInt(clk[0]), Server.clock[0]);
    }

    public static boolean CheckQueue(){
        if (Server.q.getNextNode()==Server.ID){
            return true;
        } else {
            return false;
        }
    }

	public static String Search(String name){
        // Handle search requests
        String msg = "No reservation found for " + name;
        // Search through the seating to see if a reservation is there
        for (int i=0; i<Server.seating.length; i++){
            if (name.equals(Server.seating[i])){
                msg = "Your seating reservation is for seat " + i;
                return msg;
            }
        }
        return msg;
    }

    public static String Delete(String name){
        // Handle delete requests
        String msg = "No reservation for " + name + " was found";
        for (int i=0; i<Server.seating.length; i++){
            if (name.equals(Server.seating[i])){
                Server.seating[i] = "";
                if (Server.soldOut){
                    Server.soldOut = false;
                }
                Server.sold--;
                msg = "Reservation for " + name + " has been deleted";
                return msg;
            }
        }
        return msg;
    }

    public static String Reserve(String name){
        // Handle reserve requests
        boolean prevRes = false;
        String msg = "Sold out - No seat available";
        // Check if seating is sold out
        if (!Server.soldOut){
            // If seating is not sold out we search through the seating
            for (int i=0; i<Server.seating.length; i++){
                // Check if there is a previous reservation
                if (Server.seating[i].equals(name)){
                    prevRes = true;
                    msg = "Seat already booked against the name provided";
                    return msg;
                }
            }
            // If there was no previous reservation, find an empty seat to reserve
            if (!prevRes){
                for (int i=0; i<Server.seating.length; i++){
                    if (Server.seating[i].equals("")){
                        Server.seating[i] = name;
                        // Increment the number of seat sold
                        Server.sold++;
                        // If seat sold is equal to seat available, sold out
                        if (Server.sold == Server.seating.length){
                            Server.soldOut = true;
                        }
                        msg = "Seat assigned to you is " + i;
                        return msg;
                    }
                }
            }
        }
        return msg;
    }

    public static String BookSeat(String name,
                                  String seatNum){
        // Handle bookSeat requests
        String msg = "Sold out - No seat available";
        int num = Integer.parseInt(seatNum);
        // First we want to check that the seat number given is valid
        if (num>Server.seating.length){
            msg = "Seat " + num + " is not available for reservation";
            return msg;
        }
        // If we are not sold out we need to check for existing reservations
        if (!Server.soldOut){
            for (int i=0; i<Server.seating.length; i++){
                if (name.equals(Server.seating[i])){
                    msg = "Seat already booked against the name provided";
                    return msg;
                }
            }
            if (Server.seating[num].equals("")){
                Server.seating[num] = name;
                Server.sold++;
                if (Server.sold==Server.seating.length){
                    Server.soldOut = true;
                }
                msg = "Reservation for seat " + num + " has been booked for " + name;
                return msg;
            } else {
                msg = "Seat " + num + " is not available for reservation"; 
            }
        }
        return msg;
    }

    public static void ReleaseCS(String[][] servers, String[] seats){
        // Broadcast a message releaseing the critical section
        Server.q.dequeue(Server.clock);
        for (int i=1; i<Server.numSrvs + 1; i++){
            if (i!=Server.ID){
                IncrementClock();
                BCThread t = new BCThread(servers, i, "release", Server.ID, seats, Server.clock);
                System.out.println("Sending release to server: " + i + " " + Server.clock[0] + "," + Server.clock[1]);
                t.start();
                try {
                    t.join();
                } catch (Exception e) {
                    System.out.println("There was an exception in join");
                    e.printStackTrace();
                }
            }
        }
    }

	public void run(){
		try{
			while (true){
				String[] clk;
            	String[] data;
                String msg;
                String returnData = "Error processing the transaction. Please try again";
                int port = Integer.parseInt(Server.threadInfo[1]);
                ServerSocket srv = new ServerSocket(port);
                Socket sock = srv.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("Server Connected");
                while (!in.ready()){
                    // wait
                }
                msg = in.readLine();
                System.out.println(msg);
                data = msg.split(" ");
            	IncrementClock();
                if (data[0].equals("reserve")){
                    while (!Server.my_turn){
                        // Check queue to see if it is your turn
                        Server.my_turn = CheckQueue();
                    }
                    // Send appropriate response to client
                    int[] n = new int[2];
                	n[0] = Integer.parseInt(data[2].split(",")[0]);
                	n[1] = Integer.parseInt(data[2].split(",")[1]);
                    Server.q.dequeue(n);
                    returnData = Reserve(data[1]);
                } else if (data[0].equals("bookSeat")) {
                    while (!Server.my_turn){
                        // Check queue to see if it is your turn
                        Server.my_turn = CheckQueue();
                    }
                    // Send appropriate response to client
                    int[] n = new int[2];
                	n[0] = Integer.parseInt(data[3].split(",")[0]);
                	n[1] = Integer.parseInt(data[3].split(",")[1]);
                    Server.q.dequeue(n);
                    returnData = BookSeat(data[1], data[2]);
                } else if (data[0].equals("search")) {
                    while (!Server.my_turn){
                        // Check queue to see if it is your turn
                        Server.my_turn = CheckQueue();
                    }
                    // Send appropriate response to client
                    int[] n = new int[2];
                	n[0] = Integer.parseInt(data[2].split(",")[0]);
                	n[1] = Integer.parseInt(data[2].split(",")[1]);
                    Server.q.dequeue(n);
                    returnData = Search(data[1]);
                } else if (data[0].equals("delete")) {
                    while (!Server.my_turn){
                        // Check queue to see if it is your turn
                        Server.my_turn = CheckQueue();
                    }
                    // Send appropriate response to client
                    int[] n = new int[2];
                	n[0] = Integer.parseInt(data[2].split(",")[0]);
                	n[1] = Integer.parseInt(data[2].split(",")[1]);
                    Server.q.dequeue(n);
                    returnData = Delete(data[1]);
                }
                // Send the response and close the connection
                out.println(returnData);
                ReleaseCS(Server.servers, Server.seating);
                Server.my_turn = false;
                out.close();
                sock.close();
                srv.close();
            }
		} catch(Exception e) {
            System.out.print("Error handling the request\n" + e);
        }
	}
}