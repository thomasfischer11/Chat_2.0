package sample;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private HashSet<ClientThread> clientThreads = new HashSet<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, HashSet<ClientThread>> rooms = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server();
            server.runServer();
        } catch ( IOException e ) {e.printStackTrace();}
    }

    public void runServer () throws IOException {
        ServerSocket serverSocket = new ServerSocket(1312);
        ClientAcceptorThread clientAcceptorThread = new ClientAcceptorThread(this, serverSocket);
        clientAcceptorThread.start();
        Scanner scanner = new Scanner(System.in);
        String input;
        do{
            input = scanner.nextLine();
        } while(!input.equals("stop"));
        scanner.close();
        System.out.println("Stoppe Server.");
        //stopping clientAcceptorThread:
        clientAcceptorThread.setRunning(false);
        Socket server2 =  new Socket("localhost", 1312);
        server2.close();
        // closing all clientThreads:
        for(ClientThread a : clientThreads){
            a.sendMessage("Disconnected: The Server was stopped.");
            a.getIn().close();
            a.setRunning(false);
        }
    }

    void sendToAll(String message, ClientThread excludeUser) throws IOException {
        try {
            for (ClientThread aUser : clientThreads) {
                if(aUser != excludeUser) {
                    aUser.sendMessage(message);
                }
            }
        } catch ( IOException e) {}
    }

    public HashSet<ClientThread> getClientThreads() {
        return clientThreads;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }
}