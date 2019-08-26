package com.wei.netty.websocket.client;

import com.wei.netty.websocket.client.handler.WebSocketClientHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.net.URI;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class WebSocketClient {

    private static Channel ch;
    private static final EventLoopGroup group = new NioEventLoopGroup();

    public static void main(String[] args) {
        URI uri = null;
        try {
            uri = new URI("ws://localhost:9003/ws");

            Bootstrap b = new Bootstrap();
            String protocol = uri.getScheme();
            if (!"ws".equals(protocol)) {
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
            }

            // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
            // If you change it to V00, ping is not supported and remember to change
            // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
            final WebSocketClientHandler2 handler =
                    new WebSocketClientHandler2(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false, HttpHeaders.EMPTY_HEADERS, 1280000));

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("idle", new IdleStateHandler(15, 10, 0));
                            pipeline.addLast("http-codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("ws-handler", handler);
                        }
                    });

            //System.out.println("WebSocket Client connecting");
            ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
            handler.handshakeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        //System.out.println("WebSocket Client sending close");
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
        //group.shutdownGracefully();
    }

    public void eval(final String text) throws IOException {
        ch.writeAndFlush(new TextWebSocketFrame(text));
    }
}