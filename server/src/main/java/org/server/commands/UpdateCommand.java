package org.server.commands;

import model.City;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.PermissionException;
import org.server.utils.UserDefinition;
import org.server.utils.UserInfo;
import serialize.DeserializeCity;

/**
 * Команда, обновляющая элемент коллекции по ключу
 */
public class UpdateCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            City city = DeserializeCity.deserialize(args);
            if (!city.isValid()){
                MyLogger.info("exception in execute update command from " + connector.getSocket().getInetAddress()
                + "because city is not valid");
            }
            Long id = city.getId();
            if (!collectionManager.getCollection().containsKey(id)){
                connector.send("this id not in collection".getBytes());
                MyLogger.info("command update from " + connector.getSocket().getInetAddress() + " executed , " +
                        "element not updated, because it does not exist");
            } else{
                try{
                    collectionManager.removeElement(id, userInfo);
                    collectionManager.addElementByID(id, city, userInfo);
                    connector.send("successfully".getBytes());
                    MyLogger.info("command update from " + connector.getSocket().getInetAddress() + " executed, element updated");
                } catch (PermissionException e){
                    connector.send("element wasn't update because you haven't permission on it's update".getBytes());
                    MyLogger.info("command update from " + connector.getSocket().getInetAddress() +
                            " executed , element not replaced, because user haven't permission on it's update");

                }


            }
        } catch (Exception e) {
            MyLogger.info("exception in execute update command from " + connector.getSocket().getInetAddress());

        }
    }
}
