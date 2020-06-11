package netty.gupaonetty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 * 服务端业务处理类
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Server chanelActive>>>>>>>");
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!/r/n");
        ctx.write("It is " + new Date() + "now./r/n");
        ctx.flush();
        //ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info("Server receive message:" + msg);
        ctx.channel().writeAndFlush("Server accept message " + msg);
        //ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("get server exception :" + cause.getMessage());
    }
}
