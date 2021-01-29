package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

public class privateChatController {
    @FXML
    TextArea textAreaShowChat;
    @FXML
    TextField textFieldMessages;

    private Client client;
    private String chatUser;

    @FXML
    private void sendMessageOnEnter(KeyEvent event){
        if (event.getCode().equals(KeyCode.ENTER)) {
            String txt = textFieldMessages.getText();
            int length = txt.length();
            if (!txt.equals("")) {
                if (length > 50) {
                    if (length > 500) {
                        textFieldMessages.setText("String is too long!");
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

    public void showMessage(String string){
        textAreaShowChat.appendText(string);
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }
}
