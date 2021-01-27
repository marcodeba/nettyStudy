package netty.mynetty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class HelloWorldClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldClientHandler.class);

    // 当客户端链接服务器完成，就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("client Active");
    }

    // 当通道有读取事件时会触发，即服务端发送数据给客户端
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