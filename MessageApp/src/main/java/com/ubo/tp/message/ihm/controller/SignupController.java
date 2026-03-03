package main.java.com.ubo.tp.message.ihm.controller;

import java.util.UUID;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.SignupView;

public class SignupController {
    private final SignupView signupView;
    private final IDatabase database;
    private final DataManager dataManager;
    private final Runnable onNavigateToLogin;

    public SignupController(IDatabase database, DataManager dataManager, Runnable onNavigateToLogin) {
        this.database = database;
        this.dataManager = dataManager;
        this.onNavigateToLogin = onNavigateToLogin;
        this.signupView = new SignupView(this);
    }

    public void handleSignup(String tag, String password, String name) {
        // Validation simple
        if (tag.isEmpty() || password.isEmpty() || name.isEmpty()) {
            signupView.setError("Tous les champs sont requis");
            return;
        }

        // Vérification des doublons (Lecture seule via IDatabase)
        for (User u : database.getUsers()) {
            if (u.getUserTag().equalsIgnoreCase(tag)) {
                signupView.setError("Ce tag @"+tag+" est déjà utilisé");
                return;
            }
        }

        // Création de l'entité
        User newUser = new User(UUID.randomUUID(), tag, password, name);
        
        // ECRITURE via le DataManager
        this.dataManager.sendUser(newUser);
        
        // Retour au login
        this.onNavigateToLogin.run();
    }

    public void cancel() {
        this.onNavigateToLogin.run();
    }

    public SignupView getSignupView() {
        return signupView;
    }
}