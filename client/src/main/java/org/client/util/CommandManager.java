package org.client.util;


import model.City;
import org.client.commands.*;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.UserData;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Класс, отвечающий за вызов нужной команды при вводе ее названия
 */
public class CommandManager {
    private static CommandManager instance;
    private HashMap<String, Command> commands = new HashMap<>();

    private CommandManager() {
        commands.put("exit", new ExitCommand());
        commands.put("show", new ShowCommand());
        commands.put("insert", new InsertCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("update", new UpdateCommand());
        commands.put("clear", new ClearCommand());
        commands.put("remove_all_by_government", new RemoveAllByGovernmentCommand());

    }

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    /**
     * Вызов нужной команды
     * @param name имя команды
     * @param connector коннектор для приема и отправки сообщений
     */
    public void executeCommand(String name, Connector connector, UserData userData, City city) {
        if (!commands.containsKey(name)) {
            throw new IllegalArgumentException("no such command");
        }
        Command command = commands.get(name);
        command.execute(connector, userData, city);
    }

    public String getDescriptions(){
        StringBuilder res = new StringBuilder();
        for (String k: commands.keySet()){
            res.append(k).append(": ").append(commands.get(k).description()).append("\n");
        }
        return res.toString();
    }

}
