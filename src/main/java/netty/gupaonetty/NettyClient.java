package netty.gupaonetty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyClient implements Runnable {

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(new NettyClient(), ">>> this thread " + i).start();
        }
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder",
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast("frameEncoder", new LengthFieldPrepender(4))
                                    .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast("handler", new MyClientHandler());
                        }
                    });
            for (int i = 0; i < 10; i++) {
                // 客户端连接
                ChannelFuture f = bootstrap.connect("127.0.0.1", 6666).sync();
                // 发送数据
                f.channel().writeAndFlush("hello service !" + Thread.currentThread().getName() + ":---->" + i);
                f.channel().closeFuture().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
