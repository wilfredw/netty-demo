package com.wei.netty.time.client.netty;

import com.wei.netty.time.client.netty.linebased.LineBasedNettyTimeClient;

public class NettyTimeClientApp {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8008;
        if (null != args && args.length >= 2) {
            try {
                host = args[1];
                port = Integer.valueOf(args[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LineBasedNettyTimeClient server = new LineBasedNettyTimeClient();
        server.start(host, port);
    }
}
