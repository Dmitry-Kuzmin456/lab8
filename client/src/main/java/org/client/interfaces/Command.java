package org.client.interfaces;



import model.City;
import org.client.network.Connector;
import org.common.request.UserData;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Интерфейс для всех команд
 */
public interface Command {
    /**
     * Исполнение команды
     * @param connector для взаимодействия с сервером
     */
    void execute(Connector connector, UserData userData, City city);
    String description();
}
