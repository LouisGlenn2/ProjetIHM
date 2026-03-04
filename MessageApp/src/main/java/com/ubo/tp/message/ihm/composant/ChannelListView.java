package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelListView extends JPanel {
    private final ChannelController controller;
    private final JPanel listPanel;

    public ChannelListView(ChannelController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(200, 0)); // Largeur de la sidebar

        // Titre de la section
        JLabel title = new JLabel(" CANAUX", SwingConstants.LEFT);
        title.setOpaque(true);
        title.setBackground(new Color(230, 230, 230));
        title.setFont(new Font("Arial", Font.BOLD, 12));
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.add(title, BorderLayout.NORTH);

        // Liste des canaux
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        listPanel.removeAll();
        for (Channel c : controller.getChannels()) {
            listPanel.add(new ChannelView(c, controller));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}