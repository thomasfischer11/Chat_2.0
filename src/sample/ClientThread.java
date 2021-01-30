package sample;

import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientThread extends Thread implements Serializable{

    private Socket client;
    private Server server;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isRunning;
    private String clientName;
    private String clientPW;
    private boolean isLoggedIn;

    Runnable vBoxRoomsUpdater = new Runnable() {
        @Override
        public void run(){
            if(server.getController().buttonOpenCreateRoomWindow.isVisible()) {
                server.getController().updateVBoxRooms();
            }
            else server.getController().updateVBoxUsers();
        }
    };

    public ClientThread(Socket client, Server server) {
        this.client = client;
        this.server = server;
        this.isRunning = true;
        this.isLoggedIn = false;
    }

    @Override
    public String toString() {
        return "";
    }

    public void run() { // Bearbeitung einer aufgebauten Verbindung
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
            while (!isLoggedIn) registerLogin();
            sendMessage("Es sind gerade online:");
            StringBuilder onlineUserList = new StringBuilder();
            for (User i : server.getUsers().values()) {
                if(i.isOnline()) {
                    onlineUserList.append(i.getName()).append(", ");
                }
            }
            sendMessage(onlineUserList.substring(0, onlineUserList.length()-2));
            String clientMessage;
            while (isRunning) {
                clientMessage = in.readUTF();
                if (clientMessage.equals("/logout")) {
                    logout();
                    break;
                }
                else if (clientMessage.equals("/updateRooms")) {
                    sendMessage(server.encodeRoomNames());
                }
                else if (clientMessage.equals("/updateUsers"))  {
                    sendMessage(server.encodeUserNames());
                }
                else if(clientMessage.startsWith("/joinPrivateRoom+")){
                    clientMessage = clientMessage.substring(17);
                    server.sendPrivate("/startPrivateChat", this.clientName, clientMessage);
                }
                else if(clientMessage.startsWith("/private+")){
                    clientMessage = clientMessage.substring(9);
                    int i;
                    for(i = 0; clientMessage.charAt(i) != '+' ;i++){}
                    String to = clientMessage.substring(0, i);
                    clientMessage = clientMessage.substring(i+1);
                    server.sendPrivate(clientMessage, this.clientName, to);
                }
                else if(clientMessage.startsWith("/joinRoom+")){
                    StringBuilder roomName = new StringBuilder();
                    roomName.append(clientMessage);
                    while(roomName.charAt(0) != '+'){
                        roomName.deleteCharAt(0);
                    }
                    roomName.deleteCharAt(0);
                    //remove clientThread from old room
                    String oldRoom = server.getUsers().get(clientName).getRoom();
                    if(!oldRoom.equals("")) {
                        server.getRooms().get(oldRoom).remove(this);
                    }
                    //put clientThread into new room
                    server.getRooms().get(roomName.toString()).add(this);
                    //change room-name in user-class
                    server.getUsers().get(clientName).setRoom(roomName.toString());
                    //update ServerInterface
                    Platform.runLater(vBoxRoomsUpdater);
                    //update ClientInterfaces
                    for(ClientThread a : server.getClientThreads()){
                        a.sendMessage(server.encodeRoomNames());
                    }
                }
                else{
                    clientMessage = clientName + ": " + clientMessage;
                    server.sendToRoom(clientMessage, clientName);
                }
            }
        } catch (IOException e) {
        } // Fehler bei Ein- und Ausgabe
         finally {
            if (client != null) try {
                client.close();
            } catch (IOException e) {
            }
        }
    }


    public void logout() throws IOException {
        sendMessage("Logged out");
        server.getUsers().get(clientName).setOnline(false);
        server.getUsers().get(clientName).setRoom("");
        server.getClientThreads().remove(this);
        server.sendToAll(clientName + " disconnected");
        server.writeInServerLog("User "+ clientName + " logged out.");
        Platform.runLater(vBoxRoomsUpdater);
        //andere ClientInterfaces updaten
        for(ClientThread a : server.getClientThreads()) {
            a.sendMessage(server.encodeRoomNames());
        }
        //update all User-Interfaces
        server.sendToAll((server.encodeUserNames()));
    }

    private void registerLogin() throws IOException {
        String registerOrLogin = in.readUTF();
        requestClientName();
        this.clientName = in.readUTF();
        requestClientPW();
        this.clientPW = in.readUTF();
        if (registerOrLogin.equals("/login")) login(clientName, clientPW);
        else if(registerOrLogin.equals("/register")) register(clientName, clientPW);
    }

    private void requestClientName() throws IOException {
        sendMessage("/requestName");
    }

    private void requestClientPW() throws IOException {
        sendMessage("/requestPW");
    }

    public void login(String clientName, String clientPW) throws IOException {
        System.out.println("l");
        if (!server.getUsers().containsKey(clientName)) {
            sendMessage("/errNotRegistered");
            System.out.println("b");
            return;
        }
        if (server.getUsers().get(clientName).isOnline()){
            sendMessage("/errAlreadyOnline");
            System.out.println("a");
            return;
        }
        if (clientPW.equals(server.getUsers().get(clientName).getPassword())) {
            if(server.getUsers().get(clientName).isBanned()){
                sendMessage("/errIsBanned");
                return;
            }
            server.sendToAllExcept(clientName + " connected", this);
            sendMessage("/loggedIn");
            server.getUsers().get(clientName).setOnline(true);
            this.isLoggedIn = true;
            System.out.println("c");
            sendMessage(server.encodeRoomNames());
            server.writeInServerLog("User "+ clientName + " logged in.");
            Platform.runLater(vBoxRoomsUpdater);
            //update all User-Interfaces
            server.sendToAll((server.encodeUserNames()));

        }
        else {
            sendMessage("/errWrongPW");
            System.out.println("d");
            return;
        }
    }

    private void register(String clientName, String clientPW) throws IOException {
        System.out.println("r");
        if(server.getUsers().containsKey(clientName)){
            sendMessage("/errAlreadyRegistered");
            System.out.println("e");
            return;
        }
        else {
            server.getUsers().put(clientName, new User(clientName, clientPW, server.getClientThreads().size(), ""));
            server.getUsers().get(clientName).setOnline(true);
            server.sendToAllExcept(clientName + " connected", this);
            sendMessage("/registered");
            this.isLoggedIn = true;
            System.out.println("f");
            server.writeInServerLog("New User "+ clientName + " was registered.");
            sendMessage(server.encodeRoomNames());
            Platform.runLater(vBoxRoomsUpdater);
            //update all User-Interfaces
            server.sendToAll((server.encodeUserNames()));

        }
    }


    public void sendMessage(String message) throws IOException {
        try {
            out.writeUTF(message);
        } catch (IOException ignored) { }
        try {
            this.sleep(10);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getClientName() {
        return clientName;
    }
}