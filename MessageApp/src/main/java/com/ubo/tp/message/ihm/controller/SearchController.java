package com.ubo.tp.message.ihm.controller;

import java.util.ArrayList;
import java.util.List;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.User;

public class SearchController {
    protected IDatabase database;
    protected ISession session;
    protected ChannelController channelController;
    protected UserListController userListController;

    public SearchController(IDatabase database, ISession session, ChannelController channelController,UserListController userListController) {
        this.database = database;
        this.session = session;
        this.channelController = channelController;
        this.userListController=userListController;
    }

    public List<IMessageRecipient> getSearchResults(String query) {
        List<IMessageRecipient> results = new ArrayList<>();
        if (query == null || query.isEmpty()) return results;

        String cleanQuery = query.toLowerCase().trim();
        User me = session.getConnectedUser();

        if (cleanQuery.startsWith("@")) {
            String search = cleanQuery.substring(1);
            for (User u : database.getUsers()) {
                if (!u.equals(me) && (u.getUserTag().toLowerCase().contains(search) 
                    || u.getName().toLowerCase().contains(search))) {
                    results.add(u);
                }
            }
        } 
        else {
            for (Channel c : database.getChannels()) {
                boolean isVisible = c.getUsers().isEmpty() || c.getCreator().equals(me) || c.getUsers().contains(me);
                if (isVisible && c.getName().toLowerCase().contains(cleanQuery)) {
                    results.add(c);
                }
            }
        }
        return results;
    }

    public void openRecipient(IMessageRecipient recipient) {
        if (recipient instanceof Channel) {
            channelController.selectChannel((Channel) recipient);
        } else if (recipient instanceof User) {
            channelController.selectUser((User) recipient);
        }
    }
}