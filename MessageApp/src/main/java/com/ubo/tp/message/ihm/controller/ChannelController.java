package com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.ChannelListView;

public class ChannelController implements IDatabaseObserver {
    private final IDatabase database;
    private final DataManager dataManager;
    private final ChannelListView view;
    private final ISession session;
    
    private MessageController messageListController;
    private UserListController userListController; // REFERENCE AJOUTÉE

    public ChannelController(IDatabase database, DataManager data, ISession session) {
        this.database = database;
        this.dataManager = data;
        this.session = session;
        this.view = new ChannelListView(this);
        this.database.addObserver(this);
    }

    public void setUserListController(UserListController ulc) {
        this.userListController = ulc;
    }

    public void setMessageListController(MessageController mlc) {
        this.messageListController = mlc;
    }

    public void selectChannel(Channel channel) {
        // Filtrer les messages
        if (messageListController != null) {
            messageListController.setFilter("#" + channel.getName());
        }
        // Filtrer les utilisateurs à droite
        if (userListController != null) {
            userListController.updateViewForChannel(channel);
        }
    }

    public void createChannel(String name, List<User> selectedUsers) {
        User connectedUser = session.getConnectedUser();
        if (connectedUser != null && name != null && !name.trim().isEmpty()) {
            Channel newChannel = (selectedUsers == null || selectedUsers.isEmpty()) 
                ? new Channel(connectedUser, name)
                : new Channel(connectedUser, name, selectedUsers);
            dataManager.sendChannel(newChannel);
        }
    }

    public void openEditChannelDialog(Channel channel) {
        DefaultListModel<User> listModel = new DefaultListModel<>();
        List<User> allUsers = new ArrayList<>(database.getUsers());
        for (User u : allUsers) listModel.addElement(u);

        JList<User> userJList = new JList<>(listModel);
        userJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        List<User> currentMembers = channel.getUsers();
        int[] indices = currentMembers.stream()
                .mapToInt(allUsers::indexOf)
                .filter(i -> i != -1).toArray();
        userJList.setSelectedIndices(indices);

        int result = JOptionPane.showConfirmDialog(null, new JScrollPane(userJList), 
                "Gérer les membres de " + channel.getName(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Channel updatedChannel = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), userJList.getSelectedValuesList());
            this.dataManager.sendChannel(updatedChannel);
        }
    }

    public Set<Channel> getChannels() { return database.getChannels(); }
    public User getConnectedUser() { return session.getConnectedUser(); }
    public ChannelListView getView() { return view; }

    // Notifications (simplifié : rafraîchir la vue des canaux)
    @Override public void notifyChannelAdded(Channel c) { view.refresh(); }
    @Override public void notifyChannelDeleted(Channel c) { view.refresh(); }
    @Override public void notifyChannelModified(Channel c) { view.refresh(); }
    @Override public void notifyUserAdded(User u) { view.refresh(); }
    @Override public void notifyUserDeleted(User u) { view.refresh(); }
    @Override public void notifyUserModified(User u) { view.refresh(); }
    @Override public void notifyMessageAdded(Message m) { view.refresh(); }
    @Override public void notifyMessageDeleted(Message m) { view.refresh(); }
    @Override public void notifyMessageModified(Message m) { view.refresh(); }
}