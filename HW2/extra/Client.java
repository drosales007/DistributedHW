import java.util.Scanner;
import java.util.*;
import java.net.*;
import java.io.*;

public class Client {

    public static boolean TCPCall(String cmd, String[][] servers, int numServer){
        // Makes TCP calls to available servers until we receive a response
        String msg;
        boolean next = false;
        while(true){
            // Loop through all servers in order of proximity
            for (int i=0; i<numServer; i++){
                String hostname = servers[i][0];
                int port = Integer.parseInt(servers[i][1]);
                try {
                    Socket sock = new Socket(hostname, port);
                    BufferedReader in = new BufferedReader(new InputStreamReader
                        (sock.getInputStream()));
                    long startTime = System.currentTimeMillis();
                    while (!in.ready()) {
                        // Wait up to 100 ms to receive a connection acknowledgement
                        if (System.currentTimeMillis() - startTime > 100){
                            next = true;
                            break;
                        }
                    }
                    // If we waited longer than 100 ms, close and try another server
                    if (next){
                        System.out.println("Timeout");
                        in.close();
                        sock.close();
                        continue;
                    }
                    // Print the response from the server to the console
                    msg = in.readLine();
                    System.out.println(msg);
                    PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                    // Send the command to the server
                    out.println(cmd);
                    while (!in.ready()); {
                        // wait
                    }
                    // Print the response from the server to the console and close
                    msg = in.readLine();
                    System.out.println(msg);
                    String[] msg2 = msg.split(" ");
                    in.close();
                    out.close();
                    sock.close();

                    // Now we will connect to the worker thread with the info returned
                    String hostname2 = msg2[0];
                    int port2 = Integer.parseInt(msg2[1]);
                    String node_val = msg2[2];
                    Socket sock2 = new Socket(hostname2, port2);
                    BufferedReader in2 = new BufferedReader(new InputStreamReader
                        (sock2.getInputStream()));
                    while (!in2.ready()){
                        // wait
                    }
                    // Print the response from the server to the console
                    msg = in2.readLine();
                    System.out.println(msg);
                    PrintWriter out2 = new PrintWriter(sock2.getOutputStream(), true);
                    // Send the command to the server
                    String cmd2 = cmd + " " + node_val;
                    out2.println(cmd2);
                    while (!in2.ready()){
                        // wait
                    }
                    System.out.println(in2.readLine());
                    in2.close();
                    out2.close();
                    sock2.close();
                    return true;
                } catch (Exception e) {
                    System.out.print("Error");
                }
            }
        }
    }

    public static void main (String[] args) {

        Scanner sc = new Scanner(System.in);
        int numServer = sc.nextInt();

        // Create an array to keep track of all server ips/ports
        String[][] servers = new String[numServer][2];
        sc.nextLine();

        for (int i = 0; i < numServer; i++) {
            // Parse inputs to get the ips and ports of servers
            String server = sc.nextLine();
            String[] servInfo = server.split(":");
            servers[i][0] = servInfo[0];
            servers[i][1] = servInfo[1];
        }

        System.out.println("Server info gathered");

        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");
            if (tokens[0].equals("reserve")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 2){
                    TCPCall(cmd, servers, numServer);
                } else {
                    System.out.println("Invalid command. The command should " +
                        "be of the form '<action> <name>'.\nEx: reserve david");
                }
            } else if (tokens[0].equals("bookSeat")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 3){
                    TCPCall(cmd, servers, numServer);
                }
            } else if (tokens[0].equals("search")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 2){
                    TCPCall(cmd, servers, numServer);
                }
            } else if (tokens[0].equals("delete")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 2){
                    TCPCall(cmd, servers, numServer);
                }
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }
}