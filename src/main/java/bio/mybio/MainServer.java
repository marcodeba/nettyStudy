package bio.mybio;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private static final int PORT = 8888;
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ServerInputThread(socket)).start();
            new Thread(new ServerOutputThread(socket)).start();
        }
    }
}
