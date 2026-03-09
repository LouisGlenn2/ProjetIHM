package com.ubo.tp.message.ihm.controller;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.MessageListView;

public class MessageController implements IDatabaseObserver {
    private final MessageListView view;
    private final IDatabase database;
    private final DataManager dataManager;
    private final ISession session;
    private IMessageRecipient currentRecipient; // Canal ou Utilisateur cible

    public MessageController(IDatabase database, DataManager dataManager, ISession session) {
        this.database = database;
        this.dataManager = dataManager;
        this.session = session;
        this.view = new MessageListView(this);
        this.database.addObserver(this);
    }

    // Définit la cible (clic sur canal ou utilisateur)
    public void setRecipient(IMessageRecipient recipient) {
        this.currentRecipient = recipient;
        if (recipient instanceof Channel) {
            this.setFilter("#" + ((Channel) recipient).getName());
        } else if (recipient instanceof User) {
            this.setFilter("@" + ((User) recipient).getUserTag());
        }
    }


    public void sendMessage(String text) {
        User author = session.getConnectedUser();
      
        if (author != null && currentRecipient != null && !text.trim().isEmpty()) {
           UUID recipientUuid = currentRecipient.getUuid();
            Message msg = new Message(author, recipientUuid, text);
            this.dataManager.sendMessage(msg);
        }
    }

    public void setFilter(String query) { this.view.setFilter(query); }

    public Set<Message> getFilteredMessages(String searchString) {
        Set<Message> allMessages = database.getMessages();
        if (searchString == null || searchString.trim().isEmpty()) return allMessages;

        String filter = searchString.trim().toLowerCase();
        return allMessages.stream().filter(msg -> {
            if (filter.startsWith("@")) {
                return msg.getSender().getUserTag().toLowerCase().equals(filter.substring(1));
            } else if (filter.startsWith("#")) {
                return msg.getText().toLowerCase().contains(filter);
            }
            return msg.getText().toLowerCase().contains(filter) || 
                   msg.getSender().getName().toLowerCase().contains(filter);
        }).collect(Collectors.toSet());
    }

    public MessageListView getView() { return view; }
    public boolean isOwnMessage(Message m) { return session.getConnectedUser() != null && m.getSender().equals(session.getConnectedUser()); }

    @Override public void notifyMessageAdded(Message m) { this.view.refresh(); }
    @Override public void notifyMessageDeleted(Message m) { this.view.refresh(); }
    @Override public void notifyMessageModified(Message m) { this.view.refresh(); }
    @Override public void notifyUserAdded(User u) { this.view.refresh(); }
    @Override public void notifyUserDeleted(User u) { this.view.refresh(); }
    @Override public void notifyUserModified(User u) { this.view.refresh(); }
    @Override public void notifyChannelAdded(Channel c) { this.view.refresh(); }
    @Override public void notifyChannelDeleted(Channel c) { this.view.refresh(); }
    @Override public void notifyChannelModified(Channel c) { this.view.refresh(); }
}