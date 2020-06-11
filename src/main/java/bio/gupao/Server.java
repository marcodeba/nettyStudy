package bio.gupao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private static int DEFAULT_PORT = 12345;
    private static ServerSocket serverSocket = null;

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    private synchronized static void start(int port) throws IOException {
        if (serverSocket != null) { return; }

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                // 在调用ServerSocket.accept()方法时，会一直阻塞到有客户端连接才会返回
                // 线程阻塞，accept()方法会acquireFD，锁住FD，有请求过来且accept后releaseFD()
                Socket socket = serverSocket.accept();
                logger.info("new connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
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
