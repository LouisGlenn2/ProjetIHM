package com.ubo.tp.message.ihm.controller;

import java.util.List;
import java.util.Set;
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
    private IMessageRecipient currentRecipient; 

    public MessageController(IDatabase database, DataManager dataManager, ISession session) {
        this.database = database;
        this.dataManager = dataManager;
        this.session = session;
        this.view = new MessageListView(this);
        this.database.addObserver(this);
    }


    public void setRecipient(IMessageRecipient recipient) {
        this.currentRecipient = recipient;
        System.out.println("DESTINATAIRE CHANGÉ : " + recipient.getUuid().toString());
        this.view.refresh();
    }


 // Dans MessageController.java

    public void sendMessage(String text) {
        if (text != null && !text.trim().isEmpty() && currentRecipient != null) {
            User me = session.getConnectedUser();
            java.util.UUID recipientUUID = currentRecipient.getUuid();
            System.out.println("id associé message"+recipientUUID);
            Message newMessage = new Message(me, recipientUUID, text);
            this.dataManager.sendMessage(newMessage);
        }
    }

    public void setFilter(String query) { this.view.setFilter(query); }



    public List<Message> getFilteredMessages(String searchString) { 
        Set<Message> allMessages = database.getMessages();
        
        return allMessages.stream()
            .filter(msg -> {
                boolean matchesRecipient = false;
                if (currentRecipient != null) {
                    String msgDest = msg.getRecipient().toString();
                    String currentDest = currentRecipient.getUuid().toString();
                    matchesRecipient = msgDest.equals(currentDest);
                }
                
                boolean matchesSearch = true;
                if (searchString != null && !searchString.isEmpty()) {
                    String filter = searchString.toLowerCase();
                    matchesSearch = msg.getText().toLowerCase().contains(filter) || 
                                    msg.getSender().getName().toLowerCase().contains(filter);
                }
                return matchesRecipient && matchesSearch;
            })
            .sorted((m1, m2) -> Long.compare(m1.getEmissionDate(), m2.getEmissionDate()))
            .collect(Collectors.toList());
    }
    
    public void deleteMessage(Message message) {
        this.dataManager.deleteMessage(message, session.getConnectedUser());
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