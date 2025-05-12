package org.client.commands;


import model.City;
import org.client.interfaces.Command;
import org.client.network.Connector;
import org.common.request.RequestGenerator;
import org.common.request.UserData;

import javax.swing.*;
import java.io.IOException;

/**
 * Команда, добавляющая элемент в коллекцию по ключу
 */
public class InsertCommand implements Command {

    @Override
    public void execute(Connector connector, UserData userData, City city) {
        try{
            System.out.println("Insert command");
            byte[] request = RequestGenerator.generateWithCity("insert", city, userData);
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
        return "insert element by id";
    }

}
