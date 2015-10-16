import java.util.Scanner;
import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Server {

    public static boolean ready = false;
    public static int acks = 0;
    public static int sold = 0;
    public static boolean soldOut = false;
    public static int numSrvs;
    public static String[] seating;
    public static Queue q = new Queue();
    public static boolean my_turn = false;
    public static int[] clock = new int[2];
    public static String[][] servers;

    public static String[] threadInfo = new String[2];
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
        String[][] srvs = new String[numServer+1][2];
        srvs[0][0] = "";
        srvs[0][1] = "";

        servers = srvs;

        // Create an array to keep track of seating
        seating = new String[numSeat];
        for (int i=0; i<seating.length; i++){
            seating[i] = "";
        }
        sc.nextLine();

        for (int i = 1; i < numServer + 1; i++) {
            String server = sc.nextLine();
            String[] servInfo = server.split(":");
            srvs[i][0] = servInfo[0];
            srvs[i][1] = servInfo[1];
        }

        for (int i=0; i<numServer + 1; i++){
            System.out.println("ID: " + i + ", IP: " + srvs[i][0] + ", Port: " + srvs[i][1]);
        }

        // Synchronize with the servers
        String[] sync = Sync(srvs);
        for (int i=0; i<sync.length; i++){
            if (sync[i] == null){
                seating[i] = "";
            } else {
                seating[i] = sync[i];
            }
        }

        // Spawn the worker thread
        // TODO: Start a thread that will queue and dequeue tasks
        int port = Integer.parseInt(srvs[ID][1]) + 50;
        threadInfo[0] = "127.0.0.1";
        threadInfo[1] = "" + port;
        WorkerThread wt = new WorkerThread();
        wt.start();

        // Handle requests from clients
        try{
            while (true){
                String[] clk;
                String[] data;
                String msg;
                String returnData = "Error processing the transaction. Please try again";
                ServerSocket srv = new ServerSocket(Integer.parseInt(srvs[myID][1]));
                Socket sock = srv.accept();
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                out.println("Server Connected 1");
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                while (!in.ready()){
                    // wait
                }
                msg = in.readLine();
                System.out.println(msg);
                data = msg.split(" ");
                if (data[0].equals("RequestCS")){
                    int[] n = new int[2];
                    n[0] = Integer.parseInt(data[3].split(",")[0]);
                    n[1] = Integer.parseInt(data[3].split(",")[1]);
                    q.enqueue(n);
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
                    RequestCS(srvs);
                    q.enqueue(clock);
                    out.println(threadInfo[0] + " " + threadInfo[1] + " " + clock[0] + "," + clock[1]);
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