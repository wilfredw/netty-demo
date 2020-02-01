package com.wei.netty.time.server.netty.linebased;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class LineBasedChildChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LineBasedNettyTimeServerHandler());
    }
}
