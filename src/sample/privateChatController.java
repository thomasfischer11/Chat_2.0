package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;

public class privateChatController {


    @FXML
    TextArea textAreaShowChat;
    @FXML
    TextField textFieldMessages;

    private Client client;
    private String chatUser;

    EventHandler<WindowEvent> eventHandlerClosePrivateChat = windowEvent -> {
        try {
            leaveChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    @FXML
    private void sendMessageOnEnter(KeyEvent event) throws IOException {
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
                        sendMessage(subString);
                    }
                    return;
                }
                sendMessage(txt);
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

    private void sendMessage (String message) throws IOException {
        if (client.getController().getPrivateChats().containsKey(chatUser)){
            client.sendMessagePrivate(chatUser + "+" + message);
            textFieldMessages.setText("");
            showMessage("You: " + message);
        }
        else textFieldMessages.setText("");
    }

    public void showMessage(String message){
        textAreaShowChat.appendText(message + "\n");
    }

    public void leaveChat () throws IOException {
        client.sendMessagePrivate(chatUser + "+" + "[left chat]");
        client.getController().getPrivateChats().remove(chatUser);
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

    public TextArea getTextArea(){
        return textAreaShowChat;
    }
}
