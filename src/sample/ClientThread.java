package sample;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientThread extends Thread {

    private Socket client;
    private Server server;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean isRunning;
    private String clientName;
    private String clientPW;
    private boolean isLoggedIn;

    public ClientThread(Socket client, Server server) {
        this.client = client;
        this.server = server;
        this.isRunning = true;
        this.isLoggedIn = false;
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
                if (clientMessage.equals("/disconnect")) {
                    sendMessage("Disconnected");
                    server.sendToAll(clientName + " disconnected", this);
                    server.getUsers().get(clientName).setOnline(false);
                    server.getClientThreads().remove(this);
                    break;
                }
                clientMessage = clientName + ": " + clientMessage;
                server.sendToAll(clientMessage, this);
                sendMessage(clientMessage);
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

    private void registerLogin() throws IOException {
        String registerOrLogin = in.readUTF();
        requestClientName();
        this.clientName = in.readUTF();
        requestClientPW();
        this.clientPW = in.readUTF();
        System.out.println("ja moin moin");
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
        if (server.getUsers().containsKey(clientName) &&  server.getUsers().get(clientName).isOnline()){
            sendMessage("/errAlreadyOnline");
            System.out.println("a");
            //registerLogin();
            return;
        }
        if (!server.getUsers().containsKey(clientName)) {
            sendMessage("/errNotRegistered");
            System.out.println("b");
            //registerLogin();
            return;
        }
        if (clientPW.equals(server.getUsers().get(clientName).getPassword())) {
            server.sendToAll(clientName + " connected", this);
            sendMessage("/connected");
            server.getUsers().get(clientName).setOnline(true);
            this.isLoggedIn = true;
            System.out.println("c");
        }
        else {
            sendMessage("/errWrongPW");
            System.out.println("d");
            //registerLogin();
            return;
        }
    }

    private void register(String clientName, String clientPW) throws IOException {
        System.out.println("r");
        if(server.getUsers().containsKey(clientName)){
            sendMessage("/errAlreadyRegistered");
            System.out.println("e");
            //registerLogin();
            return;
        }
        else {
            server.getUsers().put(clientName, new User(clientName, clientPW, server.getClientThreads().size()));
            server.getUsers().get(clientName).setOnline(true);
            server.sendToAll(clientName + " connected", this);
            sendMessage("/registered");
            this.isLoggedIn = true;
            System.out.println("f");
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
}