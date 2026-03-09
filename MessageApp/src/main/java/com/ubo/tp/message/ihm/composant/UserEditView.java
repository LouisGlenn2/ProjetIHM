package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import com.ubo.tp.message.ihm.controller.UserEditController;

public class UserEditView extends JPanel {
    private static final long serialVersionUID = 1L;
    protected JTextField mNameField;
    private final UserEditController controller;

    public UserEditView(UserEditController controller, String currentName) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titleLabel = new JLabel("Modifier mon profil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Champ Pseudo
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Nouveau pseudo :"), gbc);

        mNameField = new JTextField(currentName, 15);
        gbc.gridx = 1;
        add(mNameField, gbc);

        // Bouton Valider
        JButton btnValidate = new JButton("Valider");
        gbc.gridy = 2; gbc.gridx = 0;
        btnValidate.addActionListener(e -> controller.updateUser(mNameField.getText()));
        add(btnValidate, gbc);

        // Bouton Annuler
        JButton btnCancel = new JButton("Annuler");
        gbc.gridx = 1;
        btnCancel.addActionListener(e -> controller.cancel());
        add(btnCancel, gbc);
    }
}