package bio.gupao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int DEFAULT_PORT = 12345;

    private static ServerSocket serverSocket = null;

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    public synchronized static void start(int port) throws IOException {
        if (serverSocket != null) {
            return;
        }
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        }
    }
}
