package org.server.commands;


import model.City;
import org.server.network.Connector;
import org.common.request.UserData;
import org.server.interfaces.Command;
import org.server.managers.CollectionManager;
import org.server.utils.CityWithUser;
import org.server.utils.MyLogger;
import org.server.utils.UserInfo;
import serialize.SerializeCity;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Команда, выводящая объекты значение поля metersAboveSeaLevel которых меньше заданного
 */
public class FilterLessThanMetersAboveSeaLevelCommand implements Command {
    @Override
    public void execute(Connector connector, byte[] args, UserInfo userInfo) throws IllegalArgumentException{
        try{
            CollectionManager collectionManager = CollectionManager.getInstance();
            String k = new String(args);
            try{
                Float.parseFloat(k);
            } catch (Exception e){
                throw new IllegalArgumentException("incorrect data format");
            }
            float meters = Float.parseFloat(k);
            ArrayList<City> cities = collectionManager.getCollection().values().stream().map(CityWithUser::city)
                    .filter(city -> city.getMetersAboveSeaLevel() < meters)
                    .sorted()
                    .collect(Collectors.toCollection(ArrayList::new));
            connector.sendInt(cities.size());
            byte[] bytes;
            for (City city : cities) {
                bytes = SerializeCity.serialize(city);
                connector.sendInt(bytes.length);
                connector.send(bytes);
            }
            MyLogger.info("command print_field_descending_meters_above_sea_level from " + connector.getSocket().getInetAddress() + " executed successfully");
        } catch (Exception e) {
            MyLogger.info("exception in execute print_field_descending_meters_above_sea_level command from " + connector.getSocket().getInetAddress());
        }
    }
}