package org.server.commands;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.UserInfo;

/**
 * Команда, очищающая коллекцию элементов
 */
public class ClearCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException {
        try{
            CollectionManager.getInstance().clearCollection(userInfo.login());
            connector.send("successfully".getBytes());
            MyLogger.info("command clear from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e){
            MyLogger.info("exception in execute clear command from " + connector.getSocket().getInetAddress());
        }
    }

}
