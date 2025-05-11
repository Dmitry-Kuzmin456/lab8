package org.server.commands;


import model.City;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.UserDefinition;
import org.server.utils.UserInfo;
import serialize.DeserializeCity;

/**
 * Команда, добавляющая элемент в коллекцию по ключу
 */
public class InsertCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            System.out.println("Inserting city");
            CollectionManager collectionManager = CollectionManager.getInstance();
            City city = DeserializeCity.deserialize(args);
            if (!city.isValid()){
                throw new IllegalArgumentException("incorrect data in city");
            }
            Long id = city.getId();
            if (collectionManager.getCollection().containsKey(id)){
                connector.send("this id already exists".getBytes());
                MyLogger.info("command insert from " + connector.getSocket().getInetAddress() + " executed, element don't insert because id " + id + " already exists");
            } else{
                collectionManager.addElementByID(id, city, userInfo);
                MyLogger.info("command insert from " + connector.getSocket().getInetAddress() + " executed successfully");
                connector.send("successfully".getBytes());
            }
        } catch (Exception e) {
            MyLogger.info("exception in execute insert command from " + connector.getSocket().getInetAddress());
        }
    }
}
