package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

public class ConsoleLogger implements IDatabaseObserver {

	@Override
	public void notifyMessageAdded(Message addedMessage) {
		System.out.println("[DB Message] Ajout : " + addedMessage.getText());
	}

	@Override
	public void notifyUserAdded(User addedUser) {
		System.out.println("[DB User] Nouvel utilisateur : " + addedUser.getName() + " (@" + addedUser.getUserTag() + ")");
	}

	@Override
	public void notifyChannelAdded(Channel addedChannel) {
		System.out.println("[DB Channel] Nouveau canal : " + addedChannel.getName());
	}

	@Override 
	public void notifyMessageDeleted(Message deletedMessage) {}
	@Override 
	public void notifyMessageModified(Message modifiedMessage) {}
	@Override 
	public void notifyUserDeleted(User deletedUser) {}
	@Override 
	public void notifyUserModified(User modifiedUser) {}
	@Override 
	public void notifyChannelDeleted(Channel deletedChannel) {}
	@Override 
	public void notifyChannelModified(Channel modifiedChannel) {}
}