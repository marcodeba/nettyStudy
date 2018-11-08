package bio.mybio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientInputThread implements Runnable {
    private Socket socket;

    public ClientInputThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
            while (true) {
                byte[] buffer = new byte[1024];
                int length = is.read(buffer);
                String str = new String(buffer, 0, length);
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
    }
}
