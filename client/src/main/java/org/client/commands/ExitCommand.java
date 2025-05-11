package org.client.commands;



import model.City;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.UserData;

import java.io.IOException;

/**
 * Команда, завершающая выполнение программы
 */
public class ExitCommand implements Command {
    @Override
    public void execute(Connector connector, UserData userData, City city) {
        System.exit(0);
    }

    @Override
    public String description(){
        return "stop the client application";
    }
}
