package main.java.com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {
    private final MessageController controller;
    private final JPanel listContainer;
    private String currentFilter = "";

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());

        // --- BARRE DE RECHERCHE ---
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Rechercher un message..."));
        searchField.addCaretListener(e -> setFilter(searchField.getText()));
        this.add(searchField, BorderLayout.NORTH);

        // --- LISTE DES MESSAGES ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(245, 245, 245));
        this.add(new JScrollPane(listContainer), BorderLayout.CENTER);

        // --- BARRE D'ENVOI ---
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField messageInput = new JTextField();
        JButton btnSend = new JButton("Envoyer");

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
        for (Message m : controller.getFilteredMessages(currentFilter)) {
            MessageView mview = new MessageView(m, controller.isOwnMessage(m), false);
            listContainer.add(mview);
            listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        listContainer.revalidate();
        listContainer.repaint();
    }
}