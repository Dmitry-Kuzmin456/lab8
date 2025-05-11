package org.server.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Класс для получения и отправки сообщений серверу
 */
public class Connector {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket socket;

    public Connector(OutputStream out, InputStream in) {
        this.outputStream = out;
        this.inputStream = in;
    }

    public Connector(OutputStream out, InputStream in, Socket socket) {
        this.outputStream = out;
        this.inputStream = in;
        this.socket = socket;
    }

    public void send(byte[] data) throws IOException {
        outputStream.write(data);
        outputStream.flush();
    }

    public void sendInt(int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        send(buffer.array());
    }

    public byte[] receive() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead == -1) throw new IOException("Stream closed");
        return Arrays.copyOf(buffer, bytesRead);
    }

    public int receiveInt() throws IOException {
        byte[] intBuffer = receive(4);
        return ByteBuffer.wrap(intBuffer).getInt();
    }

    public byte[] receive(int size) throws IOException {
        byte[] buffer = new byte[size];
        int totalRead = 0;

        while (totalRead < size) {
            int bytesRead = inputStream.read(buffer, totalRead, size - totalRead);
            if (bytesRead == -1) throw new IOException("Stream closed");
            totalRead += bytesRead;
        }
        return buffer;
    }

    public String receiveString() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        return new String(Arrays.copyOf(buffer, bytesRead));
    }

    public Socket getSocket() {
        return socket;
    }
}

