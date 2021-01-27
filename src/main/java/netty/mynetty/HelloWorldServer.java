package netty.mynetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class HelloWorldServer {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldServer.class);
    private int port;

    public HelloWorldServer(int port) {
        this.port = port;
    }

    public void start() {
        /**
         * NioEventLoopGroup就是一个线程组，每个NioEventLoop都有一个selector,用于监听注册在其上的socketChannel的网络通讯
         *
         * 每个Boss NioEventLoop线程内部循环执行的步骤有 3 步
         * 1. 处理accept事件 , 与client 建立连接 , 生成 NioSocketChannel
         * 2. 将NioSocketChannel注册到某个worker NIOEventLoop上的selector
         * 3. 处理任务队列的任务，即runAllTasks
         * 每个worker NIOEventLoop线程循环执行的步骤.
         * 1. 轮询注册到自己selector上的所有NioSocketChannel 的read, write事件
         * 2. 处理 I/O 事件，即read, write 事件，在对应NioSocketChannel 处理业务
         * 3. runAllTasks处理任务队列TaskQueue的任务，一些耗时的业务处理一般可以放入TaskQueue中慢慢处理，这样不影响数据在 pipeline 中的流动处理
         *
         * 每个worker NIOEventLoop处理NioSocketChannel业务时，会使用 pipeline (管道)，管道中维护了很多 handler 处理器用来处理 channel 中的数据
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    /**
                     * handler 字段与 accept 过程有关, 即这个 handler 负责处理客户端的连接请求;
                     * 而 childHandler 就是负责和客户端的连接的 IO 交互
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new HelloWorldServerHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口服务启动，开始接收进来的连接。一个端口只能绑定一个BossGroup
            // 启动服务器并绑定端口，bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture future = serverBootstrap.bind(port).sync();
//            future.addListener((ChannelFutureListener) channelFuture -> {
//                if (future.isSuccess()) {
//                    logger.info("监听端口" + port + "成功");
//                } else {
//                    logger.info("监听端口" + port + "失败");
//                }
//            });
            logger.info("Server start listen at " + port);
            // 监听关闭事件
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new HelloWorldServer(8080).start();
    }
}