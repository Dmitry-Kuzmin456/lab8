package org.server.interfaces;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.utils.UserInfo;

import java.util.concurrent.ExecutorService;

/**
 * Интерфейс для всех команд
 */
public interface Command {
    /**
     *
     * @param args аргументы, переданные команде
     * @throws IllegalArgumentException для проверки валидности данных
     */
    void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException;
}
