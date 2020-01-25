package com.wei.netty.nio.time.server;

public class NIOTimeServerApp {
    public static void main(String[] args) {
        int port = 8008;
        if (null != args && args.length >= 1) {
            try {
                port = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        NIOTimeServer nioTimeServer = new NIOTimeServer(port);
        new Thread(nioTimeServer, "NIO Time Server").start();

    }
}
