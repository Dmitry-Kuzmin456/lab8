package org.client.gui;

import model.*;
import org.client.util.CommandManager;
import org.client.network.Connector;
import org.common.request.UserData;
import serialize.SerializeCity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainWindow extends JFrame {
    private UserData userData;
    Connector connector;

        public MainWindow(UserData userData, Connector connector) throws Exception {
                this.userData = userData;
                this.connector = connector;

                setTitle("City Manager");
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                setSize(screenSize.width / 2, screenSize.height / 2);
                setLocationRelativeTo(null);
                setDefaultCloseOperation(EXIT_ON_CLOSE);

                CommandManager.getInstance().executeCommand("show", connector, userData, null);

                CityTableModel tableModel = new CityTableModel(new ArrayList<>());
                JTable table = new JTable(tableModel);
                styleTable(table);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);

                // Визуализация городов
                CityCanvas cityCanvas = new CityCanvas(city -> {
                        JOptionPane.showMessageDialog(this,
                                "Город: " + city.getName() + "\nID пользователя: " + city.getUserId(),
                                "Информация о городе",
                                JOptionPane.INFORMATION_MESSAGE);
                });
                cityCanvas.setOpaque(false);

                // Синхронизация
                new Thread(new TableSynchronization(tableModel, connector, userData, cityCanvas)).start();

                // Панель с переключением: таблица / визуализация
                CardLayout cardLayout = new CardLayout();
                JPanel centerPanel = new JPanel(cardLayout);
                centerPanel.add(scrollPane, "table");
                centerPanel.add(cityCanvas, "visual");

                // Панель фильтров + кнопка "Переключить"
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.setOpaque(false);

                TableRowSorter<CityTableModel> sorter = new TableRowSorter<>(tableModel);
                table.setRowSorter(sorter);

                JPanel filterPanel = new JPanel(new GridLayout(1, tableModel.getColumnCount()));
                filterPanel.setOpaque(false);
                JTextField[] filterFields = new JTextField[tableModel.getColumnCount()];
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        JTextField filterField = new JTextField();
                        filterField.setBackground(new Color(230, 230, 230, 180));
                        filterField.setForeground(new Color(20, 20, 20));
                        filterField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        filterField.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
                        filterFields[i] = filterField;
                        final int colIndex = i;
                        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                                public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
                                public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
                                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }

                                private void updateFilter() {
                                        java.util.List<RowFilter<Object, Object>> filters = new ArrayList<>();
                                        for (int j = 0; j < filterFields.length; j++) {
                                                String text = filterFields[j].getText();
                                                if (!text.trim().isEmpty()) {
                                                        filters.add(RowFilter.regexFilter("(?i)" + text, j));
                                                }
                                        }
                                        sorter.setRowFilter(RowFilter.andFilter(filters));
                                }
                        });
                        filterPanel.add(filterField);
                }

                JButton toggleButton = createStyledButton("Переключить");
                toggleButton.addActionListener(e -> cardLayout.next(centerPanel));

                topPanel.add(filterPanel, BorderLayout.CENTER);
                topPanel.add(toggleButton, BorderLayout.EAST);

                // Кнопки управления
                JButton insertButton = createStyledButton("Вставить");
                insertButton.addActionListener(e -> openInsertDialog());

                JButton removeButton = createStyledButton("Удалить");
                removeButton.addActionListener(e -> CommandManager.getInstance().executeCommand("remove", connector, userData, null));

                JButton updateButton = createStyledButton("Обновить");
                updateButton.addActionListener(e -> openUpdateDialog());

                JPanel buttonPanel = new JPanel();
                buttonPanel.setOpaque(false);
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
                buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                buttonPanel.add(insertButton);
                buttonPanel.add(Box.createVerticalStrut(10));
                buttonPanel.add(removeButton);
                buttonPanel.add(Box.createVerticalStrut(10));
                buttonPanel.add(updateButton);

                // Общий фон
                Image backgroundImage = new ImageIcon("img/main_background.jpg").getImage();
                JPanel backgroundPanel = new BackgroundRegPanel(backgroundImage);
                backgroundPanel.setLayout(new BorderLayout());
                backgroundPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                backgroundPanel.add(topPanel, BorderLayout.NORTH);
                backgroundPanel.add(centerPanel, BorderLayout.CENTER);
                backgroundPanel.add(buttonPanel, BorderLayout.EAST);

                add(backgroundPanel);
                setVisible(true);
        }


        private void openInsertDialog() {
                City city = getCityInput();
                if (city != null) {
                        CommandManager.getInstance().executeCommand("insert", connector, userData, city);
                }

        }

        private void openUpdateDialog() {
                City city = getCityInput();
                if (city != null) {
                        CommandManager.getInstance().executeCommand("update", connector, userData, city);
                }
        }


        private void showError(String message, JTextField fieldToClear) {
                JOptionPane.showMessageDialog(null, message, "Неверный ввод", JOptionPane.WARNING_MESSAGE);
                if (fieldToClear != null) {
                        fieldToClear.setText("");
                }
        }

        private void styleTable(JTable table) {
                table.setFillsViewportHeight(true);
                table.setRowHeight(28);
                table.setShowGrid(false);
                table.setIntercellSpacing(new Dimension(0, 0));
                table.setOpaque(false);

                // Цвет текста темно-серый
                table.setForeground(new Color(30, 30, 30));  // или Color.DARK_GRAY
                table.setBackground(new Color(0, 0, 0, 0));
                table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                // Заголовок таблицы
                JTableHeader header = table.getTableHeader();
                header.setFont(new Font("Segoe UI", Font.BOLD, 16));
                header.setBackground(new Color(70, 70, 70, 180));
                header.setForeground(Color.WHITE);
                header.setOpaque(false);
                ((JLabel) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);


                DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
                leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

                table.setDefaultRenderer(Object.class, leftRenderer);
                table.setDefaultRenderer(String.class, leftRenderer);
                table.setDefaultRenderer(Integer.class, leftRenderer);
                table.setDefaultRenderer(Long.class, leftRenderer);
                table.setDefaultRenderer(Float.class, leftRenderer);
                table.setDefaultRenderer(Double.class, leftRenderer);

        }

        private JButton createStyledButton(String text) {
                JButton button = new JButton(text);
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setBackground(new Color(80, 80, 80, 180));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setFont(new Font("Segoe UI", Font.BOLD, 14));
                button.setMaximumSize(new Dimension(120, 40));
                button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1, true));
                return button;
        }

        private City getCityInput(){
                JDialog dialog = new JDialog(this, "Input City", true);
                dialog.setSize(400, 400);
                dialog.setLayout(new GridLayout(13, 2));

                JTextField idField = new JTextField();
                JTextField nameField = new JTextField();
                JTextField xField = new JTextField();
                JTextField yField = new JTextField();
                JTextField areaField = new JTextField();
                JTextField populationField = new JTextField();
                JTextField metersField = new JTextField();
                JComboBox<Climate> climateBox = new JComboBox<>(Climate.values());
                JComboBox<Government> governmentBox = new JComboBox<>(Government.values());
                JComboBox<StandardOfLiving> standardBox = new JComboBox<>(StandardOfLiving.values());
                JTextField humanField = new JTextField();

                dialog.add(new JLabel("ID*"));
                dialog.add(idField);
                dialog.add(new JLabel("Name*"));
                dialog.add(nameField);
                dialog.add(new JLabel("X coordinate*"));
                dialog.add(xField);
                dialog.add(new JLabel("Y coordinate*"));
                dialog.add(yField);
                dialog.add(new JLabel("Area*"));
                dialog.add(areaField);
                dialog.add(new JLabel("Population*"));
                dialog.add(populationField);
                dialog.add(new JLabel("Meters Above Sea Level"));
                dialog.add(metersField);
                dialog.add(new JLabel("Climate*"));
                dialog.add(climateBox);
                dialog.add(new JLabel("Government"));
                dialog.add(governmentBox);
                dialog.add(new JLabel("Standard Of Living*"));
                dialog.add(standardBox);
                dialog.add(new JLabel("Governor Age"));
                dialog.add(humanField);

                JButton submitButton = new JButton("Ввод");
                dialog.getContentPane().setBackground(new Color(40, 40, 40, 220));

                Color backgroundColor = new Color(60, 60, 60, 200);
                Color textColor = Color.WHITE;
                Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

                JTextField[] fields = {
                        idField, nameField, xField, yField,
                        areaField, populationField, metersField, humanField
                };

                for (JTextField field : fields) {
                        field.setBackground(backgroundColor);
                        field.setForeground(textColor);
                        field.setCaretColor(textColor);
                        field.setFont(fieldFont);
                        field.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
                }

                JComboBox<?>[] boxes = {climateBox, governmentBox, standardBox};
                for (JComboBox<?> box : boxes) {
                        box.setBackground(backgroundColor);
                        box.setForeground(textColor);
                        box.setFont(fieldFont);
                        ((JLabel) box.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
                }

                Component[] components = dialog.getContentPane().getComponents();
                for (Component comp : components) {
                        if (comp instanceof JLabel label) {
                                label.setForeground(new Color(200, 200, 200));
                                label.setFont(fieldFont);
                        }
                }


                submitButton.setBackground(new Color(90, 90, 90));
                submitButton.setForeground(Color.WHITE);
                submitButton.setFocusPainted(false);
                submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                submitButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

                AtomicReference<City> resultCity = new AtomicReference<>(null);

                submitButton.addActionListener(e -> {
                        try {
                                long id;
                                try {
                                        id = Long.parseLong(idField.getText().trim());
                                        if (id <= 0) {
                                                showError("ID должно быть больше 0.", idField);
                                                return;
                                        }
                                } catch (NumberFormatException ex) {
                                        showError("ID должно быть числом (long).", idField);
                                        return;
                                }


                                String name = nameField.getText().trim();
                                if (name.isEmpty()) {
                                        showError("Name не может быть пустым.", nameField);
                                        return;
                                }


                                double x;
                                try {
                                        x = Double.parseDouble(xField.getText().trim());
                                } catch (NumberFormatException ex) {
                                        showError("X должно быть числом (double).", xField);
                                        return;
                                }


                                float y;
                                try {
                                        y = Float.parseFloat(yField.getText().trim());
                                } catch (NumberFormatException ex) {
                                        showError("Y должно быть числом (float).", yField);
                                        return;
                                }


                                long area;
                                try {
                                        area = Long.parseLong(areaField.getText().trim());
                                        if (area <= 0) {
                                                showError("Area должно быть больше 0.", areaField);
                                                return;
                                        }
                                } catch (NumberFormatException ex) {
                                        showError("Area должно быть числом (long).", areaField);
                                        return;
                                }


                                int population;
                                try {
                                        population = Integer.parseInt(populationField.getText().trim());
                                        if (population <= 0) {
                                                showError("Population должно быть больше 0.", populationField);
                                                return;
                                        }
                                } catch (NumberFormatException ex) {
                                        showError("Population должно быть числом (int).", populationField);
                                        return;
                                }


                                float meters = 0;
                                String metersText = metersField.getText().trim();
                                if (!metersText.isEmpty()) {
                                        try {
                                                meters = Float.parseFloat(metersText);
                                        } catch (NumberFormatException ex) {
                                                showError("Meters Above Sea Level должно быть числом (float).", metersField);
                                                return;
                                        }
                                }

                                Climate climate = (Climate) climateBox.getSelectedItem();
                                if (climate == null) {
                                        showError("Не выбран Climate.", null);
                                        return;
                                }

                                Government government = (Government) governmentBox.getSelectedItem();

                                StandardOfLiving standard = (StandardOfLiving) standardBox.getSelectedItem();
                                if (standard == null) {
                                        showError("Не выбран Standard of Living.", null);
                                        return;
                                }




                                Human governor = null;
                                String ageText = humanField.getText().trim();
                                if (ageText.isEmpty()) {
                                        showError("Не выбран Governor", null);
                                        return;
                                } else {
                                        try {
                                                long age = Long.parseLong(ageText);
                                                if (age <= 0) {
                                                        showError("Возраст губернатора должен быть больше 0.", humanField);
                                                        return;
                                                }
                                                governor = new Human(age);
                                        } catch (NumberFormatException ex) {
                                                showError("Возраст должен быть числом (long).", humanField);
                                                return;
                                        }
                                }

                                City newCity = new City.CityBuilder()
                                        .setId(id)
                                        .setName(name)
                                        .setCoordinates(new Coordinates(x, y))
                                        .setArea(area)
                                        .setPopulation(population)
                                        .setMetersAboveSeaLevel(meters)
                                        .setClimate(climate)
                                        .setGovernment(government)
                                        .setStandardOfLiving(standard)
                                        .setGovernor(governor)
                                        .build();
                                resultCity.set(newCity);
                                dialog.dispose();

                        } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, "Ошибка при создании города:\n" + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                });

                dialog.add(new JLabel());
                dialog.add(submitButton);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);

                return resultCity.get();
        }






}
