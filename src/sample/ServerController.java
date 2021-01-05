package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerController {

    private Server server;
    @FXML
    private TextField txtFieldServer;
    @FXML
    private TextArea txtAreaServer;
    @FXML
    private Button buttonStartStop;
    @FXML
    private TextField textFieldRoomName;
    @FXML
    public Button buttonCreateRoom;

    public void setServer(Server server) throws IOException {
        this.server = server;
        server.setServerOnline();
    }

    @FXML
    private void sendMessageOnEnter(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            String txt = txtFieldServer.getText();
            int length = txt.length();
            if (!txt.equals("")) {
                if (length > 50) {
                    if (length > 500) {
                        txtFieldServer.setText("String is too long!");
                        return;
                    }
                    for (String subString : splitEqually(txt, 50)) {
                        showMessage(subString);
                    }
                    return;
                }
                showMessage(txt);
            }
        }
    }

    public ArrayList<String> splitEqually(String text, int size) {
        ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    @FXML
    private void onStartStopClicked(MouseEvent event) throws IOException {
        executeCommand("/stop");
    }

    private void showMessage(String s) throws IOException {
        if (s.startsWith("/")) executeCommand(s);
        else server.sendToAll("[Server]: " + s);
        if (server.isOnline()) txtFieldServer.setText("");
    }

    private void executeCommand(String command) throws IOException {
        if (command.equals("/stop")) {
            server.stopServer();
            return;
        }
        if (command.startsWith("/kick")) {
            String name = command.substring(6);
            for (ClientThread aUser : server.getClientThreads()) {
                if (aUser.getClientName().equals(name)) {
                    aUser.sendMessage("You were kicked from the Server");
                    aUser.logout();
                }
            }
        }
    }

    public TextArea getTxtAreaServer() {
        return txtAreaServer;
    }

    @FXML
    private void createRoom() throws IOException {
        if(textFieldRoomName.getText() != null){
            String roomName = textFieldRoomName.getText();
            server.getRooms().put(roomName, new HashSet<>());
            showMessage("New room " + textFieldRoomName.getText() + " has been created.");
        }
    }

    @FXML
    private void openCreateRoomWindow() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createRoomInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Scene secondScene = new Scene(root);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Create Room");
        newWindow.setScene(secondScene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(server.getPrimaryStage());

        // Set position of second window, related to primary window.
        newWindow.setX(server.getPrimaryStage().getX() + 200);
        newWindow.setY(server.getPrimaryStage().getY() + 100);

        newWindow.show();
    }

    @FXML
    private void openEditRoomWindow() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editRoomInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Scene secondScene = new Scene(root);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Edit Room");
        newWindow.setScene(secondScene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(server.getPrimaryStage());

        // Set position of second window, related to primary window.
        newWindow.setX(server.getPrimaryStage().getX() + 200);
        newWindow.setY(server.getPrimaryStage().getY() + 100);

        newWindow.show();
    }

    @FXML
    private void openDeleteRoomWindow() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("deleteRoomInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Scene secondScene = new Scene(root);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Delete Room");
        newWindow.setScene(secondScene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(server.getPrimaryStage());

        // Set position of second window, related to primary window.
        newWindow.setX(server.getPrimaryStage().getX() + 200);
        newWindow.setY(server.getPrimaryStage().getY() + 100);

        newWindow.show();
    }
}
