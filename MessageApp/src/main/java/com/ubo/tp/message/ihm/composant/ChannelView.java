package com.ubo.tp.message.ihm.composant;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final Color idleColor = new Color(248, 249, 250);
    private final Color hoverColor = new Color(232, 240, 254);
    private final Color activeTextColor = new Color(52, 73, 94);
    private final Color notificationColor = new Color(231, 76, 60);

    public ChannelView(Channel channel, ChannelController controller) {
        this.setLayout(new BorderLayout(10, 0));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        this.setPreferredSize(new Dimension(200, 45));
        this.setBackground(idleColor);
        this.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- SECTION GAUCHE : ICONE ---
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

        // --- SECTION CENTRALE : NOM ET BADGE ---
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        centerPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(activeTextColor);
        centerPanel.add(nameLabel);

        if (controller.isUnread(channel.getUuid())) {
            JPanel badge = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(notificationColor);
                    g2.fillOval(0, 0, 8, 8);
                    g2.dispose();
                }
            };
            badge.setPreferredSize(new Dimension(8, 8));
            badge.setOpaque(false);
            centerPanel.add(badge);
        }
        this.add(centerPanel, BorderLayout.CENTER);

        // --- SECTION DROITE : ACTIONS (EDIT/DELETE OU LEAVE) ---
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        User currentUser = controller.getConnectedUser();
        
        if (channel.getCreator().equals(currentUser)) {
            // Bouton Editer (uniquement créateur)
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

            // Bouton Supprimer (uniquement créateur)
            JLabel btnDelete = new JLabel("✕");
            btnDelete.setForeground(new Color(189, 195, 199));
            btnDelete.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int choice = JOptionPane.showConfirmDialog(null, "Supprimer " + channel.getName() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) { 
                         controller.deleteChannel(channel, channel.getCreator());
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) { btnDelete.setForeground(Color.RED); }
                @Override
                public void mouseExited(MouseEvent e) { btnDelete.setForeground(new Color(189, 195, 199)); }
            });

            actionsPanel.add(btnEdit);
            actionsPanel.add(btnDelete);
        } else {
            // Bouton Quitter (uniquement membres non-créateurs)
            JLabel btnLeave = new JLabel("logout");
            btnLeave.setToolTipText("Quitter le channel");
            btnLeave.setForeground(new Color(189, 195, 199));
            
            btnLeave.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int choice = JOptionPane.showConfirmDialog(
                        null, 
                        "Voulez-vous vraiment quitter le canal " + channel.getName() + " ?", 
                        "Quitter le canal", 
                        JOptionPane.YES_NO_OPTION
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        controller.leaveChannel(channel);
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) { btnLeave.setForeground(new Color(230, 126, 34)); } // Orange
                @Override
                public void mouseExited(MouseEvent e) { btnLeave.setForeground(new Color(189, 195, 199)); }
            });
            actionsPanel.add(btnLeave);
        }

        this.add(actionsPanel, BorderLayout.EAST);

        // --- INTERACTION CLIC SUR LA LIGNE ---
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