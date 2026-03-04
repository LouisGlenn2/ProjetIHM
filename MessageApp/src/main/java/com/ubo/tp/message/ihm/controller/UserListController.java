package main.java.com.ubo.tp.message.ihm.controller;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.composant.ListUserView;

/**
 * Contrôleur pour gérer la liste des utilisateurs.
 */
public class UserListController implements IDatabaseObserver {
    private final IDatabase database;
    private final ListUserView view; 
    public UserListController(IDatabase database) {
        this.database = database;
        this.view = new ListUserView(new java.util.ArrayList<>(database.getUsers()));
        
        this.database.addObserver(this);
    }

    @Override
    public void notifyUserAdded(User user) {
        SwingUtilities.invokeLater(() -> {
            view.setUsers(new java.util.ArrayList<>(database.getUsers()));
            view.refresh();
        });
    }

    public ListUserView getView() {
        return view;
    }

    @Override public void notifyMessageAdded(Message m) {}
    @Override public void notifyChannelAdded(main.java.com.ubo.tp.message.datamodel.Channel c) {}

	@Override
	public void notifyMessageDeleted(Message deletedMessage) {
		view.refresh();		
	}

	@Override
	public void notifyMessageModified(Message modifiedMessage) {
		view.refresh();		
		
	}

	@Override
	public void notifyUserDeleted(User deletedUser) {
		view.refresh();		
		
	}

	@Override
	public void notifyUserModified(User modifiedUser) {
		view.refresh();		
		
	}

	@Override
	public void notifyChannelDeleted(Channel deletedChannel) {
		view.refresh();		
		
	}

	@Override
	public void notifyChannelModified(Channel modifiedChannel) {
		view.refresh();		
		
	}
}