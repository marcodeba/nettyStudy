package bio.mybio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerInputThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerInputThread.class);
    private Socket socket = null;

    public ServerInputThread(Socket socket) {
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
                logger.info(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
            if (is != null) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                is = null;
            }
        }
    }
}
