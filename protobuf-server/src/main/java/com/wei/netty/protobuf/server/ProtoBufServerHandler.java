package com.wei.netty.protobuf.server;

import com.wei.netty.protobuf.common.model.dto.MessageDTO;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProtoBufServerHandler extends SimpleChannelInboundHandler {
    private volatile AtomicInteger messageSeq = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object data) throws Exception {
        MessageDTO.Message msg = (MessageDTO.Message)data;
        System.out.println("server receive\n" + msg.toString());
        ctx.writeAndFlush(createMessage());
    }

    private MessageDTO.Message createMessage() {
        MessageDTO.Message.Builder messageBuilder = MessageDTO.Message.newBuilder();
        messageBuilder.setMessageId(messageSeq.addAndGet(1));
        messageBuilder.setMessageType("ping");
        messageBuilder.setData("this is data");
        ArrayList<String> param = new ArrayList<>(4);
        param.add("userid");
        param.add("source");
        messageBuilder.addAllParam(param);
        messageBuilder.setParam(1, "set 1 param");
        return messageBuilder.build();
    }
}
