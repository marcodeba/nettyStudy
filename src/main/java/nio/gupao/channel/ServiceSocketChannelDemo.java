package nio.gupao.channel;

import nio.gupao.buffer.Buffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/*服务器端，接收客户端发送过来的数据并显示，
 *服务器把上接收到的数据加上"echo from service:"再发送回去*/
@SuppressWarnings("ALL")
public class ServiceSocketChannelDemo {
    private static final Logger logger = LoggerFactory.getLogger(ServiceSocketChannelDemo.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        Thread thread = new Thread(new TCPEchoServer(8080));
        thread.start();
        Thread.sleep(100000);
        /*结束服务器线程*/
        thread.interrupt();
    }

    public static class TCPEchoServer implements Runnable {
        /*服务器地址*/
        private InetSocketAddress localAddress;

        public TCPEchoServer(int port) throws IOException {
            this.localAddress = new InetSocketAddress(port);
        }

        public void run() {
            Charset utf8 = Charset.forName("UTF-8");
            ServerSocketChannel serverSocketChannel = null;
            Selector selector = null;
            Random rnd = new Random();

            try {
                /*创建服务器通道*/
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.bind(localAddress, 100);
                /*创建选择器*/
                selector = Selector.open();
                /*服务器通道只能对tcp链接事件感兴趣*/
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e1) {
                logger.info("server start failed");
                return;
            }

            logger.info("server start with address : " + localAddress);

            /*服务器线程被中断后会退出*/
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    int n = selector.select();
                    if (n == 0) {
                        continue;
                    }

                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> it = keySet.iterator();
                    SelectionKey key = null;
                    while (it.hasNext()) {
                        key = it.next();
                        /*防止下次select方法返回已处理过的通道*/
                        it.remove();

                        /*若发现异常，说明客户端连接出现问题,但服务器要保持正常*/
                        try {
                            /*ssc通道只能对链接事件感兴趣*/
                            if (key.isAcceptable()) {
                                /*accept方法会返回一个普通通道，
                                     每个通道在内核中都对应一个socket缓冲区*/
                                //ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                                SocketChannel socketChannel = serverSocketChannel.accept();
                                socketChannel.configureBlocking(false);
                                /*向选择器注册这个通道和普通通道感兴趣的事件，同时提供这个新通道相关的缓冲区*/
                                socketChannel.register(selector, SelectionKey.OP_READ, new Buffers(256, 256));

                                logger.info("accept from " + socketChannel.getRemoteAddress());
                            }

                            /*（普通）通道感兴趣读事件且有数据可读*/
                            if (key.isReadable()) {
                                /*通过SelectionKey获取通道对应的缓冲区*/
                                Buffers buffers = (Buffers) key.attachment();
                                ByteBuffer readBuffer = buffers.getReadBuffer();
                                ByteBuffer writeBuffer = buffers.gerWriteBuffer();
                                /*通过SelectionKey获取对应的通道*/
                                SocketChannel sc = (SocketChannel) key.channel();
                                /*从底层socket读缓冲区中读入数据*/
                                sc.read(readBuffer);
                                readBuffer.flip();

                                /*解码显示，客户端发送来的信息*/
                                CharBuffer cb = utf8.decode(readBuffer);
                                logger.info(Arrays.toString(cb.array()));

                                readBuffer.rewind();

                                /*准备好向客户端发送的信息*/
                                /*先写入"echo:"，再写入收到的信息*/
                                writeBuffer.put("echo from service:".getBytes("UTF-8"));
                                writeBuffer.put(readBuffer);

                                readBuffer.clear();
                                /*设置通道写事件*/
                                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                            }

                            /*通道感兴趣写事件且底层缓冲区有空闲*/
                            if (key.isWritable()) {
                                Buffers buffers = (Buffers) key.attachment();
                                ByteBuffer writeBuffer = buffers.gerWriteBuffer();
                                writeBuffer.flip();

                                SocketChannel sc = (SocketChannel) key.channel();
                                int len = 0;
                                while (writeBuffer.hasRemaining()) {
                                    len = sc.write(writeBuffer);
                                    /*说明底层的socket写缓冲已满*/
                                    if (len == 0) {
                                        break;
                                    }
                                }

                                writeBuffer.compact();
                                /*说明数据全部写入到底层的socket写缓冲区*/
                                if (len != 0) {
                                    /*取消通道的写事件*/
                                    key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                                }
                            }
                        } catch (IOException e) {
                            logger.info("service encounter client error");
                            /*若客户端连接出现异常，从Seletcor中移除这个key*/
                            key.cancel();
                            key.channel().close();
                        }
                    }
                    Thread.sleep(rnd.nextInt(500));
                }
            } catch (InterruptedException e) {
                logger.info("serverThread is interrupted");
            } catch (IOException e1) {
                logger.info("serverThread selecotr error");
            } finally {
                if (selector != null) {
                    try {
                        selector.close();
                        selector = null;
                    } catch (IOException e) {
                        logger.info("selector close failed");
                    }
                }
                if (serverSocketChannel != null) {
                    try {
                        serverSocketChannel.close();
                        serverSocketChannel = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}