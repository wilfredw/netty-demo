package com.wei.netty.time.server.netty;

import com.wei.netty.time.server.netty.linebased.LineBasedNettyTimeServer;

public class NettyTimeServerApp {
    public static void main(String[] args) {
        int port = 8008;
        if (null != args && args.length > 0) {
            try {
                port = Integer.valueOf(args[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LineBasedNettyTimeServer server = new LineBasedNettyTimeServer();
        server.start(port);
    }
}
