package bio.gupao;

import java.io.IOException;
import java.util.Random;

public class Test {
    //测试主方法
    public static void main(String[] args) throws InterruptedException {
        // 启动服务器端
        new Thread(new Runnable() {
            public void run() {
                try {
                    Server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(100);

        final char[] operators = {'+', '-', '*', '/'};
        final Random random = new Random(System.currentTimeMillis());
        // 启动客户端
        new Thread(new Runnable() {
            @SuppressWarnings("static-access")
            public void run() {
                while (true) {
                    String expression = random.nextInt(10) + "" + operators[random.nextInt(4)] + (random.nextInt(10) + 1);
                    try {
                        Client.send(expression);
                        Thread.currentThread().sleep(random.nextInt(1000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

