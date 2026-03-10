package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final Color idleColor = new Color(248, 249, 250);
    private final Color hoverColor = new Color(232, 240, 254);
    private final Color activeTextColor = new Color(52, 73, 94);

    public ChannelView(Channel channel, ChannelController controller) {
        this.setLayout(new BorderLayout(10, 0));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        this.setPreferredSize(new Dimension(200, 45));
        this.setBackground(idleColor);
        this.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean isPrivate = channel.getUsers() != null && !channel.getUsers().isEmpty();
        
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(127, 140, 141));
                
                if (isPrivate) {
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(4, 12, 10, 8, 2, 2); 
                    g2.drawArc(6, 7, 6, 10, 0, 180);     
                } else {
                    g2.setFont(new Font("Monospaced", Font.BOLD, 18));
                    g2.drawString("#", 2, 22);
                }
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(20, 30));
        this.add(iconPanel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(activeTextColor);
        this.add(nameLabel, BorderLayout.CENTER);

        if (channel.getCreator().equals(controller.getConnectedUser())) {
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionsPanel.setOpaque(false);

            JLabel btnEdit = new JLabel("✎");
            btnEdit.setForeground(new Color(189, 195, 199));
            btnEdit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { controller.openEditChannelDialog(channel); }
                @Override
                public void mouseEntered(MouseEvent e) { btnEdit.setForeground(Color.BLUE); }
                @Override
                public void mouseExited(MouseEvent e) { btnEdit.setForeground(new Color(189, 195, 199)); }
            });

            JLabel btnDelete = new JLabel("✕");
            btnDelete.setForeground(new Color(189, 195, 199));
            btnDelete.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int choice = JOptionPane.showConfirmDialog(null, "Supprimer " + channel.getName() + " ?");
                    if (choice == JOptionPane.YES_OPTION) { /* controller.deleteChannel(channel); */ }
                }
                @Override
                public void mouseEntered(MouseEvent e) { btnDelete.setForeground(Color.RED); }
                @Override
                public void mouseExited(MouseEvent e) { btnDelete.setForeground(new Color(189, 195, 199)); }
            });

            actionsPanel.add(btnEdit);
            actionsPanel.add(btnDelete);
            this.add(actionsPanel, BorderLayout.EAST);
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { controller.selectChannel(channel); }
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(idleColor); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g2.dispose();
    }
}