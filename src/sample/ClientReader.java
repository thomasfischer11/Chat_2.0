package sample;

import javafx.application.Platform;
import sample.Client;

import java.io.*;
import java.net.*;

public class ClientReader extends Thread {
    private Socket server;
    private Client client;
    private DataInputStream in;
    private String messageReceived;

    Runnable loggedInUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().login();
            client.showMessage("Logged In!");
        }
    };
    /*Runnable requestNameUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                client.getController().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };*/
    Runnable errorUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: Check your entered data");
        }
    };
    Runnable messageUpdater = new Runnable() {
        @Override
        public void run() {
            if (client.isLoggedIn()) client.showMessage(messageReceived);
        }
    };
    Runnable logOut = new Runnable() {
        @Override
        public void run() { if (client.isLoggedIn()) {
            try {
                client.getController().logOut();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    };

    ClientReader(Socket server , Client client){
        this.server = server;
        this.client = client;
    }

    @Override
    public void run(){
        try {
            in = new DataInputStream(server.getInputStream());
            messageReceived = "";
            do{
                messageReceived = in.readUTF();
                if(messageReceived.startsWith("/")){
                    if(messageReceived.startsWith("/roomNames")){
                        client.getController().setRoomNames(messageReceived);
                    }
                    if(messageReceived.equals("/requestName")) client.getController().getName();
                    else if(messageReceived.equals("/requestPW")) client.getController().getPW();
                    else if(messageReceived.equals("/registered") || messageReceived.equals("/loggedIn")) {
                        Platform.runLater(loggedInUpdater);
                    }
                    else if (messageReceived.equals("/errAlreadyOnline") || messageReceived.equals("/errNotRegistered") || messageReceived.equals("/errWrongPW") || messageReceived.equals("/errAlreadyRegistered")){
                        Platform.runLater(errorUpdater);
                    }
                    //else if(messageReceived.equals("/errAlreadyOnline")) client.getController().printError("Error: User is already Online");
                    //else if(messageReceived.equals("/errNotRegistered")) client.getController().printError("Error: User is not registered");
                    //else if(messageReceived.equals("/errWrongPW")) client.getController().printError("Error: Wrong Password");
                    //else if(messageReceived.equals("/errAlreadyRegistered")) client.getController().printError("Error: User with this name is already registered");
                }
                else {
                    Platform.runLater(messageUpdater);
                    //client.showMessage(messageReceived); //Platform.runLater(messageUpdater);
                }
            }
            while (!messageReceived.equals("Logged out") && !messageReceived.equals("Disconnected: The Server was stopped."));
            // Streams schließen
            Platform.runLater(logOut);
            sleep(10);
            in.close();
            client.getOut().close();

        } catch (IOException | InterruptedException ignored) { }
    }
}
