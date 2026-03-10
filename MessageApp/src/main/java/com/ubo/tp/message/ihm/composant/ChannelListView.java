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

        // --- EN-TÊTE ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));

        JLabel title = new JLabel(" CANAUX");
        title.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setOpaque(false);

        // Bouton Recherche
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

        // Bouton Ajout
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
        String name = JOptionPane.showInputDialog(this, "Nom du nouveau canal :");
        if (name != null && !name.trim().isEmpty()) {
            List<User> allAppUsers = controller.getAllAvailableUsers(); 
            List<User> selectedUsers = showUserSelectionDialog("Membres (vide = public)", allAppUsers, null);
            if (selectedUsers != null) {
                controller.createChannel(name, selectedUsers);
            }
        }
    }

    /**
     * Affiche la liste des utilisateurs avec sélection multiple par SIMPLE CLIC
     */
    @SuppressWarnings("serial")
	public List<User> showUserSelectionDialog(String title, List<User> allUsers, List<User> initialSelection) {
        DefaultListModel<User> model = new DefaultListModel<>();
        for (User u : allUsers) model.addElement(u);

        JList<User> userJList = new JList<>(model);
        
        // --- LOGIQUE DE SÉLECTION PAR SIMPLE CLIC (SANS CTRL) ---
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

        // Pré-sélection
        if (initialSelection != null) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < allUsers.size(); i++) {
                if (initialSelection.contains(allUsers.get(i))) indices.add(i);
            }
            userJList.setSelectedIndices(indices.stream().mapToInt(i -> i).toArray());
        }

        JScrollPane scrollPane = new JScrollPane(userJList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, title, JOptionPane.OK_CANCEL_OPTION);
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