package main.java.com.ubo.tp.message.ihm.controller;

import java.util.Set;
import java.util.stream.Collectors;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.MessageListView;

public class MessageController implements IDatabaseObserver{

    private final MessageListView view;
    private final IDatabase database;
    private final Session session;

    public MessageController(IDatabase database, Session session) {
        this.database = database;
        this.session = session;
        this.view = new MessageListView(this);
        this.database.addObserver(this);
    }

    /**
     * Appelé par le ChannelController pour filtrer la liste
     */
    public void setFilter(String query) {
        this.view.setFilter(query);
    }

    /**
     * Logique de filtrage inchangée
     */
    public Set<Message> getFilteredMessages(String searchString) {
        Set<Message> allMessages = database.getMessages();

        if (searchString == null || searchString.trim().isEmpty()) {
            return allMessages;
        }

        String filter = searchString.trim().toLowerCase();

        return allMessages.stream().filter(msg -> {
            if (filter.startsWith("@")) {
                return msg.getSender().getUserTag().toLowerCase().equals(filter.substring(1));
            } else if (filter.startsWith("#")) {
                return msg.getText().toLowerCase().contains(filter);
            } else {
                return msg.getText().toLowerCase().contains(filter) || 
                       msg.getSender().getUserTag().toLowerCase().contains(filter);
            }
        }).collect(Collectors.toSet());
    }

    public boolean isOwnMessage(Message m) {
        return session.getConnectedUser() != null && 
               m.getSender().equals(session.getConnectedUser());
    }

    public MessageListView getView() {
        return view;
    }

	@Override
	public void notifyMessageAdded(Message addedMessage) {
		this.view.refresh();		
	}

	@Override
	public void notifyMessageDeleted(Message deletedMessage) {
		this.view.refresh();		
	}

	@Override
	public void notifyMessageModified(Message modifiedMessage) {
		this.view.refresh();		
	}

	@Override
	public void notifyUserAdded(User addedUser) {
		this.view.refresh();		
	}

	@Override
	public void notifyUserDeleted(User deletedUser) {
		this.view.refresh();		
	}

	@Override
	public void notifyUserModified(User modifiedUser) {
		this.view.refresh();		
	}

	@Override
	public void notifyChannelAdded(Channel addedChannel) {
		this.view.refresh();		
	}

	@Override
	public void notifyChannelDeleted(Channel deletedChannel) {
		this.view.refresh();		
	}

	@Override
	public void notifyChannelModified(Channel modifiedChannel) {
		this.view.refresh();		
	}
}