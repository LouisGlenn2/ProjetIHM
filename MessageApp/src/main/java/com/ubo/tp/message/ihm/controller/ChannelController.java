package main.java.com.ubo.tp.message.ihm.controller;

import java.util.Set;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.ChannelListView;

public class ChannelController implements IDatabaseObserver {
    private final IDatabase database;
    private final ChannelListView view;
    private MessageController messageListController;

    public ChannelController(IDatabase database) {
        this.database = database;
        this.view = new ChannelListView(this);
        this.database.addObserver(this);
    }

    public Set<Channel> getChannels() {
        return database.getChannels();
    }
    public void setMessageListController(MessageController mlc) {
        this.messageListController = mlc;
    }

    public void selectChannel(Channel channel) {
        if (messageListController != null) {
            messageListController.setFilter("#" + channel.getName());
        }
    }

    

    public ChannelListView getView() {
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