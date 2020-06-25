package com.wei.netty.protobuf.client;

import com.wei.netty.protobuf.common.model.dto.MessageDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ProtoBufClientHandler extends SimpleChannelInboundHandler {
    private volatile AtomicInteger messageSeq = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active at " + new Date());
        for(int i = 0; i < 10; ++i) {
            ctx.write(createMessage());
        }
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inactive at " + new Date());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("receive " + msg.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete ");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private MessageDTO.Message createMessage() {
        MessageDTO.Message.Builder messageBuilder = MessageDTO.Message.newBuilder();
        messageBuilder.setMessageId(messageSeq.addAndGet(1));
        messageBuilder.setMessageType("pong");
        messageBuilder.setData("client");
        ArrayList<String> param = new ArrayList<>(4);
        param.add("userid");
        param.add("source");
        messageBuilder.addAllParam(param);
        messageBuilder.setParam(1, "set 1 param");
        return messageBuilder.build();
    }
}
