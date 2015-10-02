import java.net.*;
import java.io.*;

public class Server {

    public static boolean soldOut = false;
    public static int seatsSold = 0;

    public static synchronized String Reserve(String[] seating, String name){
        // Method for handling reserve requests
        boolean newRes = false;
        boolean prevRes = false;
        String msg = "";
        // Check if seating is sold out
        if (!Server.soldOut){
            // If seating is not sold out we search through the seating
            for (int i=0; i<seating.length; i++){
                // Check if there is a previous reservation
                if (seating[i].equals(name)){
                  prevRes = true;
                  msg = "Seat already booked against the name provided";
                }
            }
            // If there was no previous reservation, find an empty seat to reserve
            if (!prevRes){
                for (int i=0; i<seating.length; i++){
                    if (seating[i].equals("")){
                        seating[i] = name;
                        newRes = true;
                        // Increment the number of seats sold
                        Server.seatsSold++;
                        // If seats sold is equal to seats available, sold out
                        if (Server.seatsSold == seating.length){
                            Server.soldOut = true;
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

    public static synchronized String BookSeat(String[] seating,
                                               String name, String seatNum){
        // Method for handling bookSeat requests
        boolean bookable = true;
        String msg = "";
        int seat = Integer.parseInt(seatNum);
        for (int i=0; i<seating.length; i++){
            if (seating[i].equals(name)){
                msg = "Seat already booked against the name provided";
                bookable = false;
                break;
            }
        }
        if (bookable){
            if (seating[seat] == ""){
                seating[seat] = name;
                Server.seatsSold++;
                if (Server.seatsSold == seating.length){
                            Server.soldOut = true;
                        }
                msg = "Seat assigned to you is " + seat;
            } else {
                msg = "" + seat + " is not available";
            }
        }
        return msg;
    }

    public static String Search(String[] seating, String name){
        // Method for handling search request
        String msg = "No reservation found for " + name;
        for (int i=0; i<seating.length; i++){
            if (seating[i].equals(name)){
                msg = "Your reserved seat is number " + i;
                return msg;
            }
        }
        return msg;
    }

    public static String Delete(String[] seating, String name){
        // Method for handling delete request
        String msg = "No reservation for " + name + " was found";
        for (int i=0; i<seating.length; i++){
            if (seating[i].equals(name)){
                seating[i] = "";
                Server.seatsSold--;
                Server.soldOut = false;
                msg = "Reservation for " + name + " has been deleted";
                return msg;
            }
        }
        return msg;
    }

    public static void main (String[] args) {
        int N;
        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <N>: the total number of available seats");
            System.out.println("\t\t\tassume the seat numbers are from 1 to N");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");

            System.exit(-1);
        }
        N = Integer.parseInt(args[0]);
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        // TODO: handle request from clients

        Seating seating = new Seating(N);
        UDPThread u = new UDPThread((Seating)seating, udpPort);
        TCPThread t = new TCPThread(seating, tcpPort);
        u.start();
        t.start();
        try {
                u.join();
                t.join();
            } catch (Exception e) { 
                System.out.println("There was an exception in join: ");
                e.printStackTrace();
            }
    }
}
