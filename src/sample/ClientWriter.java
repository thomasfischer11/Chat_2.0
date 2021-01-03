package sample;

import sample.Client;

import java.io.*;
import java.net.*;
import java.util.*;

// wird aktuell nicht benutzt
public class ClientWriter extends Thread {
    private Socket server;
    private Client client;
    private DataOutputStream out;
    private String message;
    private Scanner scan;


    ClientWriter (Socket server , Client client){
        this.server = server;
        this.client = client;
    }

    public void run(){
        try {
            out = new DataOutputStream(server.getOutputStream());
            scan = new Scanner(System.in);
            System.out.println("Namen eingeben: ");
            out.writeUTF(scan.nextLine());
            do {
                message = scan.nextLine();
                out.writeUTF(message);
            } while (!message.equals("disconnect"));
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
