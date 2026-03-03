package main.java.com.ubo.tp.message.ihm.controller;



import java.util.Set;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.LoginView;

public class LoginController {

    private final LoginView loginView;
    private final IDatabase database;
    private final Session session;
	private Runnable onNavigateToSignup;

    public LoginController(IDatabase database, Session session, Runnable onNavigateToSignup) {
        this.database = database;
        this.session = session;
        this.onNavigateToSignup = onNavigateToSignup;
        this.loginView = new LoginView(this);
    }

  
    public void handleLogin(User connectedUser) {
        User userDb = searchUser(connectedUser);

        if (userDb == null) {
            loginView.setError("Identifiant ou mot de passe incorrect");
            return;
        }
        session.connect(userDb);
    }

    private User searchUser(User user) {
        Set<User> usersDb = database.getUsers();

        for (User u : usersDb) {
            if (u.getUserTag().equals(user.getUserTag()) && u.getUserPassword().equals(user.getUserPassword())) {
                return u;
            }
        }
        return null;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public Session getSession() {
        return session;
    }


 // Dans LoginController.java

    public void goToSignup() {
        // Ce Runnable 'onNavigateToSignup' a été passé par MessageApp lors de l'init
        if (this.onNavigateToSignup != null) {
            this.onNavigateToSignup.run();
        }
    }
}