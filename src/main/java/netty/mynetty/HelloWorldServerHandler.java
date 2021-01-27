package netty.mynetty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldServerHandler.class);

    // 读取客户端发送的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("server channelRead..");
        logger.info(ctx.channel().remoteAddress() + "->Server :" + msg.toString());
        ctx.write("server write " + "Hello Netty Client, I am a common server");
        ctx.flush();
    }

    // 异常处理，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel active");
    }
}
