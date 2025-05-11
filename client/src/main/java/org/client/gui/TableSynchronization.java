package org.client.gui;

import model.City;
import org.client.util.CommandManager;
import org.client.network.Connector;
import org.common.request.UserData;

import java.util.ArrayList;

public class TableSynchronization implements Runnable {
    private CityTableModel tableModel;
    private Connector connector;
    private UserData userData;
    private static ArrayList<City> cities;
    private CityCanvas cityCanvas;

    public TableSynchronization(CityTableModel tableModel, Connector connector, UserData userData, CityCanvas cityCanvas) {
        this.tableModel = tableModel;
        this.connector = connector;
        this.userData = userData;
        this.cityCanvas = cityCanvas;
    }

    @Override
    public void run() {
        CommandManager commandManager = CommandManager.getInstance();
        while (true) {
            try {
                Thread.sleep(1000);
                commandManager.executeCommand("show", connector, userData, null);
                tableModel.updateCities(cities);
                cityCanvas.updateCities(cities); // <-- добавлено обновление канваса
            } catch (Exception e) {
                System.out.println("error in update " + e.getMessage());
            }
        }
    }

    public static void setCities(ArrayList<City> cities) {
        TableSynchronization.cities = cities;
    }
}
