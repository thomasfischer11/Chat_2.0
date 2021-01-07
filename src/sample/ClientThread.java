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
                if (clientMessage.equals("/logout")) {
                    logout();
                    break;
                }
                clientMessage = clientName + ": " + clientMessage;
                server.sendToAll(clientMessage);
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
        server.getClientThreads().remove(this);
        server.sendToAll(clientName + " disconnected");
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
            server.sendToAllExcept(clientName + " connected", this);
            sendMessage("/loggedIn");
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
            server.getUsers().put(clientName, new User(clientName, clientPW, server.getClientThreads().size(), null));
            server.getUsers().get(clientName).setOnline(true);
            server.sendToAllExcept(clientName + " connected", this);
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

    public String getClientName() {
        return clientName;
    }
}