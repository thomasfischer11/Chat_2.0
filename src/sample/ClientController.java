package sample;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class ClientController {
    @FXML
    private TextField txtFieldClient;
    @FXML
    private TextArea txtAreaClient;
    @FXML
    private Pane paneMain;
    @FXML
    private Label labelLoginRegister;
    @FXML
    private Button buttonConnect;
    @FXML
    private Button buttonChangeLoginRegister;
    @FXML
    private Label labelChangeLoginRegister;
    @FXML
    private TextField txtFieldUsername;
    @FXML
    private PasswordField pwField;
    @FXML
    private PasswordField pwField2;
    @FXML
    private Label labelError;
    @FXML
    VBox vBoxRooms;
    @FXML
    Label labelCurrentRoom;
    @FXML
    Button buttonUpdateRooms;
    @FXML
    Button buttonJoinRoom;
    @FXML
    Label labelRooms;



    private boolean register = false;
    private Client client;
    private String roomNames = "";
    private String room = "";


    public void setClient(Client client) throws IOException, ClassNotFoundException {
        this.client = client;
        this.client.setServer(new Socket("localhost", 1312));
        this.client.setClientReader(new ClientReader(this.client.getServer(), this.client));
        this.client.getClientReader().start();
        this.client.setOut(new DataOutputStream(this.client.getServer().getOutputStream()));
        this.client.setConnected(true);
    }



    @FXML
    public void updateRooms() throws IOException, InterruptedException {
        client.sendMessage("/updateRooms");
        vBoxRooms.getChildren().clear();
        //wait(30);
        StringBuilder roomNamesTemp = new StringBuilder();
        roomNamesTemp.append(roomNames);
        if(!roomNamesTemp.toString().equals("")) {
            while(roomNamesTemp.charAt(0) != '+') {
                roomNamesTemp.deleteCharAt(0);
            }
            roomNamesTemp.deleteCharAt(0);

            while(roomNamesTemp.length() != 0){
                StringBuilder s = new StringBuilder();
                while(roomNamesTemp.charAt(0) != '+'){
                    s.append(roomNamesTemp.charAt(0));
                    roomNamesTemp.deleteCharAt(0);
                }
                roomNamesTemp.deleteCharAt(0);
                Button button = new Button(s.toString());
                button.setOnMouseClicked(e -> {
                    for(Node n : vBoxRooms.getChildren()) {
                        n.setStyle("-fx-text-fill: black;");
                    }
                    button.setStyle(("-fx-text-fill: blue;"));
                });
                vBoxRooms.getChildren().add(button);
            }
        }
    }

    @FXML
    private void joinRoom() throws IOException, InterruptedException {
        Button a = null;
        for(Node n : vBoxRooms.getChildren()) {
            if(n.getStyle().equals("-fx-text-fill: blue;")){
                a = (Button)n;
            }
        }
        if (a != null){
            StringBuilder roomToJoin = new StringBuilder(a.getText());
            int i = 0;
            while(roomToJoin.charAt(i) != '('){
                i++;
            }
            roomToJoin.delete(i-1, roomToJoin.length());
            client.sendMessage("/joinRoom+" + roomToJoin.toString());
            this.room = roomToJoin.toString();
            labelCurrentRoom.setText("Current room: "+ roomToJoin.toString());
        }
        updateRooms();
    }

    @FXML
    private void sendMessageOnEnter (KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)){
            String txt = txtFieldClient.getText();
            int length = txt.length();
            if (!txt.equals("")){
                if (length > 50){
                    if (length > 500) {
                        txtFieldClient.setText("Message is too long!");
                        return;
                    }
                    for (String subString : splitEqually(txt, 50)) {
                        client.sendMessage(subString);
                    }
                    return;
                }
                client.sendMessage(txt);
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
    private void onConnectClicked (MouseEvent event) throws IOException {
        labelError.setText("");
        if (!client.isLoggedIn()) {
            if (txtFieldUsername.getText().equals("") || pwField.getText().equals("")){
                labelError.setText("Enter Name and Password!");
                labelError.setVisible(true);
                return;
            }
            else if (register && !pwField.getText().equals(pwField2.getText())){
                labelError.setText("Passwords do not match!");
                labelError.setVisible(true);
                return;
            }
            if(!client.isConnected()) client.reconnect();
            if (client.isConnected()) {
                if (register) client.sendMessage("/register");
                else client.sendMessage("/login");
            }
        } else {
            logOut();
            return;
        }
    }

    @FXML
    private void onChangeLoginRegisterClicked (MouseEvent event){
        if(register){
            register = false;
            labelLoginRegister.setText("Login");
            pwField2.setVisible(false);
            labelChangeLoginRegister.setText("Not registered yet?");
            buttonChangeLoginRegister.setText("Register");
        }
        else{
            register = true;
            labelLoginRegister.setText("Register");
            pwField2.setVisible(true);
            labelChangeLoginRegister.setText("Already registered?");
            buttonChangeLoginRegister.setText("Login");
        }
    }


    public TextField getTxtFieldClient() {
        return txtFieldClient;
    }

    public TextArea getTxtAreaClient() {
        return txtAreaClient;
    }

    public void getName() throws IOException {
        client.sendMessage(txtFieldUsername.getText());
    }

    public void getPW() throws IOException {
        client.sendMessage(pwField.getText());
    }

    public void printError(String ErrorMessage) {
        labelError.setText(ErrorMessage);
        labelError.setVisible(true);
    }

    public void login() {
        client.setLoggedIn(true);
        buttonConnect.setText("Log out");
        txtFieldClient.setEditable(true);
        labelLoginRegister.setVisible(false);
        labelLoginRegister.setText("Login");
        labelChangeLoginRegister.setText("Not registered yet?");
        buttonChangeLoginRegister.setText("Register");
        buttonChangeLoginRegister.setVisible(false);
        labelChangeLoginRegister.setVisible(false);
        txtFieldUsername.setVisible(false);
        pwField.setVisible(false);
        pwField2.setVisible(false);
        labelError.setVisible(false);
        register = false;
        labelCurrentRoom.setVisible(true);
        vBoxRooms.setVisible(true);
        buttonUpdateRooms.setVisible(true);
        buttonJoinRoom.setVisible(true);
        labelRooms.setVisible(true);
    }

    public void logOut() throws IOException {
        if (client.isConnected()) client.sendMessage("/logout");
        buttonConnect.setText("Log in");
        txtFieldClient.setEditable(false);
        labelLoginRegister.setVisible(true);
        buttonChangeLoginRegister.setVisible(true);;
        labelChangeLoginRegister.setVisible(true);
        txtFieldUsername.setVisible(true);
        pwField.setVisible(true);
        labelError.setVisible(true);
        labelCurrentRoom.setVisible(false);
        vBoxRooms.setVisible(false);
        buttonUpdateRooms.setVisible(false);
        buttonJoinRoom.setVisible(false);
        labelRooms.setVisible(false);
        client.setLoggedIn(false);
        client.setConnected(false);
    }

    public void setRoomNames(String roomNames) {
        this.roomNames = roomNames;
    }

    public Label getLabelError (){
        return labelError;
    }
}
