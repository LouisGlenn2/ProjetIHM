package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;

public class MainContentView extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainContentView(ChannelListView channelList, MessageListView messageList, ListUserView usersList) {
        this.setLayout(new BorderLayout());

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, messageList, usersList);
        rightSplit.setDividerLocation(600); 
        rightSplit.setDividerSize(3);
        rightSplit.setBorder(null);
        rightSplit.setResizeWeight(1.0); 
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, channelList, rightSplit);
        mainSplit.setDividerLocation(200); 
        mainSplit.setDividerSize(3);
        mainSplit.setBorder(null);

        this.add(mainSplit, BorderLayout.CENTER);
    }
}