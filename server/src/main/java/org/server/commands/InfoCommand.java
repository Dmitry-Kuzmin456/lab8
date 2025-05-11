package org.server.commands;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.UserDefinition;
import org.server.utils.UserInfo;

/**
 * Команда, выводящая информацию о коллекции
 */
public class InfoCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            System.out.println("info running");
            connector.send(CollectionManager.getInstance().getInfo().getBytes());
            MyLogger.info("command info from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e){
            System.out.println(e.getMessage());
            MyLogger.info("exception in execute info command from " + connector.getSocket().getInetAddress());
        }
    }
}
