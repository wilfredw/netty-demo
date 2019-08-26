package com.wei.netty.websocket.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/*
1.扩展 SimpleChannelInboundHandler 用于处理 TextWebSocketFrame 信息
2.覆写userEventTriggered() 方法来处理自定义事件
3.如果接收的事件表明握手成功,就从 ChannelPipeline 中删除HttpRequestHandler ，因为接下来不会接受 HTTP 消息了
4.写一条消息给所有的已连接 WebSocket 客户端，通知它们建立了一个新的 Channel 连接
5.添加新连接的 WebSocket Channel 到 ChannelGroup 中，这样它就能收到所有的信息
6.保留收到的消息，并通过 writeAndFlush() 传递给所有连接的客户端。
上面显示了 TextWebSocketFrameHandler 仅作了几件事：
当WebSocket 与新客户端已成功握手完成，通过写入信息到 ChannelGroup 中的 Channel 来通知所有连接的客户端，然后添加新 Channel 到 ChannelGroup
如果接收到 TextWebSocketFrame，调用 retain() ，并将其写、刷新到 ChannelGroup，使所有连接的 WebSocket Channel 都能接收到它。和以前一样，retain() 是必需的，因为当 channelRead0（）返回时，TextWebSocketFrame 的引用计数将递减。由于所有操作都是异步的，writeAndFlush() 可能会在以后完成，我们不希望它访问无效的引用。
由于 Netty 在其内部处理了其余大部分功能，唯一剩下的需要我们去做的就是为每一个新创建的 Channel 初始化 ChannelPipeline 。要完成这个，我们需要一个ChannelInitializer
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> { //1
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {    //2
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {

            ctx.pipeline().remove(HttpRequestHandler.class);    //3

            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));//4

            group.add(ctx.channel());    //5
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain());    //6
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("HttpRequestHandler exceptionCaught");
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }
}
