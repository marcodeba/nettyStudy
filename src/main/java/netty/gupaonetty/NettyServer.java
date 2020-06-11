package netty.gupaonetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static final String IP = "localhost";
    private static final int PORT = 6666;
    //private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    //private static final int BIZTHREADSIZE = 100;

    // EventLoopGroup：管理事件的线程组
    // bossGroup 对应 Reactor 模式的 mainReactor ，用于服务端接受客户端的连接
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // workerGroup 对应 Reactor 模式的 subReactor ，用于进行 SocketChannel 的数据读写
    private static final EventLoopGroup workGroup = new NioEventLoopGroup();

    public static void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        // 这里的channel是 NioSocketChannel!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new TcpServerHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(IP, PORT).sync();
            channelFuture.channel().closeFuture().sync();
            logger.info("server start");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    protected static void shutdown() {
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        logger.info("启动Server...");
        NettyServer.start();
    }
}
