package com.wei.netty.time.server.netty.linebased;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

public class LineBasedNettyTimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("The time server receive order: " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
                ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf respBuf = Unpooled.copiedBuffer(currentTime.getBytes("UTF-8"));
        ctx.write(respBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        t.printStackTrace();
        ctx.close();
    }
}
