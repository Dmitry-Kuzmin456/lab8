package org.client.commands;

import model.City;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.RequestGenerator;
import org.common.request.UserData;

import javax.swing.*;

public class ClearCommand implements Command {
    @Override
    public void execute(Connector connector, UserData userData, City city) {
        try{
            byte[] request = RequestGenerator.generateWithCity("clear", city, userData);
            connector.sendInt(request.length);
            connector.send(request);
            String answer = new String(connector.receive());
            if (!answer.equals("successfully")) {JOptionPane.showMessageDialog(null, answer);}

        } catch (Exception e){
            System.out.println(e.getMessage());
        }


    }


    @Override
    public String description() {
        return "clears all your objects";
    }
}
