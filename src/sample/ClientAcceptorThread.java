package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;

public class ClientAcceptorThread extends Thread {

    private Server server;
    private ServerSocket serverSocket;
    private volatile boolean isRunning;

    public ClientAcceptorThread(Server server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        this.isRunning = true;
    }

    public void run() {
        while(isRunning) {
            ClientThread clThread = null;
            try {
                clThread = new ClientThread(serverSocket.accept(), server);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert clThread != null;
            clThread.start();
            server.getClientThreads().add(clThread);
        }

    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
