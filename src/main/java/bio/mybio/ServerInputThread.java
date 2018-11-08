package bio.mybio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerInputThread implements Runnable {
    private Socket socket = null;

    public ServerInputThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream is = socket.getInputStream();
            while (true) {
                byte[] buffer = new byte[1024];
                int length = is.read(buffer);
                String str = new String(buffer, 0, length);
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
