package com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.ListUserView;

public class UserListController implements IDatabaseObserver {
    private final IDatabase database;
    private final ListUserView view;
    private MessageController messageController;
    private Channel currentChannel; 

    public UserListController(IDatabase database) {
        this.database = database;
        this.view = new ListUserView(new ArrayList<>(database.getUsers()));
        this.database.addObserver(this);
    }

    public void updateViewForChannel(Channel channel) {
        this.currentChannel = channel;
        this.refreshWithFilter();
    }

    private void refreshWithFilter() {
        List<User> membersToShow;

        if (currentChannel == null ||currentChannel.getUsers() == null || currentChannel.getUsers().isEmpty()) {
            membersToShow = new ArrayList<>(database.getUsers());
        } else {
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
 

    public void setMessageController(MessageController messageController) {
    	this.messageController = messageController;
    	this.view.setOnUserSelected(user -> {
         if (this.messageController != null) {
             this.messageController.setRecipient(user);
         }
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
        if (currentChannel != null && currentChannel.getUuid().equals(c.getUuid())) {
            this.currentChannel = c;
            refreshWithFilter();
        }
    }
    
    @Override public void notifyMessageAdded(Message m) {}
    @Override public void notifyMessageDeleted(Message m) {}
    @Override public void notifyMessageModified(Message m) {}
    @Override public void notifyChannelAdded(Channel c) {}
    @Override public void notifyChannelDeleted(Channel c) {}
}