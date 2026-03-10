package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import com.ubo.tp.message.ihm.controller.UserEditController;

public class UserEditView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final UserEditController controller;
    protected JTextField mNameField;
    
    private final Color primaryColor = new Color(0, 132, 255); 
    private final Color textColor = new Color(44, 62, 80);
    private final Color backgroundColor = Color.WHITE;

    public UserEditView(UserEditController controller, String currentName) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        this.setBackground(backgroundColor);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel titleLabel = new JLabel("Modifier mon profil", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Nouveau pseudo");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(nameLabel, gbc);

        mNameField = new JTextField(currentName);
        mNameField.setPreferredSize(new Dimension(300, 40));
        mNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        gbc.gridy = 2;
        add(mNameField, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        gbc.gridy = 3;
        gbc.insets = new Insets(25, 0, 0, 0);

        JButton btnValidate = createStyledButton("Enregistrer", true);
        btnValidate.addActionListener(e -> controller.updateUser(mNameField.getText()));

        JButton btnCancel = createStyledButton("Annuler", false);
        btnCancel.addActionListener(e -> controller.cancel());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnValidate);
        add(buttonPanel, gbc);
    }

    /**
     * Crée un bouton stylisé avec des bords arrondis et des effets de couleur.
     */
    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isPrimary) {
                    g2.setColor(primaryColor);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(new Color(245, 245, 245));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(new Color(100, 100, 100));
                }
                
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
}