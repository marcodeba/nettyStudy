package netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    // 创建一个ChannelGroup，其是一个线程安全的集合，其中存放着与当前服务器相连接的所有Active状态的Channel
    // GlobalEventExecutor是一个单例、单线程的EventExecutor，是为了保证对当前group中的所有Channel的处理
    // 线程是同一个线程
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 只要有客户端Channel与服务端连接成功就会执行这个方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 获取到当前与服务器连接成功的channel
        Channel channel = ctx.channel();
        group.writeAndFlush("[ 客户端 ]" + channel.remoteAddress() + "上线了" + sdf.format(new java.util.Date()) + "\n");
        // 将当前channel添加到group中
        group.add(channel);
        System.out.println(channel.remoteAddress() + "上线了" + sdf.format(new java.util.Date()) + "\n");
    }

    // 只要有客户端Channel断开与服务端的连接就会执行这个方法
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 获取到当前要断开连接的Channel
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "下线了\n");
        group.writeAndFlush(channel.remoteAddress() + "下线了" + sdf.format(new java.util.Date()) + "，当前在线人数：" + group.size() + "\n");
    }

    // 只要有客户端Channel给当前的服务端发送了消息，那么就会触发该方法的执行
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取到向服务器发送消息的channel
        Channel channel = ctx.channel();
        // 这里要实现将消息广播给所有group中的客户端Channel
        // 发送给自己的消息与发送给大家的消息是不一样的
        group.forEach(ch -> {
            if (ch != channel) {
                ch.writeAndFlush("[ 客户端 ]" + channel.remoteAddress() + "：" + msg + "\n");
            } else {
                channel.writeAndFlush("me：" + msg + "\n");
            }
        });
    }

    /**
     * 当Channel中的数据在处理过程中出现异常时会触发该方法的执行
     *
     * @param ctx   上下文
     * @param cause 发生的异常对象
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}