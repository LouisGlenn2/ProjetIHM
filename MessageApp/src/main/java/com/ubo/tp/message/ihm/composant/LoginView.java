package com.ubo.tp.message.ihm.composant;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.LoginController;

public class LoginView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    protected JTextField mTagField;
    protected JPasswordField mPasswordField; 
    private final JLabel errorLabel;
    private final JButton btnLogin;
    private final JButton btnGoSignup; 
    private final LoginController controller;
    private final Color primaryColor = new Color(52, 152, 219); 
    private final Color backgroundColor = Color.WHITE;
    private final Color textColor = new Color(44, 62, 80);

    public LoginView(LoginController controller) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        this.setBackground(backgroundColor);
        this.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Bienvenue", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0); 
        add(titleLabel, gbc);

        gbc.gridwidth = 2; 
        gbc.insets = new Insets(5, 0, 5, 0);

        addLabel(gbc, "Tag d'utilisateur", 1);
        mTagField = createStyledTextField();
        gbc.gridy = 2;
        add(mTagField, gbc);

        addLabel(gbc, "Mot de passe", 3);
        mPasswordField = createStyledPasswordField();
        gbc.gridy = 4;
        add(mPasswordField, gbc);

        btnLogin = new JButton("Se connecter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(primaryColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        styleButton(btnLogin, true);
        gbc.gridy = 5;
        gbc.insets = new Insets(25, 0, 5, 0);
        add(btnLogin, gbc);

        btnGoSignup = new JButton("Pas encore de compte ? S'inscrire");
        styleButton(btnGoSignup, false);
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(btnGoSignup, gbc);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridy = 7;
        add(errorLabel, gbc);

        manageAction();
    }

    private void addLabel(GridBagConstraints gbc, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(127, 140, 141));
        gbc.gridy = y;
        add(lbl, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        applyCommonStyle(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        applyCommonStyle(field);
        return field;
    }

    private void applyCommonStyle(JTextField field) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton btn, boolean isPrimary) {
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (!isPrimary) {
            btn.setForeground(primaryColor);
        }
    }

    public void manageAction() {
        btnLogin.addActionListener(e -> {
            User user = new User(UUID.randomUUID(), mTagField.getText(), new String(mPasswordField.getPassword()), null);
            controller.handleLogin(user);
        });

        btnGoSignup.addActionListener(e -> controller.goToSignup());
    }

    public void setError(String error) {
        errorLabel.setText(error);
        this.revalidate();
    }
}