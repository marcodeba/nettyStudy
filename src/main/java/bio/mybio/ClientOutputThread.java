package bio.mybio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientOutputThread implements Runnable {//extends Thread {
    private Socket socket = null;

    public ClientOutputThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            OutputStream os = socket.getOutputStream();
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                String line = br.readLine();
                os.write(line.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
