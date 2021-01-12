package sample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Client extends Application  {
    private Socket server;
    private ClientReader clientReader;
    private ClientController controller;
    private DataOutputStream out;
    private boolean connected;
    private boolean loggedIn = false;

    EventHandler<WindowEvent> eventHandlerCloseWindow = windowEvent -> {
        try {
            controller.logOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ControllerSample.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        controller = fxmlLoader.<ClientController>getController();
        controller.setClient(this);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Server");
        primaryStage.onCloseRequestProperty().set(eventHandlerCloseWindow);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }


    public ClientController getController() {
        return controller;
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        if (isLoggedIn()) controller.getTxtFieldClient().setText("");
    }

    public void reconnect () throws IOException, ConnectException {
        try {
            server = new Socket("localhost", 1312);
            clientReader = new ClientReader(server, this);
            clientReader.start();
            out = new DataOutputStream(server.getOutputStream());
            this.setConnected(true);
        } catch (ConnectException e) {controller.getLabelError().setText("Can't connect to Server");}
    }

    public void showMessage(String message){
        controller.getTxtAreaClient().appendText(message + "\n");
    }

    public Socket getServer() {
        return server;
    }

    public void setServer(Socket server) {
        this.server = server;
    }

    public ClientReader getClientReader() {
        return clientReader;
    }

    public void setClientReader(ClientReader clientReader) {
        this.clientReader = clientReader;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean b) {
        loggedIn = b;
    }

    public boolean isConnected(){return connected;}

    public void setConnected(boolean b){ connected = b;}
}
