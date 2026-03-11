package com.ubo.tp.message.ihm.controller;



import java.util.Set;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.session.Session;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.LoginView;

public class LoginController {

    private final LoginView loginView;
    private final IDatabase database;
    private final Session session;
	private Runnable onNavigateToSignup;
    private final DataManager mDataManager;

    public LoginController(IDatabase database, Session session, Runnable onNavigateToSignup, DataManager mDataManager) {
        this.database = database;
        this.session = session;
        this.onNavigateToSignup = onNavigateToSignup;
        this.loginView = new LoginView(this);
        this.mDataManager = mDataManager;
    }

  
    public void handleLogin(User userFromFields) {
        User userInDb = searchUser(userFromFields); 

        if (userInDb == null) {
            loginView.setError("Identifiant ou mot de passe incorrect");
            return;
        }
        userInDb.setOnline(true); 
        mDataManager.sendUser(userInDb);
        session.connect(userInDb);

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

    public void goToSignup() {
        if (this.onNavigateToSignup != null) {
            this.onNavigateToSignup.run();
        }
    }
}