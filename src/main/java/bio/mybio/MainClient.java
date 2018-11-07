package bio.mybio;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            new ClientOutputThread(socket).start();
            new ClientInputThread(socket).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
