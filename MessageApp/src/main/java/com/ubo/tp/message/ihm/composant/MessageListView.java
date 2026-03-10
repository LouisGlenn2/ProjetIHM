package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final MessageController controller;
    private final JPanel listContainer;
    private final JScrollPane scrollPane;
    private String currentFilter = "";

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 242, 245));

        // --- 1. BARRE DE RECHERCHE ---
        JTextField searchField = new JTextField();
        setupPlaceholder(searchField, "Rechercher un message...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        searchField.addCaretListener(e -> {
            String text = searchField.getText();
            if (!text.equals("Rechercher un message...")) {
                setFilter(text);
            } else {
                setFilter("");
            }
        });
        this.add(searchField, BorderLayout.NORTH);

        // --- 2. ZONE DE DISCUSSION ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(240, 242, 245));
        
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        // --- 3. BARRE D'ENVOI ---
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setBackground(Color.WHITE);
        inputContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JTextField messageInput = new JTextField();
        setupPlaceholder(messageInput, "Écrire un message...");
        messageInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JButton btnSend = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 132, 255));
                int[] xPoints = {5, 20, 5};
                int[] yPoints = {5, 12, 20};
                g2.fillPolygon(xPoints, yPoints, 3);
                g2.dispose();
            }
        };

        btnSend.setPreferredSize(new Dimension(30, 25));
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ActionListener sendAction = e -> {
            String text = messageInput.getText().trim();
            if (!text.isEmpty() && !text.equals("Écrire un message...")) {
                controller.sendMessage(text);
                messageInput.setText("");
                messageInput.requestFocus();
                refresh();
            }
        };

        btnSend.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);

        inputContainer.add(messageInput, BorderLayout.CENTER);
        inputContainer.add(btnSend, BorderLayout.EAST);
        this.add(inputContainer, BorderLayout.SOUTH);

        refresh();
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    public void setFilter(String query) {
        this.currentFilter = query;
        this.refresh();
    }

    public void refresh() {
        listContainer.removeAll();
        List<Message> messages = controller.getFilteredMessages(currentFilter);
        if (messages != null) {
            for (Message m : messages) {
                MessageView mview = new MessageView(m, controller.isOwnMessage(m), controller);
                mview.setMaximumSize(new Dimension(Integer.MAX_VALUE, mview.getPreferredSize().height));
                listContainer.add(mview);
                listContainer.add(Box.createRigidArea(new Dimension(0, 8))); 
            }
        }
        listContainer.add(Box.createVerticalGlue());
        listContainer.revalidate();
        listContainer.repaint();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}