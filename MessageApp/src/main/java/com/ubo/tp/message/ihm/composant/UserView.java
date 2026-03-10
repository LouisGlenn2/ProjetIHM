package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import com.ubo.tp.message.datamodel.User;

public class UserView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final Color idleColor = new Color(248, 249, 250);
    private final Color hoverColor = new Color(232, 240, 254);
    private final Color borderColor = new Color(218, 220, 224);
    
    private final Color onlineColor = new Color(46, 204, 113); 
    private final Color offlineColor = new Color(231, 76, 60); 
    public UserView(User user) {
        this.setLayout(new BorderLayout(15, 0));
        this.setPreferredSize(new Dimension(220, 65));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        this.setBackground(idleColor);
        this.setBorder(new EmptyBorder(10, 15, 10, 15));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel statusPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isOnline = user.isOnline();
                g2.setColor(isOnline ? onlineColor : offlineColor);
                int size = 12;
                int y = (getHeight() - size) / 2;
                g2.fillOval(0, y, size, size);
                g2.setColor(UserView.this.getBackground());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, y, size, size);
                g2.dispose();
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setPreferredSize(new Dimension(15, 45));
        this.add(statusPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(32, 33, 36));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagLabel.setForeground(new Color(95, 99, 104));

        infoPanel.add(nameLabel);
        infoPanel.add(tagLabel);
        this.add(infoPanel, BorderLayout.CENTER);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint(); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(idleColor);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
        g2.dispose();
    }

    public void addSelectionListener(MouseAdapter adapter) {
        this.addMouseListener(adapter);
        for (Component c : getComponents()) {
            c.addMouseListener(adapter);
            if (c instanceof Container) {
                for (Component sub : ((Container) c).getComponents()) {
                    sub.addMouseListener(adapter);
                }
            }
        }
    }
}