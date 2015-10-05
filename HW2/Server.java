import java.util.Scanner;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Server {

    public static int sold = 0;
    public static boolean soldOut = false;
    public static int numSrvs;
    public static String[] seating;
    public static Queue q = new Queue();

    public static int ID;
    public static String[] CLIENT_REQUESTS = {"reserve", "bookSeat", "search", "delete"};

    public static void SendAck(){
        // Send an acknowledgement when a request comes in
    }

    public static void ReceiveAck(){
        // Handles an acknowlegment
    }

    public static void ReleaseCS(String[][] servers, String[] seats){
        // Broadcast a message releaseing the critical section
        for (int i=1; i<numSrvs + 1; i++){
            if (i!=ID){
                BCThread t = new BCThread(servers, i, "release", ID, seats);
                System.out.println("Sending release to server: " + i);
                t.start();
                try {
                    t.join();
                } catch (Exception e) {
                    System.out.println("There was an exception in join.");
                    e.printStackTrace();
                }
            }
        }
    }

    public static void RequestCS(String[][] servers){
        // Broadcast a message to request the critical section
        for (int i=1; i<numSrvs + 1; i++) {
            if (i!=ID){
                BCThread t = new BCThread(servers, i, "request", ID);
                t.start();
                try {
                    t.join();
                } catch (Exception e) {
                    System.out.println("There was an exception in join.");
                    e.printStackTrace();
                }
            }
        }
    }

    public static String[] SyncSeating(String[][] servers){
        // Synchronize the reservation list with the other servers
        for (int i=1; i<numSrvs+1; i++){
            if (i!=ID){
                try {
                    String msg;
                    Socket sock = new Socket(servers[i][0], Integer.parseInt(servers[i][1]));
                    PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    while(!in.ready()){
                        // wait
                    }
                    msg = in.readLine();
                    System.out.println(msg);
                    msg = "sync ServerID: " + ID;
                    out.println(msg);
                    while(!in.ready()){
                        // wait
                    }
                    msg = in.readLine();
                    String[] seating = msg.split(",");
                    /*for (int j=0; j<seating.length; j++){
                        if (seating[j] == null){
                            seating[j] = "";
                        }
                        System.out.println(seating[j]);
                    }*/
                    System.out.println("Reservations have been synchronized.");
                    return seating;
                } catch(Exception e) {
                    System.out.print("Could not synchronize reservations with ServerID: " + i + "\n");
                }
            }
        }
        String[] x = new String[servers.length];
        return x;
    }

    public String Search(String[] seats, String name){
        // Handle search requests
        return "";
    }

    public String Delete(String[] seats, String name){
        // Handle delete requests
        return "";
    }

    public static String Reserve(String[] seats, String name){
        // Handle reserve requests
        boolean prevRes = false;
        String msg = "";
        // Check if seating is sold out
        if (!soldOut){
            // If seating is not sold out we search through the seating
            for (int i=0; i<seats.length; i++){
                // Check if there is a previous reservation
                if (seats[i].equals(name)){
                    prevRes = true;
                    msg = "Seat already booked against the name provided";
                }
            }
            // If there was no previous reservation, find an empty seat to reserve
            if (!prevRes){
                for (int i=0; i<seats.length; i++){
                    if (seats[i].equals("")){
                        seats[i] = name;
                        // Increment the number of seats sold
                        sold++;
                        // If seats sold is equal to seats available, sold out
                        if (sold == seats.length){
                            soldOut = true;
                        }
                        msg = "Seat assigned to you is " + i;
                        break;
                    }
                }
            }
        } else {
            msg = "Sold out - No seat available";
        }
        return msg;
    }

    public String BookSeat(String[] seats, String name,
                                        String seatNum){
        // Handle bookSeat requests
        return "";
    }

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        int numSeat = sc.nextInt();

        ID = myID;
        numSrvs = numServer;

        // Create an array to keep track of all server ips
        String[][] servers = new String[numServer+1][2];
        servers[0][0] = "";
        servers[0][1] = "";

        // Create an array to keep track of seating
        seating = new String[numSeat];
        for (int i=0; i<seating.length; i++){
            seating[i] = "";
        }
        sc.nextLine();

        for (int i = 1; i < numServer + 1; i++) {
            String server = sc.nextLine();
            String[] servInfo = server.split(":");
            servers[i][0] = servInfo[0];
            servers[i][1] = servInfo[1];
        }

        for (int i=0; i<numServer + 1; i++){
            System.out.println("ID: " + i + ", IP: " + servers[i][0] + ", Port: " + servers[i][1]);
        }

        // Synchronize the reservation list on startup
        String[] sync = SyncSeating(servers);
        for (int i=0; i<sync.length; i++){
            if (sync[i] == null){
                seating[i] = "";
            } else {
                seating[i] = sync[i];
            }
        }

        // Handle requests from clients
        try{
            while (true){
                String[] data;
                String msg;
                String returnData = "Error processing the transaction. Please try again.";
                ServerSocket srv = new ServerSocket(Integer.parseInt(servers[myID][1]));
                Socket sock = srv.accept();
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("Server Connected");
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                while (!in.ready()){
                    // wait
                }
                msg = in.readLine();
                System.out.println(msg);
                data = msg.split(" ");
                if (data[0].equals("RequestCS")){
                    returnData = "Acknowledge ServerID: " + myID;
                    out.println(returnData);
                    out.close();
                    sock.close();
                    srv.close();
                } else if(data[0].equals("ReleaseCS")){
                    String[] s = msg.split(" ")[3].split(",");
                    for (int i=0; i<s.length; i++){
                        seating[i] = s[i];
                    }
                    out.close();
                    sock.close();
                    srv.close();
                } else if (Arrays.asList(CLIENT_REQUESTS).contains(data[0])){
                    RequestCS(servers);
                    if (data[0].equals("reserve")){
                        // Send appropriate response to client
                        returnData = Reserve(seating, data[1]);
                    } else if (data[0].equals("bookSeat")) {
                        // Send appropriate response to client
                    } else if (data[0].equals("search")) {
                        // Send appropriate response to client
                    } else if (data[0].equals("delete")) {
                        // Send appropriate response to client
                    }
                    // Send the response and close the connection
                    out.println(returnData);
                    ReleaseCS(servers, seating);
                    out.close();
                    sock.close();
                    srv.close();
                } else if(data[0].equals("sync")){
                    returnData = seating[0];
                    for (int i=1; i<seating.length; i++){
                        returnData = returnData + "," + seating[i];
                    }
                    out.println(returnData);
                    out.close();
                    sock.close();
                    srv.close();
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch(Exception e) {
            System.out.print("Error handling the request");
        }
    }
}