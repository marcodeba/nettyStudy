package nio.mynio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Reactor由两部分组成：1. Acceptor（负责处理client的连接请求）；2.Selector负责事件派发
 * Reactor组件：
 * 1. Acceptor：接收client连接，建立对应client的Handler，并向Reactor注册此Handler
 * 2. Handler：业务实现处理类
 * 3. Selector：Reactor事件派发者
 */
@SuppressWarnings("Duplicates")
public class ServerHandler implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean started;

    /**
     * 注册一个Acceptor事件处理器到Reactor中，Acceptor事件处理器所关注的事件是ACCEPT事件，
     * 这样Reactor会监听客户端向服务器端发起的连接请求事件(ACCEPT事件)
     */
    public ServerHandler(int port) {
        try {
            //创建选择器
            selector = Selector.open();
            //打开监听通道
            serverSocketChannel = ServerSocketChannel.open();
            //开启非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //绑定ip和端口 backlog设为1024
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            //监听客户端连接请求，将ServerSocketChannel注册到Reactor线程中的Selector上，监听ACCEPT事件
            // selector注册的单位是事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //标记服务器已开启
            started = true;
            System.out.println("服务器已启动，" + new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        started = false;
    }

    public void run() {
        //Selector轮询准备就绪的key
        while (started) {
            try {
                // 阻塞等待
                int n = selector.select();
                if (n == 0) {
                    continue;
                }

                //阻塞,只有当至少一个注册的事件发生的时候才会继续.
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        //selector关闭后会自动释放里面管理的资源
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            /**
             * Acceptor处理器通过accept()方法得到与这个客户端对应的连接(SocketChannel)，
             * 然后将该连接所关注的READ事件以及对应的READ事件处理器注册到Reactor中，
             * 这样一来Reactor就会监听该连接的READ事件了
             */
            if (key.isAcceptable()) {
                // Acceptor处理器通过accept()方法得到与这个客户端对应的连接(SocketChannel)
                SocketChannel sc = serverSocketChannel.accept();
                // 设置为非阻塞的
                sc.configureBlocking(false);
                // 将该连接所关注的READ事件以及对应的READ事件处理器注册到Reactor中，这样一来Reactor就会监听该连接的READ事件了
                sc.register(selector, SelectionKey.OP_READ);
            }
            // 当Reactor监听到有读或者写事件发生时，将相关的事件派发给对应的处理器进行处理
            if (key.isReadable()) {
                // 通过SelectionKey可以获取就绪Channel的集合，进行后续的I/O操作
                SocketChannel sc = (SocketChannel) key.channel();
                //创建ByteBuffer，并开辟一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                //读取请求码流，返回读取到的字节数
                int readBytes = sc.read(buffer);
                //读取到字节，对字节进行编解码
                if (readBytes > 0) {
                    //将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                    buffer.flip();
                    //根据缓冲区可读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    //将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String expression = new String(bytes, "UTF-8");
                    System.out.println("服务器收到消息：" + expression);
                    //发送应答消息
                    doWrite(sc, expression);
                } else if (readBytes < 0) {
                    //链路已经关闭，释放资源
                    key.cancel();
                    sc.close();
                }
            }
        }
    }

    //异步发送应答消息
    private void doWrite(SocketChannel channel, String expression) throws IOException {
        //将消息编码为字节数组
        byte[] bytes = expression.getBytes();
        //根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        //flip操作
        writeBuffer.flip();
        //发送缓冲区的字节数组
        channel.write(writeBuffer);
    }
}