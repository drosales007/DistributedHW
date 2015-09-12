
import java.net.*;
import java.io.*;

public class UDPThread extends Thread{
	
	Seating seating;
	int port;
	int len;

	public UDPThread(Seating seating, int port){
		this.seating = seating;
		this.port = port;
		this.len = 1024;
	}

	public synchronized String Reserve(String[] seats, String name){
        // Method for handling reserve requests
        boolean newRes = false;
        boolean prevRes = false;
        String msg = "";
        // Check if seating is sold out
        if (!seating.isSoldOut()){
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
                        newRes = true;
                        // Increment the number of seats sold
                        seating.incrementSold();
                        // If seats sold is equal to seats available, sold out
                        if (seating.getSeatsSold() == seats.length){
                            seating.setSoldOut(true);
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

    public synchronized String BookSeat(String[] seats,
                                               String name, String seatNum){
        // Method for handling bookSeat requests
        boolean bookable = true;
        String msg = "";
        int seat = Integer.parseInt(seatNum);
        for (int i=0; i<seats.length; i++){
            if (seats[i].equals(name)){
                msg = "Seat already booked against the name provided";
                bookable = false;
                break;
            }
        }
        if (bookable){
            if (seats[seat] == ""){
                seats[seat] = name;
                seating.incrementSold();
                if (seating.getSeatsSold() == seats.length){
                            seating.setSoldOut(true);
                        }
                msg = "Seat assigned to you is " + seat;
            } else {
                msg = "" + seat + " is not available";
            }
        }
        return msg;
    }

    public String Search(String[] seats, String name){
        // Method for handling search request
        String msg = "No reservation found for " + name;
        for (int i=0; i<seats.length; i++){
            if (seats[i].equals(name)){
                msg = "Your reserved seat is number " + i;
                return msg;
            }
        }
        return msg;
    }

    public String Delete(String[] seats, String name){
        // Method for handling delete request
        String msg = "No reservation for " + name + " was found";
        for (int i=0; i<seats.length; i++){
            if (seats[i].equals(name)){
                seats[i] = "";
                seating.decrementSold();
                seating.setSoldOut(false);
                msg = "Reservation for " + name + " has been deleted";
                return msg;
            }
        }
        return msg;
    }

	public void run(){
		String returnData = "";
		byte[] buffer;
		DatagramPacket datapacket, returnpacket;
		try {
            DatagramSocket datasocket = new DatagramSocket(port);
            byte[] buf = new byte[len];
            // Loop indefinitely
            while (true) {
                datapacket = new DatagramPacket(buf, buf.length);
                // Receive data
                datasocket.receive(datapacket);
                // Splits data received into array index
                String[] data = new String(datapacket.getData(), 0,
                          datapacket.getLength()).split(" ");
                if (data[0].equals("reserve")){
                    System.out.println("reserve");
                    String[] s = seating.getSeats();
                    returnData = Reserve(s, data[1]);
                    for (int i=0; i<s.length; i++){
                        System.out.println(":  " + s[i]);
                    }

                } else if (data[0].equals("bookSeat")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    System.out.println("bookSeat");
                    String[] s = seating.getSeats();
                    returnData = BookSeat(s, data[1], data[2]);
                    for (int i=0; i<s.length; i++){
                        System.out.println(":  " + s[i]);
                    }
                } else if (data[0].equals("search")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    System.out.println("search");
                    String[] s = seating.getSeats();
                    returnData = Search(s, data[1]);
                    for (int i=0; i<s.length; i++){
                        System.out.println(":  " + s[i]);
                    }
                } else if (data[0].equals("delete")) {
                    // TODO: send appropriate command to the server and display the
                    // appropriate responses form the server
                    System.out.println("delete");
                    String[] s = seating.getSeats();
                    returnData = Delete(s, data[1]);
                    for (int i=0; i<s.length; i++){
                        System.out.println(":  " + s[i]);
                    }
                } else {
                    System.out.println("ERROR: No such command");
                }

                buffer = new byte[returnData.length()];
                buffer = returnData.getBytes();
                returnpacket = new DatagramPacket(
                      buffer,
                      buffer.length,
                      datapacket.getAddress(),
                      datapacket.getPort());
                datasocket.send(returnpacket);
            }
        } catch (SocketException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
	}
}