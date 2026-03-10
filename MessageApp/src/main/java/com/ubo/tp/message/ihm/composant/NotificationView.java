package com.ubo.tp.message.ihm.composant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ubo.tp.message.datamodel.Message;

public class NotificationView extends JPanel {

    private static final long serialVersionUID = 1L;

    public NotificationView(String text, Message message, Runnable onClick, Runnable onClose) {
        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(60, 63, 65));
        this.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        Dimension fixedSize = new Dimension(250, 30); 
        this.setPreferredSize(fixedSize);
        this.setMaximumSize(fixedSize);
        this.setMinimumSize(fixedSize);

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel btnClose = new JLabel("X");
        btnClose.setOpaque(false); 
        btnClose.setForeground(Color.LIGHT_GRAY);
        btnClose.setFont(new Font("Arial", Font.BOLD, 10));
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { onClose.run(); }
            @Override
            public void mouseEntered(MouseEvent e) { btnClose.setForeground(Color.WHITE); }
            @Override
            public void mouseExited(MouseEvent e) { btnClose.setForeground(Color.LIGHT_GRAY); }
        });

        gbc.gridx = 1;     
        gbc.gridy = 0;      
        gbc.weightx = 0;   
        gbc.anchor = GridBagConstraints.NORTHEAST; 
        gbc.insets = new Insets(0, 5, 0, 0);      
        this.add(btnClose, gbc);

        JLabel lbl = new JLabel("<html><body >" + text + "</body></html>");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { onClick.run(); }
        });

        gbc.gridx = 0;     
        gbc.gridy = 0;      
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0); 
        this.add(lbl, gbc);
    }
}