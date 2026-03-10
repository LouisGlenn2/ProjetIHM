package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.SearchController;

public class SearchView extends JPanel {
    private static final long serialVersionUID = 1L;

    public SearchView(SearchController searchController) {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(350, 450));
        this.setBackground(Color.WHITE);

        // --- 1. BARRE DE RECHERCHE STYLE "SPOTLIGHT" ---
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(0, 50));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        setupPlaceholder(searchField, "Rechercher (@ pour utilisateur, # pour canal)");

        // --- 2. PANNEAU DE RÉSULTATS ---
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText();
                resultsPanel.removeAll();
                
                // On ne cherche que si le texte n'est pas le placeholder et pas vide
                if (!query.isEmpty() && !query.equals("Rechercher (@ pour utilisateur, # pour canal)")) {
                    List<IMessageRecipient> matches = searchController.getSearchResults(query);
                    for (IMessageRecipient r : matches) {
                        resultsPanel.add(createResultItem(r, searchController));
                        resultsPanel.add(Box.createVerticalStrut(2)); // Petit espacement
                    }
                }
                
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        this.add(searchField, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crée une ligne de résultat interactive et stylisée.
     */
    private JPanel createResultItem(IMessageRecipient recipient, SearchController controller) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setPreferredSize(new Dimension(0, 50));
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(5, 15, 5, 15));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Détection du type pour l'icône et le texte
        String icon = (recipient instanceof User) ? "👤" : "💬";
        String labelText = (recipient instanceof User) ? 
                "@" + ((User)recipient).getUserTag() : 
                "# " + ((Channel)recipient).getName();

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        JLabel lblName = new JLabel(labelText);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblName.setForeground(new Color(44, 62, 80));

        item.add(lblIcon, BorderLayout.WEST);
        item.add(lblName, BorderLayout.CENTER);

        // Effets de survol (Hover)
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.openRecipient(recipient);
                Window window = SwingUtilities.getWindowAncestor(SearchView.this);
                if (window != null) window.dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });

        return item;
    }

    /**
     * Gère l'affichage du texte d'aide en gris.
     */
    private void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.LIGHT_GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.LIGHT_GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}