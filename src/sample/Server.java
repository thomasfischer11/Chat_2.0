package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Application {

    private HashSet<ClientThread> clientThreads = new HashSet<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, HashSet<ClientThread>> rooms = new HashMap<>();
    private ServerController controller;
    private ServerSocket serverSocket;
    private ClientAcceptorThread clientAcceptorThread;
    private boolean isOnline = false;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        controller = fxmlLoader.<ServerController>getController();
        controller.setServer(this);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Serverlog");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException { launch(args); }

    public void runServer () throws IOException {
        serverSocket = new ServerSocket(1312);
        clientAcceptorThread = new ClientAcceptorThread(this, serverSocket);
        clientAcceptorThread.start();
    }

    void sendToAll(String message) throws IOException {
        showInServerApp(message);
        try {
            for (ClientThread aUser : clientThreads) {
                aUser.sendMessage(message);
            }
        } catch ( IOException e) {}
    }

    void sendToAllExcept(String message, ClientThread excludeUser) throws IOException {
        showInServerApp(message);
        try {
            for (ClientThread aUser : clientThreads) {
                if (!aUser.equals(excludeUser)){
                    aUser.sendMessage(message);
                }
            }
        } catch ( IOException e) {}
    }



    public void setServerOnline() throws IOException {
        this.serverSocket = new ServerSocket(1312);
        this.clientAcceptorThread = new ClientAcceptorThread(this, serverSocket);
        clientAcceptorThread.start();
        isOnline = true;
    }

    public void stopServer() throws IOException {
        showInServerApp("Server is stopped by command...");
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

    public void showInServerApp(String message) throws IOException {
        controller.getTxtAreaServer().appendText(message + "\n");
    }

    public boolean isOnline(){
        return isOnline;
    }

    public HashSet<ClientThread> getClientThreads() {
        return clientThreads;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public HashMap<String, HashSet<ClientThread>> getRooms() {
        return rooms;
    }

    public void setRooms(HashMap<String, HashSet<ClientThread>> rooms) {
        this.rooms = rooms;
    }

    public ClientAcceptorThread getClientAcceptorThread() {
        return clientAcceptorThread;
    }

    public void setClientAcceptorThread(ClientAcceptorThread clientAcceptorThread) {
        this.clientAcceptorThread = clientAcceptorThread;
    }

    public ServerController getController() {
        return controller;
    }

    public void setController(ServerController controller) {
        this.controller = controller;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}