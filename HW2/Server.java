import java.util.Scanner;

public class Server {

    public static void SyncWithServers(){
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
            servers[i][0] = servInfo[0];
            servers[i][1] = servInfo[1];
            /*System.out.println("Server IP: " + servers[i][0] + "\nServer " +
                "Port: " + servers[i][1] + "\n\n");*/
        }

        // TODO: handle request from clients
    }
}