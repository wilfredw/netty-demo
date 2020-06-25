package com.wei.netty.protobuf.common.model.dto;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;

public class TestMessage {
    private static byte[] encode(MessageDTO.Message msg) {
        return msg.toByteArray();
    }

    private static MessageDTO.Message decode(byte[] body) throws InvalidProtocolBufferException {
        return MessageDTO.Message.parseFrom(body);
    }

    private static MessageDTO.Message createMessage() {
        MessageDTO.Message.Builder messageBuilder = MessageDTO.Message.newBuilder();
        messageBuilder.setMessageId(1);
        messageBuilder.setMessageType("ping");
        messageBuilder.setData("this is data");
        ArrayList<String> param = new ArrayList<>(4);
        param.add("userid");
        param.add("source");
        messageBuilder.addAllParam(param);
        messageBuilder.setParam(1, "set 1 param");
        return messageBuilder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        MessageDTO.Message msg = createMessage();
        System.out.println("before encode " + msg.toString());
        MessageDTO.Message msgAfterDecode = decode(encode(msg));
        System.out.println("after encode " + msgAfterDecode);
    }
}
