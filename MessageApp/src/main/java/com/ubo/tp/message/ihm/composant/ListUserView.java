package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Vue pour afficher une liste de UserView.
 */
public class ListUserView extends JPanel {
    private static final long serialVersionUID = 1L;

    public ListUserView(List<User> users) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ajout de chaque utilisateur sous forme de UserView
        for (User user : users) {
            UserView userView = new UserView(user);
            
            userView.setMaximumSize(new Dimension(300, 100)); // Largeur fixe de 300px
            userView.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer horizontalement

            this.add(Box.createVerticalStrut(10));
            this.add(userView);
        }
    }
}