package com.wei.netty.nio.time.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public NIOTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", port), 10240);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is create in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    public void run() {
        while (!stop) {
            try {
                selector.select(100);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeySet.iterator();
                SelectionKey selectionKey = null;
                while (it.hasNext()) {
                    selectionKey = it.next();
                    it.remove();
                    try {
                        handleSelected(selectionKey);
                    } catch (Exception e) {
                        if (null != selectionKey) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (null != selector) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuffer messageSB = new StringBuffer();

    public void handleSelected(SelectionKey selectionKey) throws IOException {
        if (null != selectionKey && selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            } else if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int totalReadBytes = 0;
                while (true) {
                    int readBytes = socketChannel.read(byteBuffer);
                    totalReadBytes = totalReadBytes + readBytes;
                    if (readBytes > 0) {
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        String message = new String(bytes, "UTF-8");
                        messageSB.append(message);
                    } else {
                        break;
                    }
                }
                if (totalReadBytes > 0) {
                    String request = decodeReqeuest(messageSB);
                    if (null != request) {
                        System.out.println("Thre time server receive request: " + request);
                        String response = "QUERY TIME ORDER".equalsIgnoreCase(request.replaceAll("\r\n","")) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                        writeResponse(socketChannel, response);
                    }
                } else if (totalReadBytes < 0) {
                    System.out.println("close conn");
                    selectionKey.cancel();
                    socketChannel.close();
                } else {
                    ;
                }
            } else if (selectionKey.isWritable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                writeResponse(socketChannel);
            }
        }
    }

    public String decodeReqeuest(StringBuffer sb) {
        String message = sb.toString();
        sb.delete(0, sb.length());
        return message;
    }

    private ByteBuffer writeByteBuffer;

    public void writeResponse(SocketChannel socketChannel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes("UTF-8");
            writeByteBuffer = ByteBuffer.allocate(bytes.length);
            writeByteBuffer.put(bytes);
            writeByteBuffer.flip();

            writeResponse(socketChannel);

        }
    }

    public void writeResponse(SocketChannel socketChannel) throws IOException {


        int writeBytes = socketChannel.write(writeByteBuffer);
        if (writeBytes >= 0) {
            if (writeByteBuffer.hasRemaining()) {
                socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
        } else {
            socketChannel.close();
        }


    }
}
