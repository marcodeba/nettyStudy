package bio.mybio;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainClient {

    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_IP = "localhost";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(DEFAULT_IP, DEFAULT_PORT);
            new Thread(new ClientOutputThread(socket)).start();
            new Thread(new ClientInputThread(socket)).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
