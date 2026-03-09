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
    private UserListController userListController; 

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
        this.userListController.updateViewForChannel(channel);
        this.messageListController.setRecipient(channel);
        
        System.out.println("Canal sélectionné : " + channel.getName() + " UUID: " + channel.getUuid());
    }

    public void createChannel(String name, List<User> selectedUsers) {
        User connectedUser = session.getConnectedUser();
        if (connectedUser != null && name != null && !name.trim().isEmpty()) {
            List<User> users = (selectedUsers != null) ? selectedUsers : new ArrayList<>();
            Channel newChannel = new Channel(
                java.util.UUID.randomUUID(), 
                connectedUser,           
                name,                      
                users                      
            );
            
            dataManager.sendChannel(newChannel);
        }
    }

    public List<User> getAllAvailableUsers() {
        return new ArrayList<>(database.getUsers());
    }
    public void openCreateChannelWithMembers() {
        List<User> allUsers = new ArrayList<>(database.getUsers());
        List<User> selected = view.showUserSelectionDialog("Membres du canal", allUsers, null);
        
        if (selected != null) {
            String name = JOptionPane.showInputDialog(view, "Nom du canal :");
            if (name != null && !name.isEmpty()) {
                createChannel(name, selected);
            }
        }
    }
    
    public void openEditChannelDialog(Channel channel) {
        List<User> allAppUsers = new ArrayList<>(this.database.getUsers());
        List<User> selectedUsers = view.showUserSelectionDialog(
            "Gérer les membres de " + channel.getName(), 
            allAppUsers, 
            channel.getUsers() 
        );
        if (selectedUsers != null) {
            Channel updatedChannel = new Channel(
                channel.getUuid(), 
                channel.getCreator(), 
                channel.getName(), 
                selectedUsers
            );
            this.dataManager.sendChannel(updatedChannel);
        }
    }
    public List<User> showUserSelectionDialog(String title, List<User> allUsers, List<User> alreadySelected) {
        DefaultListModel<User> listModel = new DefaultListModel<>();
        for (User u : allUsers) listModel.addElement(u);

        JList<User> userJList = new JList<>(listModel);
        userJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (alreadySelected != null) {
            int[] indices = alreadySelected.stream()
                    .mapToInt(allUsers::indexOf)
                    .filter(i -> i != -1).toArray();
            userJList.setSelectedIndices(indices);
        }
        int result = JOptionPane.showConfirmDialog(null, new JScrollPane(userJList), title, JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION) ? userJList.getSelectedValuesList() : null;
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