package main.java.com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.ListUserView;

public class UserListController implements IDatabaseObserver {
    private final IDatabase database;
    private final ListUserView view;
    private Channel currentChannel; // Canal sélectionné

    public UserListController(IDatabase database) {
        this.database = database;
        // Initialement, on affiche tout le monde ou personne selon ton choix
        this.view = new ListUserView(new ArrayList<>(database.getUsers()));
        this.database.addObserver(this);
    }

    /**
     * Appelé par le ChannelController lors d'un clic sur un canal
     */
    public void updateViewForChannel(Channel channel) {
        this.currentChannel = channel;
        this.refreshWithFilter();
    }

    private void refreshWithFilter() {
        List<User> membersToShow;

        if (currentChannel == null) {
            // Si rien n'est sélectionné, on affiche tous les utilisateurs de la DB
            membersToShow = new ArrayList<>(database.getUsers());
        } else {
            // On combine le créateur et les invités du canal
            Set<User> members = new HashSet<>(currentChannel.getUsers());
            members.add(currentChannel.getCreator());
            membersToShow = new ArrayList<>(members);
        }

        final List<User> finalUsers = membersToShow;
        SwingUtilities.invokeLater(() -> {
            view.setUsers(finalUsers);
            view.refresh();
        });
    }

    public ListUserView getView() {
        return view;
    }

    @Override
    public void notifyUserAdded(User user) { refreshWithFilter(); }
    @Override
    public void notifyUserDeleted(User user) { refreshWithFilter(); }
    @Override
    public void notifyUserModified(User user) { refreshWithFilter(); }
    @Override
    public void notifyChannelModified(Channel c) { 
        // Si le canal actuel est celui modifié, on rafraîchit (pour les membres)
        if (currentChannel != null && currentChannel.getUuid().equals(c.getUuid())) {
            this.currentChannel = c;
            refreshWithFilter();
        }
    }
    
    // Autres méthodes de l'interface
    @Override public void notifyMessageAdded(Message m) {}
    @Override public void notifyMessageDeleted(Message m) {}
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyChannelAdded(Channel c) {}
    @Override public void notifyChannelDeleted(Channel c) {}
}