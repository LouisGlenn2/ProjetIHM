package main.java.com.ubo.tp.message.ihm.controller;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.ListUserView;

/**
 * Contrôleur pour gérer la liste des utilisateurs.
 */
public class UserListController {
    private final IDatabase database;
    private final JScrollPane scrollableListUserView;

    public UserListController(IDatabase database) {
        this.database = database;

        // Récupération des utilisateurs depuis la base de données
        List<User> users = database.getUsers().stream().collect(Collectors.toList());

        // Création de la vue avec la liste des utilisateurs
        ListUserView listUserView = new ListUserView(users);

        // Ajout de l'ascenseur
        this.scrollableListUserView = new JScrollPane(listUserView);
        this.scrollableListUserView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.scrollableListUserView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.scrollableListUserView.setPreferredSize(new Dimension(250, 0)); 
    }


    /**
     * Retourne la vue associée à ce contrôleur.
     */
     public JScrollPane getListUserView() {
        return scrollableListUserView;
    }
}