package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelView extends JPanel {
    private static final long serialVersionUID = 1L;

    public ChannelView(Channel channel, ChannelController controller) {
        this.setLayout(new BorderLayout());
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        boolean isPrivate = channel.getUsers() != null && !channel.getUsers().isEmpty();
        String prefix = isPrivate ? "🔒 " : "# ";

        JButton btnName = new JButton(prefix + channel.getName());
        btnName.setContentAreaFilled(false);
        btnName.setBorderPainted(false);
        btnName.setHorizontalAlignment(SwingConstants.LEFT);
        btnName.addActionListener(e -> controller.selectChannel(channel));
        this.add(btnName, BorderLayout.CENTER);

        if (channel.getCreator().equals(controller.getConnectedUser())) {
            
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            actionsPanel.setOpaque(false);

            JButton btnEdit = new JButton("✎");
            btnEdit.setToolTipText("Gérer les membres du canal");
            btnEdit.addActionListener(e -> controller.openEditChannelDialog(channel));
            actionsPanel.add(btnEdit);

            JButton btnDelete = new JButton("🗑");
            btnDelete.setForeground(Color.RED);
            btnDelete.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(this, "Supprimer le canal " + channel.getName() + " ?");
                if (choice == JOptionPane.YES_OPTION) {
                    // controller.deleteChannel(channel);
                }
            });
            actionsPanel.add(btnDelete);

            this.add(actionsPanel, BorderLayout.EAST);
        }
    }
}