package org.common.request;




import model.City;
import serialize.SerializeCity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Генератор для создания сериализованного запроса
 */
public class RequestGenerator {

    public static byte[] generate(String name, byte[] args, UserData userData) throws IOException {
        Request request = new Request(name, args, userData);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] generateWithString(String name, String value, UserData userData) throws Exception {
        return generate(name, value.getBytes(), userData);
    }

    public static byte[] generateWithCity(String name, City city, UserData userData) throws Exception {
        byte[] citySerialized = SerializeCity.serialize(city);
        return generate(name, citySerialized, userData);
    }

    public static byte[] generateUserDataRequest(char type, String login, String password) throws Exception {
        UserDataRequest request = new UserDataRequest(type, login, password);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();

        return byteArrayOutputStream.toByteArray();
    }
}
