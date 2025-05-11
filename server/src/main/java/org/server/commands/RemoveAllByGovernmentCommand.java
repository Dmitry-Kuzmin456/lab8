package org.server.commands;


import model.City;
import model.Government;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.PermissionException;
import org.server.utils.UserInfo;

import java.util.HashSet;

/**
 * Команда, удаляющая все элементы, значение поля government которых совпадает с заданным
 */
public class RemoveAllByGovernmentCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            String government = new String(args);
            try{
                Government.fromDescription(government);
            } catch (Exception e){
                throw new IllegalArgumentException("no such government");
            }

            HashSet<Long> keys = new HashSet<>(collectionManager.getCollection().keySet());

            for (long key : keys) {
                City city = collectionManager.getCollection().get(key).city();
                if (city != null && city.getGovernment().equals(government)) {
                    try{
                        collectionManager.removeElement(key, userInfo);
                    } catch (PermissionException e){
                        MyLogger.info(e.getMessage());
                    }

                }
            }


            connector.send("successfully".getBytes());
            MyLogger.info("command remove_all_by_government from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e) {
            MyLogger.info("exception in execute remove_all_by_government command from " + connector.getSocket().getInetAddress());
        }
    }
}