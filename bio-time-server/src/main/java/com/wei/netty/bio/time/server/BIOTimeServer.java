package com.wei.netty.bio.time.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOTimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8008;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                socket = serverSocket.accept();
                Thread workThread = new Thread(new TimeServerHandler(socket));
                workThread.start();
            }
        } finally {
            if (serverSocket != null) {
                System.out.println("The time server close.");
                serverSocket.close();
                serverSocket = null;
            }
        }
    }
}
