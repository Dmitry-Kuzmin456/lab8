package org.server.commands;


import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.MyLogger;
import org.server.utils.PermissionException;
import org.server.utils.UserInfo;

/**
 * Команда, удаляющая элемент по ключу
 */
public class RemoveKeyCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            Long id = Long.parseLong(new String(args));
            if (!collectionManager.getCollection().containsKey(id)){
                connector.send("this id not in collection".getBytes());
                MyLogger.info("command remove_key from " + connector.getSocket().getInetAddress() + " executed, id not in collection");
            } else{
                try{
                    collectionManager.removeElement(id, userInfo);
                } catch (PermissionException e){
                    connector.send("You haven't permission on update this element".getBytes());
                    MyLogger.info("exception in execute remove_key command from " + connector.getSocket().getInetAddress() + " because user haven't permissions on update this element");
                }

                connector.send("successfully".getBytes());
                MyLogger.info("command remove_key from " + connector.getSocket().getInetAddress() + " executed successfully");
            }
        } catch (Exception e) {
            MyLogger.info("exception in execute remove_key command from " + connector.getSocket().getInetAddress());
        }
    }

}