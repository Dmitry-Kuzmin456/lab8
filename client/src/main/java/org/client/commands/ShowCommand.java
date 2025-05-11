package org.client.commands;


import model.City;
import org.client.gui.TableSynchronization;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.RequestGenerator;
import org.common.request.UserData;
import serialize.DeserializeCity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Команда, выводящая все элементы в коллекции
 */
public class ShowCommand implements Command {
    @Override
    public void execute(Connector connector, UserData userData, City city) {
        try{
            byte[] request = RequestGenerator.generateWithString("show", "", userData);
            connector.sendInt(request.length);
            connector.send(request);
            int k = connector.receiveInt();
            ArrayList<City> cities = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                int size = connector.receiveInt();
//                MyConsole.println(DeserializeCity.deserialize(connector.receive(size)) + "\n");
                cities.add(DeserializeCity.deserialize(connector.receive(size)));
            }
            TableSynchronization.setCities(cities);
        }
        catch (Exception e){
            System.out.println("error in show command: " + e.getMessage());
        }


    }

    @Override
    public String description() {
        return "shows all cities in collection";
    }

}
