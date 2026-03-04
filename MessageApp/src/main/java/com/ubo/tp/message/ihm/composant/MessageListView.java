package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final MessageController controller;
    private final JPanel listContainer;
    private String currentFilter = "";

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        this.add(scrollPane, BorderLayout.CENTER);

        refresh(); 
    }

    public void setFilter(String query) {
        this.currentFilter = query;
        this.refresh();
    }

    public void refresh() {
        listContainer.removeAll();
        
        for (Message m : controller.getFilteredMessages(currentFilter)) {
            MessageView mview = new MessageView(m, controller.isOwnMessage(m), false);
            mview.setAlignmentX(Component.LEFT_ALIGNMENT);
            listContainer.add(mview);
            listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

	
}