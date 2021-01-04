package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;

public class ServerController {

    private Server server;
    @FXML
    private TextField txtFieldServer;
    @FXML
    private TextArea txtAreaServer;
    @FXML
    private Button buttonStartStop;

    public void setServer(Server server) throws IOException {
        this.server = server;
        server.setServerOnline();
    }

    @FXML
    private void sendMessageOnEnter (KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)){
            String txt = txtFieldServer.getText();
            int length = txt.length();
            if (!txt.equals("")){
                if (length > 50){
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
    private void onStartStopClicked (MouseEvent event) throws IOException {
        executeCommand("/stop");
    }

    private void showMessage(String s) throws IOException {
        if (s.startsWith("/")) executeCommand(s);
        else server.sendToAll("[Server]: " + s);
        if (server.isOnline()) txtFieldServer.setText("");
    }

    private void executeCommand(String command) throws IOException{
        if (command.equals("/stop")){
            server.stopServer();
            return;
        }
        if (command.startsWith("/kick")){
            String name = command.substring(6);
            for (ClientThread aUser : server.getClientThreads()) {
                if(aUser.getClientName().equals(name)){
                    aUser.sendMessage("You were kicked from the Server");
                    aUser.logout();
                }
            }
        }
    }

    public TextArea getTxtAreaServer() {
        return txtAreaServer;
    }
}
