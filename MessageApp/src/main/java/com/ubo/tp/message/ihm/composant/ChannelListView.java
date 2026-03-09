package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.ChannelController;

@SuppressWarnings("unused")
public class ChannelListView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ChannelController controller;
    private final JPanel listPanel;

 // Dans ChannelListView.java
    public ChannelListView(ChannelController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(200, 0));

        // --- EN-TÊTE AVEC BOUTON + ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));
        
        JLabel title = new JLabel(" CANAUX");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnAdd = new JButton("+");
        btnAdd.setToolTipText("Créer un canal");
        btnAdd.addActionListener(e -> openCreationDialog());
        headerPanel.add(btnAdd, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- LISTE ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        this.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        refresh();
    }

    private void openCreationDialog() {
        String name = JOptionPane.showInputDialog(this, "Nom du canal :");
        if (name != null && !name.isEmpty()) {
            controller.createChannel(name, null);
        }
    }

    public void refresh() {
        listPanel.removeAll();
        User me = controller.getConnectedUser();
        
        for (Channel c : controller.getChannels()) {
            boolean isVisible = c.getUsers().isEmpty() || 
                               c.getCreator().equals(me) || 
                               c.getUsers().contains(me);
            
            if (isVisible) {
                listPanel.add(new ChannelView(c, controller));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}