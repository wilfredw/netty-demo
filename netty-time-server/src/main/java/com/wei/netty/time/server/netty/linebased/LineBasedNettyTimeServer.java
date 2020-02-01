package com.wei.netty.time.server.netty.linebased;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class LineBasedNettyTimeServer {
    public void start(int port) {
        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 65536)
                    .childHandler(new LineBasedChildChannelHandler());
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }
    }
}
