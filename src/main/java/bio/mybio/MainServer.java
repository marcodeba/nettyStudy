package bio.mybio;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ServerInputThread(socket)).start();
            new Thread(new ServerOutputThread(socket)).start();
        }
    }
}
