package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.ChannelController;

public class ChannelListView extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ChannelController controller;
    private final JPanel listPanel;

    public ChannelListView(ChannelController controller) {
        this.controller = controller;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(240, 0));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 5, 10, 5));

        JLabel title = new JLabel("CANAUX");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(149, 165, 166));
        headerPanel.add(title, BorderLayout.WEST);

        // Bouton Ajouter (+)
        JButton btnAdd = new JButton("+");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 18));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setToolTipText("Créer un nouveau canal");
        btnAdd.addActionListener(e -> openCreationDialog());
        headerPanel.add(btnAdd, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- LISTE DES CANAUX ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    private void openCreationDialog() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField nameField = new JTextField();
        JCheckBox privateBox = new JCheckBox("Canal privé (restreindre l'accès)");

        inputPanel.add(new JLabel("Nom du nouveau canal :"));
        inputPanel.add(nameField);
        inputPanel.add(privateBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Nouveau Canal", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                List<User> selectedUsers = new ArrayList<>();
                if (privateBox.isSelected()) {
                    selectedUsers = showUserSelectionDialog("Inviter des membres", controller.getAllAvailableUsers(), null);
                    if (selectedUsers == null) return;
                }
                controller.createChannel(name, selectedUsers);
            }
        }
    }

    public List<User> showUserSelectionDialog(String title, List<User> allUsers, List<User> initialSelection) {
        DefaultListModel<User> model = new DefaultListModel<>();
        allUsers.forEach(model::addElement);

        JList<User> userJList = createUserJList(model);
        applyInitialSelection(userJList, allUsers, initialSelection);

        JTextField searchField = createSearchField(userJList, model, allUsers);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 10));
        mainPanel.add(searchField, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(userJList);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, mainPanel, title, JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION) ? userJList.getSelectedValuesList() : null;
    }

    private JList<User> createUserJList(DefaultListModel<User> model) {
        JList<User> list = new JList<>(model);
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) super.removeSelectionInterval(index0, index1);
                else super.addSelectionInterval(index0, index1);
            }
        });
        list.setCellRenderer((l, user, index, isSelected, cellHasFocus) -> {
            JCheckBox cb = new JCheckBox("@" + user.getUserTag() + " (" + user.getName() + ")");
            cb.setSelected(isSelected);
            cb.setOpaque(true);
            cb.setBackground(isSelected ? new Color(232, 240, 254) : Color.WHITE);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            return cb;
        });
        return list;
    }

    private JTextField createSearchField(JList<User> userJList, DefaultListModel<User> model, List<User> allUsers) {
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Rechercher..."));
        searchField.addCaretListener(e -> {
            String filter = searchField.getText().toLowerCase();
            List<User> selectedNow = userJList.getSelectedValuesList();
            model.clear();
            for (User u : allUsers) {
                if (u.getUserTag().toLowerCase().contains(filter) || u.getName().toLowerCase().contains(filter)) {
                    model.addElement(u);
                }
            }
            // Préserver la sélection lors du filtrage
            for (User u : selectedNow) {
                int index = model.indexOf(u);
                if (index != -1) userJList.addSelectionInterval(index, index);
            }
        });
        return searchField;
    }

    private void applyInitialSelection(JList<User> list, List<User> allUsers, List<User> initialSelection) {
        if (initialSelection != null) {
            for (User u : initialSelection) {
                int index = allUsers.indexOf(u);
                if (index != -1) list.getSelectionModel().addSelectionInterval(index, index);
            }
        }
    }

    public void refresh() {
        listPanel.removeAll();
        User me = controller.getConnectedUser();
        if (me != null) {
            for (Channel c : controller.getChannels()) {
                boolean isVisible = c.getUsers().isEmpty() || c.getCreator().equals(me) || c.getUsers().contains(me);
                if (isVisible) {
                    listPanel.add(new ChannelView(c, controller));
                    listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}