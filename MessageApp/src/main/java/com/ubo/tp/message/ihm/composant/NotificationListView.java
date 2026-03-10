package com.ubo.tp.message.ihm.composant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.MessageController;

public class NotificationListView extends JPanel {

	private static final long serialVersionUID = 1L;
	private final List<NotificationView> notifications = new ArrayList<>();
	private final JPanel stackPanel;
	private final JLabel counterLabel;
	private final MessageController controller;

	public NotificationListView(MessageController controller) {
		this.controller = controller;
		this.setOpaque(false); 
		this.setLayout(new BorderLayout());

		stackPanel = new JPanel();
		stackPanel.setOpaque(false);
		stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));

		counterLabel = new JLabel();
		counterLabel.setOpaque(true);
		counterLabel.setBackground(new Color(200, 50, 50));
		counterLabel.setForeground(Color.WHITE);
		counterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		counterLabel.setVisible(false);
		this.add(stackPanel, BorderLayout.CENTER); 
		this.add(counterLabel, BorderLayout.SOUTH); 
	}

	public void addNotification(String text, Message msg, Object senderSource, Runnable onRemove) {
	    final NotificationView[] itemWrapper = new NotificationView[1];
	    itemWrapper[0] = new NotificationView(text, msg, () -> {
	        if (senderSource instanceof User) {
	            controller.setRecipient((User) senderSource);
	        } else if (senderSource instanceof Channel) {
	            controller.setRecipient((Channel) senderSource);
	        }
	        onRemove.run(); 
	        removeNotification(itemWrapper[0]); 
	    }, () -> {
	        onRemove.run(); 
	        removeNotification(itemWrapper[0]); 
	    });
	    
	    notifications.add(itemWrapper[0]);
	    refreshUI();
	}

	private void removeNotification(NotificationView item) {
		if (item == null && !notifications.isEmpty()) {
			notifications.remove(0);
		} else {
			notifications.remove(item);
		}
		refreshUI();
	}

	private void refreshUI() {
		stackPanel.removeAll();
		stackPanel.add(Box.createVerticalGlue());
		int size = notifications.size();
		if (size > 3) {
			int nombreNotiSup = size - 3;
			counterLabel.setText("Vous avez " + nombreNotiSup  + " autres notifications");
			counterLabel.setVisible(true);
			for (int i = size - 3; i < size; i++) {
				stackPanel.add(notifications.get(i));
				stackPanel.add(Box.createVerticalStrut(5)); 
			}
		} else {
			counterLabel.setVisible(false);
			for (NotificationView item : notifications) {
				stackPanel.add(item);
				stackPanel.add(Box.createVerticalStrut(5));
			}
		}

		stackPanel.revalidate();
		stackPanel.repaint();
	}
}