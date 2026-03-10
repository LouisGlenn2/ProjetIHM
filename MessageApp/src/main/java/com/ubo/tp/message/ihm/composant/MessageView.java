package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageView extends JPanel {
    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm");
    private final boolean isMe;

    public MessageView(Message message, boolean ownMessage, MessageController controller) {
        this.isMe = ownMessage;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(5, isMe ? 60 : 10, 5, isMe ? 10 : 60));

        JPanel bubble = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isMe ? new Color(0, 132, 255) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 14, 10, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel authorLabel = new JLabel(isMe ? "Moi" : message.getSender().getName());
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        authorLabel.setForeground(isMe ? new Color(200, 230, 255) : new Color(0, 102, 204));
        gbc.gridy = 0;
        bubble.add(authorLabel, gbc);

        String rawText = message.getText();
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        if (rawText != null && rawText.startsWith("IMG:")) {
            String payload = rawText.substring(4);
            String imagePath = payload;
            String textContent = "";

            if (payload.contains("|")) {
                int separator = payload.indexOf("|");
                imagePath = payload.substring(0, separator);
                textContent = payload.substring(separator + 1);
            }

            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                int newW = 250;
                int newH = (img.getHeight(null) * newW) / img.getWidth(null);
                
                JLabel imageLabel = new JLabel(new ImageIcon(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)));
                bubble.add(imageLabel, gbc);
            } catch (Exception e) {
                bubble.add(new JLabel("Impossible de charger l'image"), gbc);
            }

            if (!textContent.isEmpty()) {
                gbc.gridy = 2; 
                gbc.insets = new Insets(8, 0, 2, 0);
                String formatted = textContent.replaceAll("(@\\w+)", "<b style='color: #FFD700;'>$1</b>");
                JLabel label = new JLabel("<html><p style='width: 250px'>" + formatted + "</p></html>");
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                label.setForeground(isMe ? Color.WHITE : Color.BLACK);
                bubble.add(label, gbc);
            }
        } else {
            String formatted = rawText.replaceAll("(@\\w+)", "<b style='color: #FFD700;'>$1</b>");
            JLabel label = new JLabel("<html><p style='width: 250px'>" + formatted + "</p></html>");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(isMe ? Color.WHITE : Color.BLACK);
            bubble.add(label, gbc);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        
        JLabel timeLabel = new JLabel(dateFormat.format(new Date(message.getEmissionDate())));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(isMe ? new Color(210, 230, 255) : Color.GRAY);
        footer.add(timeLabel);

        if (isMe) {
            JButton btnDel = new JButton("✕");
            btnDel.setContentAreaFilled(false);
            btnDel.setBorderPainted(false);
            btnDel.setForeground(new Color(255, 150, 150));
            btnDel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDel.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this, "Supprimer ce message ?") == JOptionPane.YES_OPTION) {
                    controller.deleteMessage(message);
                }
            });
            footer.add(btnDel);
        }

        gbc.gridy = 3; 
        gbc.insets = new Insets(2, 0, 0, 0);
        bubble.add(footer, gbc);

        this.add(bubble, isMe ? BorderLayout.EAST : BorderLayout.WEST);
    }
}