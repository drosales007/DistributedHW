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
        int[][] servers = new int[numServer][2];
        sc.nextLine();

        for (int i = 0; i < numServer; i++) {
            // TODO: parse inputs to get the ips and ports of servers
            String server = sc.nextLine();
            String[] servInfo = server.split(":");
            System.out.println(servInfo[0] + " " + servInfo[1]);
            servers[i][0] = Integer.parseInt(servInfo[0]);
            servers[i][1] = Integer.parseInt(servInfo[1]);
            /*System.out.println("Server IP: " + servers[i][0] + "\nServer " +
                "Port: " + servers[i][1] + "\n\n");*/
        }

        // TODO: handle request from clients
    }
}