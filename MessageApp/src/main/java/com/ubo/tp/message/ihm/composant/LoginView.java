package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.controller.LoginController;

public class LoginView extends JPanel {
    private static final long serialVersionUID = 1L;
    protected JTextField mTagField;
    protected JPasswordField mPasswordField; // JPasswordField est plus sécurisé
    private final JLabel errorLabel;
    private final JButton btnLogin;
    private final JButton btnGoSignup; // On le déclare ici pour y accéder partout
    private final LoginController controller;

    public LoginView(LoginController controller) {
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- TITRE ---
        JLabel titleLabel = new JLabel("MessageApp - Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Prend 2 colonnes
        add(titleLabel, gbc);

        // --- CHAMP UTILISATEUR ---
        gbc.gridwidth = 1; // On repasse à 1 colonne
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Nom d'utilisateur :"), gbc);

        mTagField = new JTextField(15);
        gbc.gridx = 1;
        add(mTagField, gbc);

        // --- CHAMP MOT DE PASSE ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Mot de passe :"), gbc);

        mPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(mPasswordField, gbc);

        // --- BOUTON SE CONNECTER (A GAUCHE) ---
        btnLogin = new JButton("Se connecter");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1; // IMPORTANT : Seulement 1 colonne ici
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnLogin, gbc);

        // --- BOUTON S'INSCRIRE (A DROITE) ---
        btnGoSignup = new JButton("S'inscrire");
        gbc.gridx = 1; // Colonne d'à côté
        add(btnGoSignup, gbc);

        // --- LABEL ERREUR ---
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(errorLabel, gbc);

        // Initialisation des actions
        manageAction();
    }

    public void manageAction() {
        // Action Connexion
        btnLogin.addActionListener(e -> {
            User user = new User(UUID.randomUUID(), mTagField.getText(), new String(mPasswordField.getPassword()), null);
            controller.handleLogin(user);
        });

        // Action Navigation vers Inscription
        btnGoSignup.addActionListener(e -> {
            controller.goToSignup();
        });
    }

    // Getters et Setters utiles
    public void setError(String error) {
        errorLabel.setText(error);
        this.revalidate(); // Force la mise à jour visuelle
    }
}