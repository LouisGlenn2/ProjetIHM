package com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.ubo.tp.message.core.DataManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.composant.MessageListView;
import com.ubo.tp.message.ihm.composant.NotificationListView;

public class MessageController implements IDatabaseObserver {
    private final MessageListView view;
    private final IDatabase database;
    private final DataManager dataManager;
    private final ISession session;
    private IMessageRecipient currentRecipient; 
    private NotificationListView notificationListView;

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



    public void sendMessage(String text) {
        if (text != null && !text.trim().isEmpty() && currentRecipient != null) {
            User me = session.getConnectedUser();
            UUID recipientUUID = currentRecipient.getUuid();
            Message newMessage = new Message(me, recipientUUID, text);
            this.dataManager.sendMessage(newMessage);
            checkMentions(text);
        }
    }

    private void checkMentions(String text) {
        for (User user : database.getUsers()) {
            String mentionTag = "@" + user.getUserTag();
            if (text.contains(mentionTag)) {
                System.out.println("Utilisateur mentionné : " + user.getName());
            }
        }
    }
    public void loadMissedNotifications() {
        User me = session.getConnectedUser();
        if (me == null || notificationListView == null) return;
        for (Message m : database.getMessages()) {
            boolean isDirectMessage = m.getRecipient().equals(me.getUuid());
            boolean isMentioned = m.getText().contains("@" + me.getUserTag());
            if (!m.getSender().equals(me) && (isDirectMessage || isMentioned)) {
                String text = isMentioned ? "Mentionné par " + m.getSender().getName() : "Message privé";
                Object source = m.getSender(); 
                if (isMentioned && !isDirectMessage) {
                    source = database.getChannels().stream()
                            .filter(c -> c.getUuid().equals(m.getRecipient()))
                            .findFirst()
                            .map(c -> (Object) c)
                            .orElse(m.getSender());
                }
                notificationListView.addNotification(text, m, source);
            }
        }
    }

    public void setFilter(String query) { this.view.setFilter(query); }



    public List<Message> getFilteredMessages(String searchString) {
        User me = session.getConnectedUser();
        if (me == null || currentRecipient == null) {
            return new ArrayList<>();
        }
        UUID currentUuid = currentRecipient.getUuid();
        return database.getMessages().stream()
            .filter(msg -> {
                boolean matchesRecipient = false;

                if (currentRecipient instanceof Channel) {
                    matchesRecipient = msg.getRecipient().equals(currentUuid);
                } 
                else if (currentRecipient instanceof User) {
                    boolean sentByMe = msg.getSender().equals(me) && msg.getRecipient().equals(currentUuid);
                    boolean sentByOther = msg.getSender().getUuid().equals(currentUuid) && msg.getRecipient().equals(me.getUuid());
                    matchesRecipient = sentByMe || sentByOther;
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
    public void setNotificationManager(NotificationListView nm) {
        this.notificationListView = nm;
    }
    
    @Override 
    public void notifyMessageAdded(Message m) {
        this.view.refresh();
        
        User me = session.getConnectedUser();
        if (me == null || m.getSender().equals(me)) return;

        boolean isDirectMessage = m.getRecipient().equals(me.getUuid());
        boolean isMentioned = m.getText().contains("@" + me.getUserTag());

        if (notificationListView != null && (isDirectMessage || isMentioned)) {
            String text;
            Object source; 

            if (isMentioned && !isDirectMessage) {
                text = "Vous avez été mentionné   dans un canal";
                source = database.getChannels().stream()
                                 .filter(c -> c.getUuid().equals(m.getRecipient()))
                                 .findFirst()
                                 .map(c -> (Object) c) 
                                 .orElse(m.getSender()); 
            } else {
                text = "Vous avez reçu un message de " + m.getSender().getName();
                source = m.getSender();
            }

            notificationListView.addNotification(text, m, source);
        }
    }

    @Override public void notifyMessageDeleted(Message m) { this.view.refresh(); }
    @Override public void notifyMessageModified(Message m) { this.view.refresh(); }
    @Override public void notifyUserAdded(User u) { this.view.refresh(); }
    @Override public void notifyUserDeleted(User u) { this.view.refresh(); }
    @Override public void notifyUserModified(User u) { this.view.refresh(); }
    @Override public void notifyChannelAdded(Channel c) { this.view.refresh(); }
    @Override public void notifyChannelDeleted(Channel c) { this.view.refresh(); }
    @Override public void notifyChannelModified(Channel c) { this.view.refresh(); }
}