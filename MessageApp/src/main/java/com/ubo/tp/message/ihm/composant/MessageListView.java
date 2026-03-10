package com.ubo.tp.message.ihm.composant;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {
    private final MessageController controller;
    private final JPanel listContainer;
    private String currentFilter = "";

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Rechercher un message..."));
        searchField.addCaretListener(e -> setFilter(searchField.getText()));
        this.add(searchField, BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(245, 245, 245));
        this.add(new JScrollPane(listContainer), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField messageInput = new JTextField();
        JButton btnSend = new JButton("Envoyer");
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (messageInput.getText().endsWith("@")) {
                    System.out.println("Afficher la liste des membres pour mention...");
                }
            }
        });
        ActionListener sendAction = e -> {
            controller.sendMessage(messageInput.getText());
            messageInput.setText("");
        };
        btnSend.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);
        this.add(inputPanel, BorderLayout.SOUTH);

        refresh();
    }

    public void setFilter(String query) {
        this.currentFilter = query;
        this.refresh();
    }


    public void refresh() {
        listContainer.removeAll();
        
        List<Message> messages = controller.getFilteredMessages(currentFilter);

        for (Message m : messages) {
            MessageView mview = new MessageView(m, controller.isOwnMessage(m), controller);
            mview.setMaximumSize(new Dimension(Integer.MAX_VALUE, mview.getPreferredSize().height));
            listContainer.add(mview);
            listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        listContainer.revalidate();
        listContainer.repaint();
        SwingUtilities.invokeLater(() -> {
            listContainer.scrollRectToVisible(new Rectangle(0, listContainer.getHeight(), 1, 1));
        });
    }
}