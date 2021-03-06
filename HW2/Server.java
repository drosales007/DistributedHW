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
    public static boolean my_turn = false;
    public static int[] clock = new int[2];

    public static int ID;
    public static String[] CLIENT_REQUESTS = {"reserve", "bookSeat", "search", "delete"};

    public static void IncrementClock(){
        clock[0]++;
    }

    public static void UpdateClock(String c){
        String[] clk = c.split(",");
        clock[0] = Math.max(Integer.parseInt(clk[0]), clock[0]);
    }

    public static boolean CheckQueue(){
        if (q.getNextNode()==ID){
            return true;
        } else {
            return false;
        }
    }

    public static void ReleaseCS(String[][] servers, String[] seats){
        // Broadcast a message releaseing the critical section
        q.dequeue(clock);
        for (int i=1; i<numSrvs + 1; i++){
            if (i!=ID){
                IncrementClock();
                BCThread t = new BCThread(servers, i, "release", ID, seats, clock);
                System.out.println("Sending release to server: " + i + " " + clock[0] + "," + clock[1]);
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

    public static void RequestCS(String[][] servers){
        // Insert your request into the queue
        q.enqueue(clock);
        // Broadcast a message to request the critical section
        for (int i=1; i<numSrvs + 1; i++) {
            if (i!=ID){
                IncrementClock();
                BCThread t = new BCThread(servers, i, "request", ID, clock);
                System.out.println("Sending request to server: " + i + " " + clock[0] + "," + clock[1]);
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

    public static String[] Sync(String[][] servers){
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
                    msg = "sync ServerID: " + ID + " " + clock[0] + "," + clock[1];
                    out.println(msg);
                    while(!in.ready()){
                        // wait
                    }
                    msg = in.readLine();
                    String[] m = msg.split(" ");
                    String[] s = m[0].split(",");
                    // Set the seating
                    for (int j=0; j<s.length; j++){
                        seating[j] = s[j];
                    }
                    // We only need to set the queue if it is not empty
                    if (!m[1].equals("Empty")){
                        String[] que = m[1].split(",");
                        // Set the queue
                        for (int j=0; j<que.length; j++){
                            String[] vals = que[j].split(":");
                            int[] c = {Integer.parseInt(vals[0]), Integer.parseInt(vals[1])};
                            q.enqueue(c);
                        }
                    }
                    // Set the clock
                    clock[0] = Integer.parseInt(m[2]);
                    clock[1] = ID;
                    System.out.println(msg);
                    System.out.println("Reservations have been synchronized");
                    return seating;
                } catch(Exception e) {
                    System.out.print("Could not synchronize reservations with ServerID: " + i + "\n");
                }
            }
        }
        String[] x = new String[servers.length];
        return x;
    }

    public static String Search(String[] seats, String name){
        // Handle search requests
        String msg = "No reservation found for " + name;
        // Search through the seating to see if a reservation is there
        for (int i=0; i<seats.length; i++){
            if (name.equals(seats[i])){
                msg = "Your seating reservation is for seat " + i;
                return msg;
            }
        }
        return msg;
    }

    public static String Delete(String[] seats, String name){
        // Handle delete requests
        String msg = "No reservation for " + name + " was found";
        for (int i=0; i<seats.length; i++){
            if (name.equals(seats[i])){
                seats[i] = "";
                if (soldOut){
                    soldOut = false;
                }
                sold--;
                msg = "Reservation for " + name + " has been deleted";
                return msg;
            }
        }
        return msg;
    }

    public static String Reserve(String[] seats, String name){
        // Handle reserve requests
        boolean prevRes = false;
        String msg = "Sold out - No seat available";
        // Check if seating is sold out
        if (!soldOut){
            // If seating is not sold out we search through the seating
            for (int i=0; i<seats.length; i++){
                // Check if there is a previous reservation
                if (seats[i].equals(name)){
                    prevRes = true;
                    msg = "Seat already booked against the name provided";
                    return msg;
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
                        return msg;
                    }
                }
            }
        }
        return msg;
    }

    public static String BookSeat(String[] seats, String name,
                                        String seatNum){
        // Handle bookSeat requests
        String msg = "Sold out - No seat available";
        int num = Integer.parseInt(seatNum);
        // First we want to check that the seat number given is valid
        if (num>seats.length){
            msg = "Seat " + num + " is not available for reservation";
            return msg;
        }
        // If we are not sold out we need to check for existing reservations
        if (!soldOut){
            for (int i=0; i<seats.length; i++){
                if (name.equals(seats[i])){
                    msg = "Seat already booked against the name provided";
                    return msg;
                }
            }
            if (seats[num].equals("")){
                seats[num] = name;
                sold++;
                if (sold==seats.length){
                    soldOut = true;
                }
                msg = "Reservation for seat " + num + " has been booked for " + name;
                return msg;
            } else {
                msg = "Seat " + num + " is not available for reservation"; 
            }
        }
        return msg;
    }

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        int numSeat = sc.nextInt();

        ID = myID;
        numSrvs = numServer;
        clock[0] = 0;
        clock[1] = ID;

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

        // Synchronize with the servers
        String[] sync = Sync(servers);
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
                String[] clk;
                String[] data;
                String msg;
                String returnData = "Error processing the transaction. Please try again";
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
                    UpdateClock(data[3]);
                    IncrementClock();
                    returnData = "Acknowledge ServerID: " + myID + " " + clock[0] + "," + clock[1];
                    out.println(returnData);
                    out.close();
                    sock.close();
                    srv.close();
                } else if(data[0].equals("ReleaseCS")){
                    UpdateClock(data[4]);
                    IncrementClock();
                    String[] s = msg.split(" ")[3].split(",");
                    for (int i=0; i<s.length; i++){
                        seating[i] = s[i];
                    }
                    out.close();
                    sock.close();
                    srv.close();
                } else if (Arrays.asList(CLIENT_REQUESTS).contains(data[0])){
                    IncrementClock();
                    RequestCS(servers);
                    if (data[0].equals("reserve")){
                        while (!my_turn){
                            // Check queue to see if it is your turn
                            my_turn = CheckQueue();
                        }
                        // Send appropriate response to client
                        returnData = Reserve(seating, data[1]);
                    } else if (data[0].equals("bookSeat")) {
                        while (!my_turn){
                            // Check queue to see if it is your turn
                            my_turn = CheckQueue();
                        }
                        // Send appropriate response to client
                        returnData = BookSeat(seating, data[1], data[2]);
                    } else if (data[0].equals("search")) {
                        while (!my_turn){
                            // Check queue to see if it is your turn
                            my_turn = CheckQueue();
                        }
                        // Send appropriate response to client
                        returnData = Search(seating, data[1]);
                    } else if (data[0].equals("delete")) {
                        while (!my_turn){
                            // Check queue to see if it is your turn
                            my_turn = CheckQueue();
                        }
                        // Send appropriate response to client
                        returnData = Delete(seating, data[1]);
                    }
                    // Send the response and close the connection
                    out.println(returnData);
                    ReleaseCS(servers, seating);
                    my_turn = false;
                    out.close();
                    sock.close();
                    srv.close();
                } else if(data[0].equals("sync")){
                    UpdateClock(data[3]);
                    IncrementClock();
                    String s = seating[0];
                    for (int i=1; i<numSeat; i++){
                        s = s + "," + seating[i];
                    }
                    String que = q.printQueue();
                    String c = "" + clock[0]; 
                    returnData = s + " " + que + " " + c + " " + clock[0] + "," + clock[1];
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