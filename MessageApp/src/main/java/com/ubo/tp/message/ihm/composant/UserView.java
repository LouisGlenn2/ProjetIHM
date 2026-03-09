package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Vue compacte pour afficher les informations d'un utilisateur sous forme de carte.
 */
public class UserView extends JPanel {
    private static final long serialVersionUID = 1L;

    public UserView(User user) {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.setPreferredSize(new Dimension(200, 100));

        JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(nameLabel, BorderLayout.NORTH);

        JLabel tagLabel = new JLabel("@" + user.getUserTag(), SwingConstants.CENTER);
        tagLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        tagLabel.setForeground(Color.DARK_GRAY);
        this.add(tagLabel, BorderLayout.CENTER);

        JLabel statusLabel = new JLabel(user.isOnline() ? "En ligne" : "Hors ligne", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(user.isOnline() ? Color.GREEN.darker() : Color.RED.darker());
        this.add(statusLabel, BorderLayout.SOUTH);
    }

    public void addSelectionListener(java.awt.event.MouseAdapter adapter) {
        this.addMouseListener(adapter);
        for (Component c : getComponents()) {
            c.addMouseListener(adapter);
        }
    }
}