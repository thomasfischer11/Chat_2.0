package ProbablyDontNeedThisAnymore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

/*public class ObjectStreamToClientThread extends Thread {
    ObjectInputStream in;
    Client client;
    boolean isRunning;
    ClientController clientController;

    ObjectStreamToClientThread(Client client, ClientController clientController) throws IOException {
        this.in = new ObjectInputStream(client.getServer().getInputStream());
        isRunning = true;
        this.clientController = clientController;
    }

    public void run() {
        while(isRunning) {
            Object inputObject = null;
            try {
                inputObject = in.readObject();
                if(inputObject instanceof HashMap){
                    if(((HashMap)inputObject).keySet() instanceof HashSet){
                        clientController.setRooms((HashMap<String, HashSet<ClientThread>>)inputObject);
                    }
                    if(((HashMap)inputObject).keySet() instanceof User){
                        clientController.setUsers((HashMap<String, User>)inputObject);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}*/
