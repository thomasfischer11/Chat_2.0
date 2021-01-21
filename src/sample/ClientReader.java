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
            client.getController().buttonUpdateRooms.setVisible(true);
            client.getController().vBoxRooms.setVisible(true);
            client.getController().buttonJoinRoom.setVisible(true);
            client.getController().labelCurrentRoom.setVisible(true);
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
    Runnable ErrorNotRegisteredUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: No User with this name is registered");
        }
    };
    Runnable ErrorAlreadyOnlineUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: User with this name is already online");
        }
    };
    Runnable ErrorIsBannedUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: You're banned from the Server");
        }
    };
    Runnable ErrorWrongPWUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: Wrong Password");
        }
    };
    Runnable ErrorAlreadyRegisteredUpdater = new Runnable() {
        @Override
        public void run() {
            client.getController().printError("Error: User with this Name is already registered");
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
    Runnable roomUpdater = new Runnable() {
        @Override
        public void run(){
            client.getController().updateRooms();
        }
    };
    Runnable userUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                client.getController().updateUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    Runnable currentRoomUpdater = new Runnable() {
        @Override
        public void run(){
            client.getController().labelCurrentRoom.setText("Current room: " + client.getController().getCurrentRoom());
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
                        Platform.runLater(roomUpdater);
                    }
                    else if(messageReceived.startsWith("/userNames")){
                        client.getController().setUserNames(messageReceived);
                        Platform.runLater(userUpdater);
                    }
                    else if(messageReceived.startsWith("/roomChanged")){
                        StringBuilder stringBuilder = new StringBuilder(messageReceived);
                        stringBuilder.delete(0, 13);
                        client.getController().setCurrentRoom(stringBuilder.toString());
                        Platform.runLater(currentRoomUpdater);
                    }
                    else if(messageReceived.equals("/requestName")) client.getController().getName();
                    else if(messageReceived.equals("/requestPW")) client.getController().getPW();
                    else if(messageReceived.equals("/registered") || messageReceived.equals("/loggedIn")) {
                        Platform.runLater(loggedInUpdater);
                    }
                    else if (messageReceived.startsWith("/err")){
                        if (messageReceived.equals("/errNotRegistered")) {
                            Platform.runLater(ErrorNotRegisteredUpdater);
                        }
                        if (messageReceived.equals("/errAlreadyOnline")) {
                            Platform.runLater(ErrorAlreadyOnlineUpdater);
                        }
                        if (messageReceived.equals("/errIsBanned")) {
                            Platform.runLater(ErrorIsBannedUpdater);
                        }
                        if (messageReceived.equals("/errWrongPW")) {
                            Platform.runLater(ErrorWrongPWUpdater);
                        }
                        if (messageReceived.equals("/errAlreadyRegistered")) {
                            Platform.runLater(ErrorAlreadyRegisteredUpdater);
                        }
                    }
                }
                else {
                    Platform.runLater(messageUpdater);
                }
            }
            while (!messageReceived.equals("Logged out") && !messageReceived.equals("Disconnected: The Server was stopped.") && !messageReceived.equals("You were kicked from the Server") && !messageReceived.equals("You were banned from the Server"));
            // Streams schließen
            sleep(10);
            in.close();
            client.getOut().close();
            client.setConnected(false);
            Platform.runLater(logOut);

        } catch (IOException | InterruptedException ignored) { }
    }
}
