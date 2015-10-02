import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Client {

    public static String TCPCall(String hostname, String cmd, int port){
        String msg = "fail";
        try{
            Socket sock = new Socket(hostname, port);
            BufferedReader in = new BufferedReader(new InputStreamReader
                (sock.getInputStream()));
            while (!in.ready()) {
                //wait
            }
            msg = in.readLine();
            System.out.println(msg);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            out.println(cmd);
            while (!in.ready()); {
                //wait
            }
            System.out.println(in.readLine());
            while (!in.ready()) {
                //wait
            }
            System.out.println(in.readLine());
            in.close();
        } catch (UnknownHostException e) {
              System.err.println(e);
        } catch (SocketException e) {
                System.err.println(e);
        } catch (IOException e) {
                  System.err.println(e);
        }
      return msg;
    }

    public static String UDPCall(String hostname, String cmd, int port){

        byte[] rbuffer = new byte[1024];
        byte[] buffer = new byte[cmd.length()];
        buffer = cmd.getBytes();
        String msg = "";
        try{
            DatagramPacket sPacket, rPacket;
            DatagramSocket datasocket = new DatagramSocket();
            InetAddress ia = InetAddress.getByName(hostname);
            sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
            datasocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            datasocket.receive(rPacket);
            msg = new String(rPacket.getData(), 0, rPacket.getLength());
        } catch (UnknownHostException e) {
              System.err.println(e);
        } catch (SocketException e) {
                System.err.println(e);
        } catch (IOException e) {
                  System.err.println(e);
        }
        return msg;
    }

    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("reserve")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 3){
                    String protocol = tokens[2];
                    if (protocol.toLowerCase().equals("u")){
                        String msg = UDPCall(hostAddress, cmd, udpPort);
                        System.out.println(msg);
                    }
                    else if (protocol.toLowerCase().equals("t")){
                        String msg = TCPCall(hostAddress, cmd, tcpPort);
                    } else {
                        System.out.println("Invalid request");
                    }
                } else {
                    System.out.println("Invalid request");
                }
            } else if (tokens[0].equals("bookSeat")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 4){
                    String protocol = tokens[3];
                    if (protocol.toLowerCase().equals("u")){
                        String msg = UDPCall(hostAddress, cmd, udpPort);
                        System.out.println(msg);
                    }
                    else if (protocol.toLowerCase().equals("t")){
                        String msg = TCPCall(hostAddress, cmd, tcpPort);
                    } else {
                        System.out.println("Invalid request");
                    }
                } else {
                    System.out.println("Invalid request");
                }
            } else if (tokens[0].equals("search")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 3){
                    String protocol = tokens[2];
                    if (protocol.toLowerCase().equals("u")){
                        String msg = UDPCall(hostAddress, cmd, udpPort);
                        System.out.println(msg);
                    }
                    else if (protocol.toLowerCase().equals("t")){
                        String msg = TCPCall(hostAddress, cmd, tcpPort);
                    } else {
                        System.out.println("Invalid request");
                    }
                } else {
                    System.out.println("Invalid request");
                }
            } else if (tokens[0].equals("delete")) {
                // TODO: send appropriate command to the server and display the
                // appropriate responses form the server
                if (tokens.length == 3){
                    String protocol = tokens[2];
                    if (protocol.toLowerCase().equals("u")){
                        String msg = UDPCall(hostAddress, cmd, udpPort);
                        System.out.println(msg);
                    }
                    else if (protocol.toLowerCase().equals("t")){
                        String msg = TCPCall(hostAddress, cmd, tcpPort);
                    } else {
                        System.out.println("Invalid request");
                    }
                } else {
                    System.out.println("Invalid request");
                }
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }
}
