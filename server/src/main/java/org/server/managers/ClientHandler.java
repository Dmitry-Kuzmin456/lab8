package org.server.managers;

import org.server.network.Connector;
import org.common.request.Request;
import org.common.request.RequestReader;
import org.common.request.UserData;
import org.server.utils.MyLogger;
import org.server.utils.UserDefinition;
import org.server.utils.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ExecutorService responsePool;
    private UserData rightUserData;

    public ClientHandler(Socket clientSocket, ExecutorService responsePool) {
        this.clientSocket = clientSocket;
        this.responsePool = responsePool;

    }

    @Override
    public void run() {
        try(InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream()) {
            Connector connector = new Connector(outputStream, inputStream, clientSocket);
            MyLogger.info("New connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());
            UserData userData = null;

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead;

            try{

                userData = UserDefinition.definition(connector);

//                CollectionManager.getInstance().setUser(userData);
               UserInfo userInfo = new UserInfo(userData.login(), userData.password(), CollectionManager.getInstance().getUserId(userData.login()));
                System.out.println(userInfo.id());
                while (true) {
                    int rl = connector.receiveInt();
                    byte[] bytes = connector.receive(rl);
                    Request request = RequestReader.read(bytes);
                    if (!CollectionManager.getInstance().checkUser(request.userData().login(), request.userData().password(), userInfo)) {
                        throw new Exception("Error in user authentication");
                    }
                    new Thread(new RequestProcessor(request, responsePool, connector, userInfo)).start();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            } finally {
                MyLogger.info("Closing connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());
            }


        } catch (Exception e){
            System.out.println(e.getMessage());
        }



    }
}
