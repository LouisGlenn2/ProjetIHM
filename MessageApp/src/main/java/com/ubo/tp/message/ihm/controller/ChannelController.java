package com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private SearchController searchController;
    
    private final Set<UUID> unreadChannels = new HashSet<>();
    private UUID currentSelectedChannelUuid = null;

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
    public void setSearchController(SearchController sc) {
    	this.searchController = sc; 
    }

    public boolean isUnread(UUID channelUuid) {
        return unreadChannels.contains(channelUuid);
    }

    public void selectChannel(Channel channel) {
        this.currentSelectedChannelUuid = channel.getUuid();
        this.unreadChannels.remove(channel.getUuid());
        this.view.refresh();
        
        if (userListController != null) userListController.updateViewForChannel(channel);
        if (messageListController != null) messageListController.setRecipient(channel);
    }

    public void selectUser(User user) {
        this.currentSelectedChannelUuid = null;
        
        if (messageListController != null) messageListController.setRecipient(user);
    }

    public void createChannel(String name, List<User> selectedUsers) {
        User me = session.getConnectedUser();
        if (me != null && name != null && !name.trim().isEmpty()) {
            Channel newChannel = new Channel(UUID.randomUUID(), me, name, selectedUsers);
            dataManager.sendChannel(newChannel);
        }
    }

    public void openEditChannelDialog(Channel channel) {
        List<User> allUsers = new ArrayList<>(database.getUsers());
        List<User> selected = view.showUserSelectionDialog("Gérer les membres : " + channel.getName(), allUsers, channel.getUsers());
        
        if (selected != null) {
            Channel updated = new Channel(channel.getUuid(), channel.getCreator(), channel.getName(), selected);
            dataManager.sendChannel(updated);
        }
    }

    public void deleteChannel(Channel channel, User user){
        if(channel.getCreator().equals(user)) {
            dataManager.deleteChannel(channel);
        }else {
            System.out.println("Erreur, le channel ne vous appartiens pas");
        }
    }

    public List<User> getAllAvailableUsers() {
    	return new ArrayList<>(database.getUsers());
    }
    public Set<Channel> getChannels() { 
    	return database.getChannels(); 
    }
    public User getConnectedUser() {
    	return session.getConnectedUser(); 
    }
    public SearchController getSearchController() { 
    	return searchController;
    }
    public ChannelListView getView() {
    	return view; 
    }

    @Override public void notifyChannelAdded(Channel c) { view.refresh(); }
    @Override public void notifyChannelDeleted(Channel c) { view.refresh(); }
    @Override public void notifyChannelModified(Channel c) { view.refresh(); }
    @Override public void notifyUserAdded(User u) { view.refresh(); }
    @Override public void notifyUserDeleted(User u) { view.refresh(); }
    @Override public void notifyUserModified(User u) { view.refresh(); }
    
    @Override 
    public void notifyMessageAdded(Message m) { 
        if (m.getRecipient() != null && !m.getRecipient().equals(currentSelectedChannelUuid)) {
            boolean isChannel = database.getChannels().stream().anyMatch(c -> c.getUuid().equals(m.getRecipient()));
            if (isChannel) {
                unreadChannels.add(m.getRecipient());
            }
        }
        view.refresh(); 
    }
    public void leaveChannel(Channel channel) {
        User me = session.getConnectedUser();
        if (me != null && channel != null) {
            // Suppression de l'utilisateur dans l'objet métier
            channel.removeUser(me);
            
            // Notification au DataManager pour mettre à jour le fichier physique (.chn)
            dataManager.sendChannel(channel);
            
            // Rafraîchissement de la vue
            this.view.refresh();
        }
    }
    
    @Override public void notifyMessageDeleted(Message m) { view.refresh(); }
    @Override public void notifyMessageModified(Message m) { view.refresh(); }
}