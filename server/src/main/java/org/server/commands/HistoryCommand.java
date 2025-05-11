package org.server.commands;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.utils.History;
import org.server.utils.MyLogger;
import org.server.utils.UserInfo;

/**
 * Команда, выводящая последние 15 исполненных команд
 */
public class HistoryCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException {
        try{
            connector.send(History.getHistory().getBytes());
            MyLogger.info("command history from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e){
            MyLogger.info("exception in execute history command from " + connector.getSocket().getInetAddress());
        }
    }
}