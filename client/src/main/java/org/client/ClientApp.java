package org.client;

import org.client.gui.MainWindow;
import org.client.gui.RegistrationWindow;
import org.client.util.CommandManager;
import org.common.request.UserData;
import org.client.network.Connector;

import javax.swing.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;



public class ClientApp {
    private final static InetSocketAddress serverAddress = new InetSocketAddress("localhost", 7777);
    private final static int serverPort = 5000;
    private static InputStream in;
    private static OutputStream out;
    private static Connector connector;
    private static UserData userData;

    public static void main(String[] args) {
        Socket socket = new Socket();
        try  {


            socket.connect(serverAddress, serverPort);
            socket.setSoTimeout(2000);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            connector = new Connector(out, in);

            RegistrationWindow registrationWindow = new RegistrationWindow(connector);
            MainWindow mainWindow = new MainWindow(userData, connector);
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainWindow.setVisible(true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setUserData(UserData userData) {
        ClientApp.userData = userData;
    }

}
