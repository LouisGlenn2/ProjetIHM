package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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
    private String currentFilter = ""; //

    public ListUserView(List<User> users) {
        this.mUsers = users;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // --- BARRE DE RECHERCHE ---
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Filtrer les membres..."));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            public void changedUpdate(DocumentEvent e) { updateFilter(); }
            private void updateFilter() {
                currentFilter = searchField.getText().toLowerCase();
                refresh();
            }
        });
        this.add(searchField, BorderLayout.NORTH);

        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.listContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        this.refresh();
    }

    public void setUsers(List<User> users) { this.mUsers = users; }
    public void setOnUserSelected(Consumer<User> callback) { this.onUserSelected = callback; }

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
                    listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }
}