package com.ubo.tp.message.ihm.controller;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.UserEditView;

public class UserEditController {
    private final UserEditView view;
    private final DataManager dataManager;
    private final ISession session;
    private final Runnable onFinish;

    public UserEditController(DataManager dataManager, ISession session, Runnable onFinish) {
        this.dataManager = dataManager;
        this.session = session;
        this.onFinish = onFinish;
        
        String currentName = (session.getConnectedUser() != null) ? session.getConnectedUser().getName() : "";
        this.view = new UserEditView(this, currentName);
    }

    public void updateUser(String newName) {
        User current = session.getConnectedUser();
        if (current != null && !newName.trim().isEmpty()) {
            // Création d'une copie de l'utilisateur avec le nouveau nom
            User updatedUser = new User(current.getUuid(), current.getUserTag(), current.getUserPassword(), newName);
            // On demande au DataManager de propager la modification (écriture fichier)
            this.dataManager.sendUser(updatedUser);
            // On met à jour la session locale pour que l'IHM reflète le changement immédiatement
            this.session.connect(updatedUser); 
            this.onFinish.run();
        }
    }

    public void cancel() {
        this.onFinish.run();
    }

    public UserEditView getView() {
        return view;
    }
}