import java.io.*;
import java.lang.reflect.Array;
import java.text.*;
import java.util.*;
import java.net.*;

public class Server {

    static ArrayList<ClientHandler> clientArray = new ArrayList<>();

    public static void main(String[] args) {

        int playerId = 0;

        try (ServerSocket ss = new ServerSocket(7777)) {

            System.out.println("The Server has successfully started at port 7777");

            while (true) {
                Socket s = ss.accept();

                ClientHandler handler = new ClientHandler(s, playerId);
                Thread t = new Thread(handler);
                t.start();
            
                clientArray.add(new ClientHandler(s, playerId));

                System.out.println("A new client connected.");

                playerId++;
                System.out.println("There are " + playerId + " connected");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized static void broadCast(String s) {
        for (ClientHandler client : clientArray) {
            client.sendToClient(s);
        }

    }
}

class ClientHandler implements Runnable {

    private Socket socket;
    private int playerId;
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket socket, int playerId) {
        this.socket = socket;
        this.playerId = playerId;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToClient(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String clientResponse;
        String serverResponse;

        try {
            out.writeUTF("You are player " + playerId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                // switch case
                clientResponse = in.readUTF();
                System.out.println("Client says "+ clientResponse);

                if (clientResponse.equals("Exit")) {
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                } else {
                // if (clientResponse.equals("Hey")) {
                    Server.broadCast(clientResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
