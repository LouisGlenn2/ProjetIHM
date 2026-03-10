package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.SearchController;

public class SearchView extends JPanel {
    public SearchView(SearchController searchController) {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(300, 400));

        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Recherche (@ pour utilisateur)"));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                resultsPanel.removeAll();
                List<IMessageRecipient> matches = searchController.getSearchResults(searchField.getText());
                for (IMessageRecipient r : matches) {
                    String label = (r instanceof User) ? "@" + ((User)r).getUserTag() : "# " + ((Channel)r).getName();
                    JButton btn = new JButton(label);
                    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                    btn.addActionListener(ev -> {
                        searchController.openRecipient(r);
                        SwingUtilities.getWindowAncestor(SearchView.this).dispose();
                    });
                    resultsPanel.add(btn);
                }
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });

        this.add(searchField, BorderLayout.NORTH);
        this.add(new JScrollPane(resultsPanel), BorderLayout.CENTER);
    }
}