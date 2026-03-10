package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import com.ubo.tp.message.datamodel.User;

public class ListUserView extends JPanel {
    private static final long serialVersionUID = 1L;
    private List<User> mUsers;
    private Consumer<User> onUserSelected;
    private final JPanel listContainer;
    private String currentFilter = "";
    
    // Texte du placeholder
    private final String PLACEHOLDER = "Filtrer par nom ou @tag...";

    public ListUserView(List<User> users) {
        this.mUsers = users;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- BARRE DE RECHERCHE AVEC PLACEHOLDER ---
        JTextField searchField = new JTextField(PLACEHOLDER);
        searchField.setPreferredSize(new Dimension(0, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(Color.GRAY); // Couleur grise au début
        
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Gestion du Focus pour faire disparaître/apparaître le texte
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText(PLACEHOLDER);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            public void changedUpdate(DocumentEvent e) { updateFilter(); }
            private void updateFilter() {
                String text = searchField.getText();
                // On ne filtre pas si c'est le texte du placeholder
                if (text.equals(PLACEHOLDER)) {
                    currentFilter = "";
                } else {
                    currentFilter = text.toLowerCase();
                }
                refresh();
            }
        });
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(searchField, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        // --- LISTE DES UTILISATEURS ---
        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        this.add(scrollPane, BorderLayout.CENTER);

        this.refresh();
    }

    public void setUsers(List<User> users) { 
        this.mUsers = users; 
        refresh();
    }
    
    public void setOnUserSelected(Consumer<User> callback) { 
        this.onUserSelected = callback; 
    }

    public void refresh() {
        listContainer.removeAll();
        if (mUsers != null) {
            for (User user : mUsers) {
                String fullName = user.getName().toLowerCase();
                String tag = user.getUserTag().toLowerCase();
                
                if (currentFilter.isEmpty() || fullName.contains(currentFilter) || tag.contains(currentFilter)) {
                    UserView userView = new UserView(user);
                    
                    userView.addSelectionListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (onUserSelected != null) {
                                onUserSelected.accept(user);
                            }
                        }
                    });
                    
                    listContainer.add(userView);
                    listContainer.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }
        }
        
        listContainer.add(Box.createVerticalGlue());
        listContainer.revalidate();
        listContainer.repaint();
    }
}