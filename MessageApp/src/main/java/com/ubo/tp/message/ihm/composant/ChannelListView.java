package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;

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

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));

        JLabel title = new JLabel(" CANAUX");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);

        JButton btnSearch = new JButton("🔍");
        btnSearch.addActionListener(e -> openSearchDialog());

        JButton btnAdd = new JButton("+");
        btnAdd.setToolTipText("Créer un canal");
        btnAdd.addActionListener(e -> openCreationDialog());

        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnAdd);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- LISTE ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        this.add(new JScrollPane(listPanel), BorderLayout.CENTER);

        refresh();
    }

    private void openSearchDialog() {
        if (controller.getSearchController() != null) {
            SearchView searchView = new SearchView(this.controller.getSearchController());
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Recherche", true);
            dialog.add(searchView);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
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
                    selectedUsers = showUserSelectionDialog("Membres autorisés", controller.getAllAvailableUsers(), null);
                    if (selectedUsers == null) return;
                }
                controller.createChannel(name, selectedUsers);
            }
        }
    }


    /**
     * Affiche le dialogue complet (Barre de recherche + Liste cochable)
     */
    public List<User> showUserSelectionDialog(String title, List<User> allUsers, List<User> initialSelection) {
        DefaultListModel<User> model = new DefaultListModel<>();
        allUsers.forEach(model::addElement);

        JList<User> userJList = createUserJList(model);
        applyInitialSelection(userJList, allUsers, initialSelection);

        JTextField searchField = createSearchField(userJList, model, allUsers);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(searchField, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(userJList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, mainPanel, title, JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION) ? userJList.getSelectedValuesList() : null;
    }

    /**
     * Configure la JList avec le rendu Checkbox et le Simple Clic
     */
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
            cb.setBackground(isSelected ? l.getSelectionBackground() : l.getBackground());
            cb.setForeground(isSelected ? l.getSelectionForeground() : l.getForeground());
            cb.setOpaque(true);
            return cb;
        });

        return list;
    }

    /**
     * Crée la barre de recherche avec filtrage dynamique
     */
    private JTextField createSearchField(JList<User> userJList, DefaultListModel<User> model, List<User> allUsers) {
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
                if (index != -1) userJList.addSelectionInterval(index, index);
            }
        });

        return searchField;
    }

    /**
     * Initialise la sélection lors de l'ouverture (cas de modification)
     */
    private void applyInitialSelection(JList<User> list, List<User> allUsers, List<User> initialSelection) {
        if (initialSelection != null) {
            for (User u : initialSelection) {
                int index = allUsers.indexOf(u);
                if (index != -1) {
                    list.getSelectionModel().addSelectionInterval(index, index);
                }
            }
        }
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