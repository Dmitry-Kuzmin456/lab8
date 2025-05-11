package org.server;

import org.server.managers.ClientHandler;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private final static ExecutorService responsePool = Executors.newCachedThreadPool();
    private final static int PORT = 7777;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            MyLogger.info("Server started on port " + PORT);
            while(true){
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, responsePool)).start();
            }
        } catch (Exception e){
            System.out.println("error " + e.getMessage());
        }
    }
}
