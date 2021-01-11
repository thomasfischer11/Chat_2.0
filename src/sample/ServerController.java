package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    @FXML
    TextArea textAreaRoomCreated;
    @FXML
    ChoiceBox<String> choiceBoxRoomsInDelete;
    @FXML
    Button buttonDeleteRoom;
    @FXML
    ChoiceBox<String> choiceBoxRoomsInEdit;
    @FXML
    Button buttonRename;
    @FXML
    TextField textFieldNewName;
    @FXML
    TextArea textAreaSuccessDeleteRoom;
    @FXML
    TextArea textAreaSuccessEditRoom;
    @FXML
    VBox vBoxRoomsUsers;
    @FXML
    Button buttonOpenCreateRoomWindow;
    @FXML
    Button buttonOpenEditRoomWindow;
    @FXML
    Button buttonOpenDeleteRoomWindow;
    @FXML
    AnchorPane anchorPaneCreateRoom;



    public void setServer(Server server) throws IOException {
        this.server = server;
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
        else {
            server.sendToAll("[Server]: " + s);
            server.writeInServerLog("[Server]: " + s);
        }
        if (server.isOnline()) txtFieldServer.setText("");

    }

    private void executeCommand(String command) throws IOException {
        if (command.equals("/stop")) {
            server.writeInServerLog("Server stopped.");
            server.stopServer();
            return;
        }
        if (command.startsWith("/kick")) {
            String name = command.substring(6);
            for (ClientThread aUser : server.getClientThreads()) {
                if (aUser.getClientName().equals(name)) {
                    aUser.sendMessage("You were kicked from the Server");
                    aUser.logout();
                    server.writeInServerLog("User "+aUser.getClientName()+ " was kicked from the server.");
                }
            }

        }
    }

    public TextArea getTxtAreaServer() {
        return txtAreaServer;
    }



    @FXML
    private void openCreateRoomWindow() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createRoomInterface.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Scene secondScene = new Scene(root);
        fxmlLoader.<ServerController>getController().setServer(server);

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
        fxmlLoader.<ServerController>getController().setServer(server);

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
        fxmlLoader.<ServerController>getController().setServer(server);

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

    @FXML
    private void setChoiceBoxRoomsInDelete(){
        for(String s: server.getRooms().keySet()){
            if(!choiceBoxRoomsInDelete.getItems().contains(s)) {
                choiceBoxRoomsInDelete.getItems().add(s);
            }
        }
    }

    @FXML
    private void setChoiceBoxRoomsInEdit(){
        for(String s: server.getRooms().keySet()){
            if(!choiceBoxRoomsInEdit.getItems().contains(s)) {
                choiceBoxRoomsInEdit.getItems().add(s);
            }
        }
    }

    @FXML
    private void createRoom() throws IOException {
        if(!textFieldRoomName.getText().equals("")){
            String roomName = textFieldRoomName.getText();
            server.getRooms().put(roomName, new HashSet<>());
            textAreaRoomCreated.setText("New room " + textFieldRoomName.getText() + " has been created.");
            server.writeInServerLog("New room " + textFieldRoomName.getText() + " has been created.");
            textFieldRoomName.clear();
            server.getController().updateVBoxRooms();

        }
    }

    @FXML
    private void deleteRoom() throws IOException {
        if(choiceBoxRoomsInDelete.getValue() != null){
            server.getRooms().remove(choiceBoxRoomsInDelete.getValue());
            textAreaSuccessDeleteRoom.setText("Success!");
            server.writeInServerLog("Room "+ choiceBoxRoomsInDelete.getValue()+ " was deleted.");
            server.getController().updateVBoxRooms();
        }
    }

    @FXML
    private void editRoom() throws IOException {
        if(choiceBoxRoomsInEdit.getValue() != null){
            HashSet<ClientThread> temp = server.getRooms().get(choiceBoxRoomsInEdit.getValue());
            if(!textFieldNewName.getText().equals("")) {
                server.getRooms().remove(choiceBoxRoomsInEdit.getValue());
                server.getRooms().put(textFieldNewName.getText(), temp);
                textAreaSuccessEditRoom.setText("Success!");
                server.writeInServerLog("Room "+ choiceBoxRoomsInEdit.getValue()+ " was renamed to "+ textFieldNewName.getText()+".");
                textFieldNewName.clear();
                server.getController().updateVBoxRooms();
            }
        }

    }

    @FXML
    public void updateVBoxRooms(){
        vBoxRoomsUsers.getChildren().clear();
        for(String s: server.getRooms().keySet()){
            StringBuilder room = new StringBuilder();
            room.append(s);
            room.append(" (");
            for(User a: server.getUsers().values()){
                if(a.getRoom().equals(s)){
                    room.append(a.getName()).append(", ");
                }
            }
            room.append(")");
            Button button = new Button(room.toString());
            vBoxRoomsUsers.getChildren().add(button);
        }
        buttonOpenEditRoomWindow.setVisible(true);
        buttonOpenDeleteRoomWindow.setVisible(true);
        buttonOpenCreateRoomWindow.setVisible(true);
    }

    @FXML
    public void updateVBoxUsers(){
        vBoxRoomsUsers.getChildren().clear();
        for(String s: server.getUsers().keySet()){
            Button button = new Button(s + "(" + server.getUsers().get(s).getRoom() + ")");
            vBoxRoomsUsers.getChildren().add(button);
        }
        buttonOpenEditRoomWindow.setVisible(false);
        buttonOpenDeleteRoomWindow.setVisible(false);
        buttonOpenCreateRoomWindow.setVisible(false);
    }

    public TextField getTxtFieldServer() {
        return txtFieldServer;
    }
}
