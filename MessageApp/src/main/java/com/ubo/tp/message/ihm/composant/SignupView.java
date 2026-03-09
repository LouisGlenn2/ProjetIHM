package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import com.ubo.tp.message.ihm.controller.SignupController;

public class SignupView extends JPanel {
    private final SignupController controller;
    protected JTextField mTagField, mNameField, mPasswordField;
    private final JLabel errorLabel;

    public SignupView(SignupController controller) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(new JLabel("Inscription"), gbc);

        // Champs
        gbc.gridwidth = 1; gbc.gridy = 1; add(new JLabel("Tag (@) :"), gbc);
        mTagField = new JTextField(15); gbc.gridx = 1; add(mTagField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Nom :"), gbc);
        mNameField = new JTextField(15); gbc.gridx = 1; add(mNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Pass :"), gbc);
        mPasswordField = new JPasswordField(15); gbc.gridx = 1; add(mPasswordField, gbc);

        // Bouton S'inscrire
        JButton btnSignup = new JButton("Créer mon compte");
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        add(btnSignup, gbc);

        // Erreur
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gbc.gridy = 5; add(errorLabel, gbc);

        btnSignup.addActionListener(e -> 
            controller.handleSignup(mTagField.getText(), mPasswordField.getText(), mNameField.getText())
        );
    }

    public void setError(String msg) { errorLabel.setText(msg); }
}