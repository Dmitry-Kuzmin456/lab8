package org.client.gui;

import org.client.ClientApp;
import org.client.network.Connector;
import org.common.request.RequestGenerator;
import org.common.request.UserData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistrationWindow extends JDialog {
    private Connector connector;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegistrationWindow(Connector connector) {
        super((Frame) null, "Регистрация", true);
        this.connector = connector;
        setTitle("Registration");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        setSize(screenSize.width / 2, screenSize.height / 2);

        // Фоновая картинка
        Image backgroundImage = new ImageIcon("img/reg_background.jpg").getImage();
        BackgroundRegPanel panel = new BackgroundRegPanel(backgroundImage);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 150, 60, 150));
        panel.setOpaque(false);

        // Поля
        usernameField = createStyledField();
        passwordField = createStyledPasswordField();

        // Кнопки
        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Register");

        loginButton.addActionListener(this::loginAction);
        registerButton.addActionListener(this::loginAction);

        // Добавление компонентов
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(300, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(230, 230, 230, 200));
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(300, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(230, 230, 230, 200));
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 100, 100, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }

    private void loginAction(ActionEvent e) {
        try {
            char type = e.getActionCommand().toLowerCase().charAt(0);
            byte[] request = RequestGenerator.generateUserDataRequest(
                    type,
                    usernameField.getText(),
                    new String(passwordField.getPassword())
            );
            connector.sendInt(request.length);
            connector.send(request);
            String status = connector.receiveString();
            if (status.equals("OK")) {
                JOptionPane.showMessageDialog(null, "Регистрация произошла успешно");
                ClientApp.setUserData(new UserData(usernameField.getText(), new String(passwordField.getPassword())));
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, status);
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }
    }
}
