package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;

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
        this.setPreferredSize(new Dimension(200, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));

        JLabel title = new JLabel(" CANAUX");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);
        JButton btnSearch = new JButton("🔍");
        btnSearch.addActionListener(e -> {
            if (controller.getSearchController() != null) {
                SearchView searchView = new SearchView(this.controller.getSearchController()); 
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Recherche", true);
                dialog.add(searchView);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        });
        JButton btnAdd = new JButton("+");
        btnAdd.setToolTipText("Créer un canal");
        btnAdd.addActionListener(e -> openCreationDialog());

        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnAdd);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- LISTE DES CANAUX ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        this.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        refresh();
    }


    private void openCreationDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField nameField = new JTextField();
        JCheckBox privateBox = new JCheckBox("Canal privé (restreindre l'accès)");
        panel.add(new JLabel("Nom du nouveau canal :"));
        panel.add(nameField);
        panel.add(privateBox);
        int result = JOptionPane.showConfirmDialog(this, panel, "Nouveau Canal", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                List<User> selectedUsers = new ArrayList<>();
                if (privateBox.isSelected()) {
                    List<User> allAppUsers = controller.getAllAvailableUsers(); 
                    selectedUsers = showUserSelectionDialog("Sélectionner les membres autorisés", allAppUsers, null);
                    if (selectedUsers == null) return; 
                }
                controller.createChannel(name, selectedUsers);
            }
        }
    }

    /**
     * Affiche la liste des utilisateurs avec une barre de recherche et sélection multiple par SIMPLE CLIC
     */
    @SuppressWarnings("serial")
    public List<User> showUserSelectionDialog(String title, List<User> allUsers, List<User> initialSelection) {
        DefaultListModel<User> model = new DefaultListModel<>();
        for (User u : allUsers) model.addElement(u);
        JList<User> userJList = new JList<>(model);
        userJList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });
        userJList.setCellRenderer(new ListCellRenderer<User>() {
            private final JCheckBox checkbox = new JCheckBox();
            @Override
            public Component getListCellRendererComponent(JList<? extends User> list, User user, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                checkbox.setText("@" + user.getUserTag() + " (" + user.getName() + ")");
                checkbox.setSelected(isSelected);
                checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                checkbox.setOpaque(true);
                return checkbox;
            }
        });
        if (initialSelection != null) {
            for (User u : initialSelection) {
                userJList.getSelectionModel().addSelectionInterval(allUsers.indexOf(u), allUsers.indexOf(u));
            }
        }
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Rechercher un utilisateur..."));
        searchField.addCaretListener(e -> {
            String filter = searchField.getText().toLowerCase();
            List<User> selectedNow = userJList.getSelectedValuesList();
            model.clear();
            for (User u : allUsers) {
                if (u.getUserTag().toLowerCase().contains(filter) || u.getName().toLowerCase().contains(filter)) {
                    model.addElement(u);
                }
            }
            for (User u : selectedNow) {
                int index = model.indexOf(u);
                if (index != -1) {
                    userJList.addSelectionInterval(index, index);
                }
            }
        });
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(searchField, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(userJList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, mainPanel, title, JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION) ? userJList.getSelectedValuesList() : null;
    }

    public void refresh() {
        listPanel.removeAll();
        User me = controller.getConnectedUser();
        for (Channel c : controller.getChannels()) {
            boolean isVisible = c.getUsers().isEmpty() || c.getCreator().equals(me) || c.getUsers().contains(me);
            if (isVisible) {
                listPanel.add(new ChannelView(c, controller));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
}