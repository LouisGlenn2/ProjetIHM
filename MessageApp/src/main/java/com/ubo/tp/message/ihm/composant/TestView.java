package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Vue du contenu principal après connexion
 */
public class TestView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private User currentUser;
    private Runnable onLogout;

    public TestView(User user, Runnable onLogout) {
        this.currentUser = user;
        this.onLogout = onLogout;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));
        
        JLabel welcomeLabel = new JLabel("Bienvenue, " + currentUser.getUserTag() + " !");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 72));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(iconLabel, gbc);

        JLabel infoLabel = new JLabel("<html><center>" +
            "Vous êtes connecté<br>" +
            "Utilisateur : <b>" + currentUser.getUserTag() + "</b>" +
            "</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        centerPanel.add(infoLabel, gbc);

        JLabel messageLabel = new JLabel("<html><center>" +
            "Cette zone contiendra le contenu principal<br>" +
            "de votre application de messagerie" +
            "</center></html>");
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 20, 20);
        centerPanel.add(messageLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }
}