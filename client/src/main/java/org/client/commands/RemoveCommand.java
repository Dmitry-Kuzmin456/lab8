package org.client.commands;

import model.City;
import org.client.gui.CityTableModel;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.RequestGenerator;
import org.common.request.UserData;

import java.util.Collections;
import java.util.List;


public class RemoveCommand implements Command {
    public void execute(Connector connector, UserData userData, City city) {
        try{
            System.out.println(CityTableModel.getSelectedCityIds());
            for (long id: CityTableModel.getSelectedCityIds()){
                byte[] request = RequestGenerator.generateWithString("remove_key", String.valueOf(id), userData);
                connector.sendInt(request.length);
                connector.send(request);

            }



        }
        catch (Exception e){
            System.out.println("error in delete command: " + e.getMessage());
        }


    }

    @Override
    public String description() {
        return "remove all selected cities";
    }
}
