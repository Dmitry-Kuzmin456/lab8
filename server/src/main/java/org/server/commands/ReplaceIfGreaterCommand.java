package org.server.commands;


import model.City;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.PermissionException;
import org.server.utils.UserInfo;
import serialize.DeserializeCity;

/**
 * Команда, заменяющая элемент, в случае, если он больше исходного
 */
public class ReplaceIfGreaterCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            City city = DeserializeCity.deserialize(args);
            if (!city.isValid()){
                throw new IllegalArgumentException("incorrect data in city");
            }
            Long id = city.getId();
            if (!collectionManager.getCollection().containsKey(id)){
                connector.send("this id not in collection".getBytes());
                MyLogger.info("command replace_if_greater from " + connector.getSocket().getInetAddress() +
                        " executed , element not replaced, because it isn't exist");
            } else{
                if (city.compareTo(collectionManager.getCollection().get(id).city()) > 0){
                    try{
                        collectionManager.removeElement(id, userInfo);
                    } catch (PermissionException e){
                        connector.send("element wasn't replace because you haven't permission on it's update".getBytes());
                        MyLogger.info("command replace_if_greater from " + connector.getSocket().getInetAddress() +
                                " executed , element not replaced, because user haven't permission on it's update");
                    }

                    collectionManager.addElementByID(id, city, userInfo);
                    connector.send("element was replaced".getBytes());
                    MyLogger.info("command replace_if_greater from " + connector.getSocket().getInetAddress()+ " executed , element replaced");
                } else{
                    connector.send("element wasn't replace because it isn't greater".getBytes());
                    MyLogger.info("command replace_if_greater from " + connector.getSocket().getInetAddress() +
                            " executed , element not replaced, because it isn't greater");
                }


            }
        } catch (Exception e) {
            MyLogger.info("exception in execute replace_if_greater command from " + connector.getSocket().getInetAddress());
        }
    }
}
