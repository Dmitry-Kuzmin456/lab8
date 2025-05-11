package org.server.managers;


import org.server.network.Connector;
import org.common.request.Request;
import org.common.request.UserData;
import org.server.commands.*;
import org.server.interfaces.Command;
import org.server.utils.History;
import org.server.utils.UserInfo;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * Класс, отвечающий за вызов нужной команды при вводе ее названия
 */
public class CommandManager {
    private static CommandManager instance;
    private HashMap<String, Command> commands = new HashMap<>();

    private CommandManager() {
        commands.put("show", new ShowCommand());
        commands.put("info", new InfoCommand());
        commands.put("insert", new InsertCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_key", new RemoveKeyCommand());
        commands.put("clear", new ClearCommand());
        commands.put("history", new HistoryCommand());
        commands.put("replace_if_greater", new ReplaceIfGreaterCommand());
        commands.put("remove_greater_key", new RemoveGreaterKeyCommand());
        commands.put("remove_all_by_government", new RemoveAllByGovernmentCommand());
        commands.put("filter_less_than_meters_above_sea_level", new FilterLessThanMetersAboveSeaLevelCommand());
        commands.put("print_field_descending_meters_above_sea_level", new PrintFieldDescendingMetersAboveSeaLevelCommand());
    }

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public void executeCommand(Request request, ExecutorService responsePool, Connector connector, UserInfo userInfo) throws IllegalArgumentException {
        if (!commands.containsKey(request.name())) {
            throw new IllegalArgumentException("no such command");
        }
        System.out.println("command starts");
        Command command = commands.get(request.name());
        responsePool.submit(() -> command.execute(connector, request.data(), userInfo));
        History.add(request.name());
    }
}
