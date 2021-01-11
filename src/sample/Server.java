package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
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
    private final String serverLog = "serverlog.txt";
    private final String userData = "userdata.txt";
    private final String roomData = "roomdata.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {
        readUserData();
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        controller = fxmlLoader.<ServerController>getController();
        controller.setServer(this);
        controller.updateVBoxUsers();
        controller.updateVBoxRooms();
        setServerOnline();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Serverlog");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException { launch(args); }

    public void setServerOnline() throws IOException {
        this.serverSocket = new ServerSocket(1312);
        this.clientAcceptorThread = new ClientAcceptorThread(this, serverSocket);
        clientAcceptorThread.start();
        isOnline = true;
    }

    private void readUserData() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream(new File(userData));
        ObjectInputStream oi = new ObjectInputStream(fi);
        this.users = (HashMap<String, User>)oi.readObject();
        oi.close();
        fi.close();
        fi = new FileInputStream(new File(roomData));
        oi = new ObjectInputStream(fi);
        StringBuilder roomNames = new StringBuilder((String)oi.readObject());
        while(roomNames.length() != 0){
            StringBuilder roomName = new StringBuilder();
            while(roomNames.charAt(0) != '/'){
                roomName.append(roomNames.charAt(0));
                roomNames.deleteCharAt(0);
            }
            rooms.put(roomName.toString(), new HashSet<>());
            roomNames.deleteCharAt(0);
        }
        oi.close();
        fi.close();
    }

    void sendToAll(String message) throws IOException {
        showInServerApp(message);
        try {
            for (ClientThread aUser : clientThreads) {
                aUser.sendMessage(message);
            }
        } catch ( IOException e) {}
    }

    void sendToRoom(String message, String clientName) throws IOException {
        String room = users.get(clientName).getRoom();
        if(!room.equals("")){
            showInServerApp("(" + room + ") " + message);
            try {
                for (ClientThread aUser : rooms.get(room)) {
                    aUser.sendMessage(message);
                }
            } catch ( IOException e) {}
        }

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

    public void writeInServerLog(String log) throws IOException {
        BufferedWriter  myWriter = new BufferedWriter (new FileWriter(serverLog, true) ) ;
        myWriter.write(log+ "\n");
        myWriter.close();
        controller.getTxtAreaServer().appendText(log+"\n");
    }

    public void saveUserData() throws IOException {
        FileOutputStream f = new FileOutputStream(new File(userData), false);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(users);
        o.close();
        f.close();
        f = new FileOutputStream(new File(roomData), false);
        o = new ObjectOutputStream(f);
        StringBuilder s = new StringBuilder();
        for(String roomName: rooms.keySet()){
            s.append(roomName);
            s.append("/");
        }
        o.writeObject(s.toString());
        o.close();
        f.close();
    }

    public void stopServer() throws IOException {
        showInServerApp("Server is stopped by command...");
        //stopping clientAcceptorThread:
        clientAcceptorThread.setRunning(false);
        Socket server2 =  new Socket("localhost", 1312);
        server2.close();
        //saving user data
        saveUserData();
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

/*public void runServer () throws IOException {
        serverSocket = new ServerSocket(1312);
        clientAcceptorThread = new ClientAcceptorThread(this, serverSocket);
        clientAcceptorThread.start();
    }*/