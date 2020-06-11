package netty.mynetty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class HelloWorldClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("client Active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("client receive Message: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}