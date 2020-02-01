package com.wei.netty.time.client.netty.linebased;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class LineBasedNettyTimeClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            String requestStr = "QUERY TIME ORDER";
            byte[] requestBytes = requestStr.getBytes("UTF-8");
            ByteBuf byteBuf = Unpooled.buffer(requestBytes.length);
            byteBuf.writeBytes(requestBytes);
            ctx.writeAndFlush(byteBuf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf byteBuf = (ByteBuf)msg;
        byte[] respBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(respBytes);
        String respStr = new String(respBytes, "UTF-8");
        System.out.println("response: " + respStr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        ctx.close();
    }
}
