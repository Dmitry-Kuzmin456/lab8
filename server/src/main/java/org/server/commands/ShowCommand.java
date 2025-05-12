package org.server.commands;


import model.City;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.CityWithUser;
import org.server.utils.MyLogger;
import org.server.utils.UserDefinition;
import org.server.utils.UserInfo;
import serialize.SerializeCity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда, выводящая все элементы в коллекции
 */
public class ShowCommand implements Command {

    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException {
        try{
            List<City> cities = CollectionManager.getInstance().getCollection().values().stream()
                    .peek(cwu -> cwu.city().setUserStatus(cwu.userID() == userInfo.id()))
                    .map(CityWithUser::city)
                    .collect(Collectors.toList());
            connector.sendInt(cities.size());
            Collections.sort(cities);
            byte[] bytes;
            for (City city : cities) {
                bytes = SerializeCity.serialize(city);
                connector.sendInt(bytes.length);
                connector.send(bytes);
            }
            MyLogger.info("command show from " + userInfo.login() + " executed successfully");
        } catch (Exception e){
            MyLogger.info("exception in execute show command from " + connector.getSocket().getInetAddress());
        }

    }
}
