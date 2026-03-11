package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.EmojiUtils;
import com.ubo.tp.message.ihm.controller.MessageController;

public class MessageListView extends JPanel {
    private final MessageController controller;
    private final JPanel listContainer;
    private final JScrollPane scrollPane;
    private String currentFilter = "";
    private String selectedImagePath = null; 
    private JWindow suggestionWindow;
    private DefaultListModel<String> suggestionModel;
    private JList<String> suggestionList;

    public MessageListView(MessageController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(240, 242, 245));
        initSuggestionWindow();

        JTextField searchField = new JTextField();
        setupPlaceholder(searchField, "Rechercher un message...");
        searchField.setPreferredSize(new Dimension(0, 40));
        searchField.addCaretListener(e -> setFilter(searchField.getText().equals("Rechercher un message...") ? "" : searchField.getText()));
        this.add(searchField, BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(240, 242, 245));
        scrollPane = new JScrollPane(listContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        this.add(scrollPane, BorderLayout.CENTER);
        
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setBackground(Color.WHITE);
        inputContainer.setBorder(new EmptyBorder(10, 15, 10, 15));

        JTextField messageInput = new JTextField();
        setupPlaceholder(messageInput, "Écrire un message...");

        setupAutocompleteLogic(messageInput);

        JButton btnAttach = new JButton("📷");
        btnAttach.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.selectedImagePath = chooser.getSelectedFile().getAbsolutePath();
                messageInput.setBorder(BorderFactory.createLineBorder(new Color(0, 132, 255), 2));
            }
        });

        JButton btnSend = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 132, 255));
                int[] x = {8, 25, 8}; int[] y = {5, 13, 21};
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
                if (selectedImagePath != null) controller.sendMessage("IMG:" + selectedImagePath + "|" + text);
                else controller.sendMessage(text);
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

    private void initSuggestionWindow() {
        suggestionWindow = new JWindow();
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setBackground(new Color(255, 255, 255));
        suggestionList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JScrollPane scroll = new JScrollPane(suggestionList);
        scroll.setBorder(null);
        suggestionWindow.add(scroll);
        suggestionWindow.setSize(150, 120);
    }

    private void setupAutocompleteLogic(JTextField input) {
        input.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
            
            private void update() {
                SwingUtilities.invokeLater(() -> {
                    String text = input.getText();
                    int caretPos = input.getCaretPosition();
                    if (caretPos <= 0) { suggestionWindow.setVisible(false); return; }

                    String beforeCaret = text.substring(0, caretPos);
                    int lastAt = beforeCaret.lastIndexOf('@');
                    int lastColon = beforeCaret.lastIndexOf(':');
                    
                    if (lastAt > lastColon && lastAt >= 0) {
                        showSuggestions(input, beforeCaret.substring(lastAt + 1), "user");
                    } else if (lastColon >= 0) {
                        showSuggestions(input, beforeCaret.substring(lastColon + 1), "emoji");
                    } else {
                        suggestionWindow.setVisible(false);
                    }
                });
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { applySelection(input); }
        });

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (suggestionWindow.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        suggestionList.setSelectedIndex(Math.min(suggestionList.getSelectedIndex() + 1, suggestionModel.size() - 1));
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        suggestionList.setSelectedIndex(Math.max(suggestionList.getSelectedIndex() - 1, 0));
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_TAB) {
                        applySelection(input);
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        suggestionWindow.setVisible(false);
                    }
                }
            }
        });
    }

    private void showSuggestions(JTextField input, String query, String type) {
        suggestionModel.clear();
        List<String> matches = new ArrayList<>();
        
        String filter = query.toLowerCase();

        if (type.equals("user")) {
            matches = controller.getUsers().stream()
                .map(u -> "@" + u.getUserTag())
                .filter(tag -> tag.toLowerCase().contains(filter))
                .collect(Collectors.toList());
        }  else if (type.equals("emoji")) {
            for (String[] entry : EmojiUtils.EMOJI_MAP) {
                String emojiCode = entry[0];
                if (emojiCode.toLowerCase().contains(filter)) {
                    matches.add(emojiCode);
                }
            
            }
        }

        if (matches.isEmpty()) {
            suggestionWindow.setVisible(false);
        } else {
            matches.forEach(suggestionModel::addElement);
            suggestionList.setSelectedIndex(0);
            
            int height = Math.min(matches.size() * 20 + 10, 150);
            suggestionWindow.setSize(180, height);

            try {
                Point p = input.getLocationOnScreen();
                suggestionWindow.setLocation(p.x, p.y - suggestionWindow.getHeight());
                suggestionWindow.setVisible(true);
            } catch (IllegalComponentStateException e) {
            }
        }
    }

    private void applySelection(JTextField input) {
        String selected = suggestionList.getSelectedValue();
        if (selected == null) return;

        String text = input.getText();
        int caretPos = input.getCaretPosition();
        String beforeCaret = text.substring(0, caretPos);
        
        int lastTrigger = Math.max(beforeCaret.lastIndexOf('@'), beforeCaret.lastIndexOf(':'));
        String newText = text.substring(0, lastTrigger) + selected + " " + text.substring(caretPos);
        
        input.setText(newText);
        input.requestFocus();
        suggestionWindow.setVisible(false);
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { if(field.getText().equals(placeholder)) { field.setText(""); field.setForeground(Color.BLACK); }}
            @Override public void focusLost(FocusEvent e) { if(field.getText().isEmpty()) { field.setText(placeholder); field.setForeground(Color.GRAY); }}
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