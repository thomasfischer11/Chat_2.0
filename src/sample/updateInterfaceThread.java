package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class updateInterfaceThread {
    public boolean isRunning;
    private ServerController controller;
    private Server server;


    public updateInterfaceThread(boolean isRunning, ServerController controller, Server server) {
        this.isRunning = isRunning;
        this.controller = controller;
        this.server = server;
    }

    public void run(){
        while (isRunning) {
            controller.updateVBoxRooms();
        }
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
