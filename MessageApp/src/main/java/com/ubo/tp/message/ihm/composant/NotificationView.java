package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.ubo.tp.message.datamodel.Message;

public class NotificationView extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Color bgColor = new Color(45, 45, 48, 230);

    public NotificationView(String text, Message message, Runnable onClick, Runnable onClose) {
        this.setLayout(new BorderLayout(10, 0));
        this.setOpaque(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        Dimension size = new Dimension(280, 50);
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setBorder(new EmptyBorder(5, 12, 5, 12));

        JLabel iconLabel = new JLabel("📧"); 
        iconLabel.setForeground(new Color(0, 120, 215));
        this.add(iconLabel, BorderLayout.WEST);

        JLabel lbl = new JLabel("<html><body'>" + text + "</body></html>");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
        this.add(lbl, BorderLayout.CENTER);

        JLabel btnClose = new JLabel("X");
        btnClose.setForeground(new Color(180, 180, 180));
        btnClose.setFont(new Font("Arial", Font.BOLD, 14));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { onClose.run(); }
            @Override
            public void mouseEntered(MouseEvent e) { btnClose.setForeground(Color.WHITE); }
            @Override
            public void mouseExited(MouseEvent e) { btnClose.setForeground(new Color(180, 180, 180)); }
        });
        
        JPanel closeWrapper = new JPanel(new BorderLayout());
        closeWrapper.setOpaque(false);
        closeWrapper.add(btnClose, BorderLayout.NORTH);
        this.add(closeWrapper, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
        g2.setColor(new Color(100, 100, 100, 100));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
        g2.dispose();
    }
}