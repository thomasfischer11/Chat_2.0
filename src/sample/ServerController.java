package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    @FXML
    Button buttonUnbanUser;
    @FXML
    Button buttonKickUser;
    @FXML
    Button buttonBanUser;
    @FXML
    Button buttonWarnUser;



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
        if (command.startsWith("/warn")){
            if (command.length() > 6) warnUser(command.substring(6));
            return;
        }
        if (command.startsWith("/kick")) {
            if (command.length() > 6) kickUser(command.substring(6));
            return;
        }
        if (command.startsWith("/ban")) {
            if (command.length() > 5) banUser(command.substring(5));
            return;
        }
        if (command.startsWith("/unban")){
            if (command.length() > 7) unbanUser(command.substring(7));
        }
    }
    private void warnUser(String name) throws IOException {
        if (!server.getUsers().containsKey(name) || !server.getUsers().get(name).isOnline()) return;
        for (ClientThread aUser : server.getClientThreads()) {
            if (aUser.getClientName().equals(name)) {
                aUser.sendMessage("Warning from the Server: Keep it up and you will be kicked / banned! ");
                server.writeInServerLog("User "+ name + " was warned.");
                break;
            }
        }
    }

    private void kickUser (String name) throws IOException {
        if (!server.getUsers().containsKey(name) || !server.getUsers().get(name).isOnline()) return;
        for (ClientThread aUser : server.getClientThreads()) {
            if (aUser.getClientName().equals(name)) {
                aUser.sendMessage("You were kicked from the Server");
                server.getUsers().get(name).setOnline(false);
                server.getUsers().get(name).setRoom("");
                server.getClientThreads().remove(aUser);
                server.writeInServerLog("User "+ name + " was kicked from the server.");
                break;
            }
        }
        server.sendToAll(name + " was kicked from the server.");
    }

    private void banUser(String name) throws IOException {
        if (!server.getUsers().containsKey(name)) return;
        for (ClientThread aUser : server.getClientThreads()) {
            if (aUser.getClientName().equals(name)) {
                aUser.sendMessage("You were banned from the Server");
                server.getUsers().get(name).setOnline(false);
                server.getUsers().get(name).setRoom("");
                server.getClientThreads().remove(aUser);
                server.writeInServerLog("User "+ name + " was banned from the server.");
                break;
            }
        }
        server.getUsers().get(name).setBanned(true);
        server.sendToAll(name + " was banned from the server.");
    }

    private void unbanUser(String name) throws IOException {
        if (!server.getUsers().containsKey(name)) return;
        server.getUsers().get(name).setBanned(false);
        server.writeInServerLog("User "+ name + " was unbanned from the server.");

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
        Parent root = fxmlLoader.load();
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
            server.sendToAll("/roomsUpdated");
            for(ClientThread a: server.getClientThreads()){
                a.sendMessage(server.encodeRoomNames());
            }
        }
    }

    @FXML
    private void deleteRoom() throws IOException {
        if(choiceBoxRoomsInDelete.getValue() != null){
            for(User u : server.getUsers().values()){
                if(u.getRoom().equals(choiceBoxRoomsInDelete.getValue())){
                    u.setRoom("");
                }
            }
            for(ClientThread a: server.getRooms().get(choiceBoxRoomsInDelete.getValue())){
                a.sendMessage("/roomChanged+none");
            }
            server.getRooms().remove(choiceBoxRoomsInDelete.getValue());
            textAreaSuccessDeleteRoom.setText("Success!");
            server.writeInServerLog("Room "+ choiceBoxRoomsInDelete.getValue()+ " was deleted.");
            server.getController().updateVBoxRooms();
            server.sendToAll("/roomsUpdated");
            for(ClientThread a: server.getClientThreads()){
                a.sendMessage(server.encodeRoomNames());
            }
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
                for(User u : server.getUsers().values()){
                    if(u.getRoom().equals(choiceBoxRoomsInEdit.getValue())){
                        u.setRoom(textFieldNewName.getText());
                    }
                }
                for(ClientThread a: server.getRooms().get(textFieldNewName.getText())){
                    a.sendMessage("/roomChanged+"+ textFieldNewName.getText());
                }
                for(ClientThread a: server.getClientThreads()){
                    a.sendMessage(server.encodeRoomNames());
                }
                server.getController().updateVBoxRooms();
                textFieldNewName.clear();
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
        buttonKickUser.setVisible(false);
        buttonUnbanUser.setVisible(false);
        buttonBanUser.setVisible(false);
        buttonWarnUser.setVisible(false);
    }

    @FXML
    public void updateVBoxUsers(){
        vBoxRoomsUsers.getChildren().clear();
        for(String s: server.getUsers().keySet()){
            StringBuilder stringBuilder = new StringBuilder(s + "(" + onlineStatus(server.getUsers().get(s).isOnline()) + ", " + server.getUsers().get(s).getRoom()+ ")");
            if(server.getUsers().get(s).isBanned()){
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                stringBuilder.append(", banned");
            }
            Button button = new Button(stringBuilder.toString());
            button.setOnMouseClicked(e -> {
                for(Node n : vBoxRoomsUsers.getChildren()) {
                    n.setStyle("-fx-text-fill: black;");
                }
                button.setStyle(("-fx-text-fill: blue;"));
            });
            vBoxRoomsUsers.getChildren().add(button);
        }
        buttonOpenEditRoomWindow.setVisible(false);
        buttonOpenDeleteRoomWindow.setVisible(false);
        buttonOpenCreateRoomWindow.setVisible(false);
        buttonKickUser.setVisible(true);
        buttonUnbanUser.setVisible(true);
        buttonBanUser.setVisible(true);
        buttonWarnUser.setVisible(true);
    }

    private String onlineStatus(boolean x){
        if(x) return "online";
        else return "offline";
    }
    public TextField getTxtFieldServer() {
        return txtFieldServer;
    }

    @FXML
    private void onButtonUnbanUser() throws IOException {
        Button a = null;
        for(Node n: vBoxRoomsUsers.getChildren()){
            if(n.getStyle().equals("-fx-text-fill: blue;")){
                a = (Button)n;
            }
        }
        if (a != null){
            StringBuilder userName = new StringBuilder(a.getText());
            int i = 0;
            while(userName.charAt(i) != '('){
                i++;
            }
            userName.delete(i, userName.length());
            if(server.getUsers().get(userName.toString()).isBanned()){
                unbanUser(userName.toString());
            }
        }
        updateVBoxUsers();
    }

    @FXML
    private void onButtonKickUser() throws IOException {
        Button a = null;
        for(Node n: vBoxRoomsUsers.getChildren()){
            if(n.getStyle().equals("-fx-text-fill: blue;")){
                a = (Button)n;
            }
        }
        if (a != null){
            StringBuilder userName = new StringBuilder(a.getText());
            int i = 0;
            while(userName.charAt(i) != '('){
                i++;
            }

            userName.delete(i, userName.length());
            if(server.getUsers().get(userName.toString()).isOnline()) {
                kickUser(userName.toString());
            }
        }
        updateVBoxUsers();
    }

    @FXML
    private void onButtonBanUser() throws IOException {
        Button a = null;
        for(Node n: vBoxRoomsUsers.getChildren()){
            if(n.getStyle().equals("-fx-text-fill: blue;")){
                a = (Button)n;
            }
        }
        if (a != null){
            StringBuilder userName = new StringBuilder(a.getText());
            int i = 0;
            while(userName.charAt(i) != '('){
                i++;
            }
            userName.delete(i, userName.length());
            banUser(userName.toString());
        }
        updateVBoxUsers();
    }

    @FXML
    private void onbuttonWarnUser() throws IOException {
        Button a = null;
        for(Node n: vBoxRoomsUsers.getChildren()){
            if(n.getStyle().equals("-fx-text-fill: blue;")){
                a = (Button)n;
            }
        }
        if (a != null){
            StringBuilder userName = new StringBuilder(a.getText());
            int i = 0;
            while(userName.charAt(i) != '('){
                i++;
            }
            userName.delete(i, userName.length());
            warnUser(userName.toString());
        }
    }
}
