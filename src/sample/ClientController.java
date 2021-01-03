package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


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

    private boolean register = false;
    private Client client;


    public void setClient(Client client) throws IOException {
        this.client = client;
        this.client.setServer(new Socket("localhost", 1312));
        this.client.setClientReader(new ClientReader(this.client.getServer(), this.client));
        this.client.getClientReader().start();
        this.client.setOut(new DataOutputStream(this.client.getServer().getOutputStream()));
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
                        sendMessage(subString);
                    }
                    return;
                }
                sendMessage(txt);
            }
        }
    }

    private void sendMessage(String message) throws IOException {
        client.sendMessage(message);
        if (client.isLoggedIn()) txtFieldClient.setText("");
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
            if (register) sendMessage("/register");
            else sendMessage("/login");
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
        sendMessage(txtFieldUsername.getText());
    }

    public void getPW() throws IOException {
        sendMessage(pwField.getText());
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
        buttonChangeLoginRegister.setVisible(false);;
        labelChangeLoginRegister.setVisible(false);
        txtFieldUsername.setVisible(false);
        pwField.setVisible(false);
        pwField2.setVisible(false);
        labelError.setVisible(false);
        register = false;
    }

    public void logOut() throws IOException {
        client.setLoggedIn(false);
        client.sendMessage("/logout");
        buttonConnect.setText("Log in");
        txtFieldClient.setEditable(false);
        labelLoginRegister.setVisible(true);
        buttonChangeLoginRegister.setVisible(true);;
        labelChangeLoginRegister.setVisible(true);
        txtFieldUsername.setVisible(true);
        pwField.setVisible(true);
        labelError.setVisible(true);
    }
}
