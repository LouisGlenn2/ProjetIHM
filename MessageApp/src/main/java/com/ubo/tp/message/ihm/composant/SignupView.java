package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import com.ubo.tp.message.ihm.controller.SignupController;

public class SignupView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SignupController controller;
    protected JTextField mTagField, mNameField;
    protected JPasswordField mPasswordField;
    private final JLabel errorLabel;

    private final Color primaryColor = new Color(0, 132, 255); 
    private final Color backgroundColor = Color.WHITE;
    private final Color textColor = new Color(44, 62, 80);
    private final Color labelColor = new Color(127, 140, 141);

    public SignupView(SignupController controller) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        this.setBackground(backgroundColor);
        this.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("Créer un compte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(titleLabel, gbc);

        gbc.insets = new Insets(5, 0, 5, 0);

        addLabel(gbc, "Tag utilisateur (@)", 1);
        mTagField = createStyledField(false);
        gbc.gridy = 2;
        add(mTagField, gbc);

        addLabel(gbc, "Nom complet", 3);
        mNameField = createStyledField(false);
        gbc.gridy = 4;
        add(mNameField, gbc);

        addLabel(gbc, "Mot de passe", 5);
        mPasswordField = (JPasswordField) createStyledField(true);
        gbc.gridy = 6;
        add(mPasswordField, gbc);

        JButton btnSignup = new JButton("S'inscrire") {
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
        styleButton(btnSignup, true);
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 0, 5, 0);
        add(btnSignup, gbc);

        JButton btnBack = new JButton("Annuler et se connecter");
        styleButton(btnBack, false);
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(btnBack, gbc);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridy = 9;
        add(errorLabel, gbc);

        btnSignup.addActionListener(e -> 
            controller.handleSignup(mTagField.getText(), new String(mPasswordField.getPassword()), mNameField.getText())
        );

        // Action du bouton retour
        btnBack.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) {
                win.setVisible(false); 
            }
        });
    }

    private void addLabel(GridBagConstraints gbc, String text, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(labelColor);
        gbc.gridy = y;
        add(lbl, gbc);
    }

    private JTextField createStyledField(boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField() : new JTextField();
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void styleButton(JButton btn, boolean isPrimary) {
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (!isPrimary) {
            btn.setForeground(new Color(52, 152, 219)); // Bleu pour le lien de retour
        }
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
        this.revalidate();
    }
}