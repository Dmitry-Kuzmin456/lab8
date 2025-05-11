package org.server.commands;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.PermissionException;
import org.server.utils.UserInfo;

import java.util.HashSet;

/**
 * Команда, удаляющая все элементы с id больше заданного
 */
public class RemoveGreaterKeyCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            Long value = Long.parseLong(new String(args));
            HashSet<Long> keys = new HashSet<>(collectionManager.getCollection().keySet());
            for (Long key : keys) {
                if (key > value){
                    try{
                        collectionManager.removeElement(key, userInfo);
                    } catch (PermissionException e){
                        System.out.println(e.getMessage());
                    }

                }
            }
            connector.send("successfully".getBytes());
            MyLogger.info("command remove_greater_key from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e) {
            MyLogger.info("exception in execute remove_greater_key command from " + connector.getSocket().getInetAddress());
        }
    }

}