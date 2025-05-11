package org.client.gui;

import model.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class CityCanvas extends JPanel {
    private List<City> cities = new ArrayList<>();
    private final Consumer<City> onCityClick;

    // Карта цветов для разных пользователей
    private final Map<Integer, Color> userColors = new HashMap<>();

    public CityCanvas(Consumer<City> onCityClick) {
        this.onCityClick = onCityClick;
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(800, 600));

        // Обработчик кликов мыши
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                City clicked = getCityAt(e.getPoint());
                if (clicked != null) {
                    onCityClick.accept(clicked);
                }
            }
        });
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (City city : cities) {
            int x = (int) Math.round(city.getCoordinates().getX());
            int y = (int) Math.round(city.getCoordinates().getY());

            // Получаем цвет по userId
            Color color = getColorForUser(city.getUserId());
            g2.setColor(color);

            // Рисуем город как круг
            int size = 20;
            g2.fillOval(x, y, size, size);

            // Обводка для своих городов
            if (city.isUserStatus()) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, size, size);
            }
        }
    }

    // Получить город, на который кликнули
    private City getCityAt(Point point) {
        for (City city : cities) {
            int x = (int) Math.round(city.getCoordinates().getX());
            int y = (int) Math.round(city.getCoordinates().getY());
            Rectangle bounds = new Rectangle(x, y, 20, 20);
            if (bounds.contains(point)) {
                return city;
            }
        }
        return null;
    }

    // Генерация или получение цвета для userId
    private Color getColorForUser(int userId) {
        if (!userColors.containsKey(userId)) {
            userColors.put(userId, generateRandomColor());
        }
        return userColors.get(userId);
    }

    private Color generateRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
    }

    public void updateCities(java.util.List<City> cities) {
        this.cities = cities;
        repaint();
    }
}