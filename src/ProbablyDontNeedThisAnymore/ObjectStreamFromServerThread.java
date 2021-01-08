package ProbablyDontNeedThisAnymore;

import sample.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ObjectStreamFromServerThread extends Thread {
    ObjectOutputStream out;
    ObjectInputStream in;
    Server server;
    boolean isRunning;

    ObjectStreamFromServerThread(Socket client, Server server) throws IOException {
        this.in = new ObjectInputStream(client.getInputStream());
        this.out = new ObjectOutputStream(client.getOutputStream());
        isRunning = true;
        this.server = server;
    }

    public void run(){
        while(isRunning){
            try {
                Object inputObject = in.readObject();
                if((inputObject instanceof Integer)){
                    if(inputObject.equals(987654321)){
                        out.writeObject(server.getRooms());
                    }
                    if(inputObject.equals(123456789)){
                        out.writeObject(server.getUsers());
                    }
                }
            }
            catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); }
        }


    }

}
