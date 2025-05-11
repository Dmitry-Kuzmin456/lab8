package org.client.gui;

import model.City;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CityTableModel extends AbstractTableModel {
    private static Set<Long> selectedCityIds = new HashSet<>();
    private static List<City> cities;
    private final String[] columnNames = {
            "Выбраны", "ID", "Name", "x", "y", "Area", "Population",
            "Meters Above Sea Level", "Climate", "Government", "Standard of Living", "Governor age"
    };
    private static CityTableModel instance;

    public static CityTableModel getInstance() {
        return instance;
    }

    public CityTableModel(List<City> cities) {
        this.cities = cities != null ? cities : new ArrayList<>();
        instance = this;
    }



    @Override
    public int getRowCount() {
        return cities.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch (columnIndex) {
            case 0: return Boolean.class;
            case 1, 5, 11: return Long.class;
            case 2, 8, 9, 10: return String.class;
            case 3: return Double.class;
            case 4, 7: return Float.class;
            case 6: return Integer.class;
            default: return null;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        City city = cities.get(rowIndex);
        switch (columnIndex) {
            case 0: return city.isUserStatus() ? selectedCityIds.contains(city.getId()) : null;
            case 1: return city.getId();
            case 2: return city.getName();
            case 3: return city.getCoordinates().getX();
            case 4: return city.getCoordinates().getY();
            case 5: return city.getArea();
            case 6: return city.getPopulation();
            case 7: return city.getMetersAboveSeaLevel();
            case 8: return city.getClimate();
            case 9: return city.getGovernment();
            case 10: return city.getStandardOfLiving();
            case 11: return city.getGovernor().getAge();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            City city = cities.get(rowIndex);
            if (city.isUserStatus()) {
                boolean selected = (Boolean) aValue;
                if (selected) {
                    selectedCityIds.add(city.getId());
                } else {
                    selectedCityIds.remove(city.getId());
                }
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }


    public void updateCities(List<City> newCities) {

        Set<Long> newIds = newCities.stream()
                .map(City::getId)
                .collect(Collectors.toSet());

        selectedCityIds.retainAll(newIds);
        Set<City> set1 = new HashSet<>(cities);
        Set<City> set2 = new HashSet<>(newCities);
        try{
            if (!set1.equals(set2)) {
                this.cities.clear();
                this.cities.addAll(newCities);
                this.cities = this.cities.stream().sorted((city1, city2) -> Long.compare(city1.getId(), city2.getId())).collect(Collectors.toList());
                fireTableDataChanged();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 && cities.get(rowIndex).isUserStatus();
    }

    public static Set<Long> getSelectedCityIds() {
        return selectedCityIds;
    }

    public static List<City> getCities() {
        return new ArrayList<>(cities);
    }
}
