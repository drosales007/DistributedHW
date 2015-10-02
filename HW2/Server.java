import java.util.Scanner;
import java.lang.*;
import java.net.*;
import java.io.*;

public class Server {

    public static int myID;
    public static int acks = 1;
    public static String[] seating;
    public static boolean requested = false;

    public static void SendAck(){
        // Send an acknowledgement when a request comes in
    }

    public static void ReleaseCS(){
        // Broadcast a message releaseing the critical section
    }

    public static void SendRequest(){
        // Send a request to access the critical section
    }

    public static void SyncSeating(){
        // Synchronize the reservation list with the other servers
    }

    public String Search(String[] seats, String name){
        // Handle search requests
        return "";
    }

    public String Delete(String[] seats, String name){
        // Handle delete requests
        return "";
    }

    public synchronized String Reserve(String[] seats, String name){
        // Handle reserve requests
        return "";
    }

    public synchronized String BookSeat(String[] seats, String name,
                                        String seatNum){
        // Handle bookSeat requests
        return "";
    }

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int myID = sc.nextInt();
        int numServer = sc.nextInt();
        int numSeat = sc.nextInt();


        // Create an array to keep track of all server ips
        String[][] servers = new String[numServer][2];
        sc.nextLine();

        for (int i = 0; i < numServer; i++) {
            String server = sc.nextLine();
            String[] servInfo = server.split(":");
            servers[i+1][0] = servInfo[0];
            servers[i+1][1] = servInfo[1];
            /*System.out.println("Server IP: " + servers[i][0] + "\nServer " +
                "Port: " + servers[i][1] + "\n\n");*/
        }

        // Synchronize the reservation list on startup
        seating = new String[numSeat];
        SyncSeating();

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
                data = msg.split(" ");
                if (data[0].equals("RequestCS")){
                    SendAck();
                }
                // Enter critical section if we have an ack from all servers
                if (requested && acks == numServer){
                    requested = false;
                    acks = 1;
                    if (data[0].equals("reserve")){
                        // Send appropriate response to client
                    } else if (data[0].equals("bookSeat")) {
                        // Send appropriate response to client
                    } else if (data[0].equals("search")) {
                        // Send appropriate response to client
                    } else if (data[0].equals("delete")) {
                        // Send appropriate response to client
                    } else {
                        System.out.println("ERROR: No such command");
                    }
                    // Send the response and close the connection
                    out.println(returnData);
                    ReleaseCS();
                    out.close();
                    sock.close();
                    srv.close();
                }
            }
        } catch(Exception e) {
            System.out.print("Error handling the request");
        }
    }
}