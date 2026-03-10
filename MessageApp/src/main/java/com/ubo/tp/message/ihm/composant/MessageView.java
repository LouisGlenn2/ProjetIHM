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
    
    // Modification du format pour inclure le jour, le mois et l'année
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy - HH:mm");
    private final boolean isMe;

    public MessageView(Message message, boolean ownMessage, MessageController controller) {
        this.isMe = ownMessage;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        
        int leftPad = isMe ? 60 : 10;
        int rightPad = isMe ? 10 : 60;
        this.setBorder(new EmptyBorder(5, leftPad, 5, rightPad));

        // --- LA BULLE ---
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

        // --- 1. NOM DE L'AUTEUR ---
        JLabel authorLabel = new JLabel();
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (isMe) {
            authorLabel.setText("Moi");
            authorLabel.setForeground(new Color(200, 230, 255));
        } else {
            authorLabel.setText(message.getSender().getName() + " (@" + message.getSender().getUserTag() + ")");
            authorLabel.setForeground(new Color(0, 102, 204));
        }
        gbc.gridy = 0;
        bubble.add(authorLabel, gbc);

        // --- 2. CORPS DU MESSAGE ---
        String text = message.getText().replaceAll("(@\\w+)", "<b style='color: #FFD700;'>$1</b>");
        JLabel content = new JLabel("<html><p style='width: 250px'>" + text + "</p></html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        content.setForeground(isMe ? Color.WHITE : Color.BLACK);
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 3, 0);
        bubble.add(content, gbc);

        // --- 3. FOOTER (DATE + HEURE + SUPPRIMER) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        
        // Affichage de la date et de l'heure formatées
        JLabel dateTimeLabel = new JLabel(dateFormat.format(new Date(message.getEmissionDate())));
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dateTimeLabel.setForeground(isMe ? new Color(210, 230, 255) : Color.GRAY);
        footer.add(dateTimeLabel);

        if (isMe) {
            JButton btnDel = new JButton("✕");
            btnDel.setMargin(new Insets(0,0,0,0));
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
        gbc.gridy = 2;
        bubble.add(footer, gbc);

        this.add(bubble, isMe ? BorderLayout.EAST : BorderLayout.WEST);
    }
}