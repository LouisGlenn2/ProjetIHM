package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {
    private final MessageController controller;
    private final JPanel listContainer;
    private final JScrollPane scrollPane;
    private String currentFilter = "";
    private String selectedImagePath = null; 

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 242, 245));

        JTextField searchField = new JTextField();
        setupPlaceholder(searchField, "Rechercher un message...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.addCaretListener(e -> setFilter(searchField.getText().equals("Rechercher un message...") ? "" : searchField.getText()));
        this.add(searchField, BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(240, 242, 245));
        scrollPane = new JScrollPane(listContainer);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        this.add(scrollPane, BorderLayout.CENTER);
        
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setBackground(Color.WHITE);
        inputContainer.setBorder(new EmptyBorder(10, 15, 10, 15));

        JTextField messageInput = new JTextField();
        setupPlaceholder(messageInput, "Écrire un message...");

        JButton btnAttach = new JButton("📷");
        btnAttach.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAttach.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.selectedImagePath = chooser.getSelectedFile().getAbsolutePath();
                messageInput.setText(""); 
                messageInput.requestFocus();
                messageInput.setBorder(BorderFactory.createLineBorder(new Color(0, 132, 255), 2));
            }
        });

        JButton btnSend = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 132, 255));
                int[] x = {5, 22, 5}; int[] y = {5, 13, 21};
                g2.fillPolygon(x, y, 3);
                g2.dispose();
            }
        };
        btnSend.setPreferredSize(new Dimension(30, 25));
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);

        ActionListener actionEnvoi = e -> {
            String text = messageInput.getText().trim();
            if (text.equals("Écrire un message...")) text = "";

            if (selectedImagePath != null || !text.isEmpty()) {
                if (selectedImagePath != null) {
                    controller.sendMessage("IMG:" + selectedImagePath + "|" + text);
                } else {
                    controller.sendMessage(text);
                }
                selectedImagePath = null;
                messageInput.setText("");
                messageInput.setBorder(UIManager.getBorder("TextField.border"));
                setupPlaceholder(messageInput, "Écrire un message...");
                refresh();
            }
        };

        btnSend.addActionListener(actionEnvoi);
        messageInput.addActionListener(actionEnvoi);

        inputContainer.add(btnAttach, BorderLayout.WEST);
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
            public void focusGained(FocusEvent e) { if(field.getText().equals(placeholder)) { field.setText(""); field.setForeground(Color.BLACK); }}
            @Override
            public void focusLost(FocusEvent e) { if(field.getText().isEmpty()) { field.setText(placeholder); field.setForeground(Color.GRAY); }}
        });
    }
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void setFilter(String q) { this.currentFilter = q; refresh(); }
    public void refresh() {
    	listContainer.removeAll(); 
        
        List<Message> messages = controller.getFilteredMessages(currentFilter);
        if (messages != null) {
            for (Message m : messages) {
                MessageView mview = new MessageView(m, controller.isOwnMessage(m), controller);
                listContainer.add(mview);
                listContainer.add(Box.createRigidArea(new Dimension(0, 8))); 
            }
        }
        
        listContainer.add(Box.createVerticalGlue());
        
        listContainer.revalidate();
        listContainer.repaint();
        scrollToBottom();
    }
}