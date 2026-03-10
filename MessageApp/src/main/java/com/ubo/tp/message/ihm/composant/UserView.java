package com.ubo.tp.message.ihm.composant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import com.ubo.tp.message.datamodel.User;

/**
 * Vue moderne pour afficher les informations d'un utilisateur.
 */
public class UserView extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Couleurs du thème
    private final Color idleColor = new Color(248, 249, 250);
    private final Color hoverColor = new Color(232, 240, 254);
    private final Color borderColor = new Color(218, 220, 224);
    private final Color onlineColor = new Color(46, 204, 113);
    private final Color offlineColor = new Color(149, 165, 166);

    public UserView(User user) {
        // Configuration du layout et de la taille
        this.setLayout(new BorderLayout(15, 0));
        this.setPreferredSize(new Dimension(220, 65));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        this.setBackground(idleColor);
        this.setBorder(new EmptyBorder(10, 15, 10, 15));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- PARTIE GAUCHE : INDICATEUR DE STATUT (PASTILLE) ---
        JPanel statusPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(user.isOnline() ? onlineColor : offlineColor);
                // Dessine un cercle de statut centré verticalement
                g2.fillOval(0, getHeight()/2 - 6, 12, 12);
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setPreferredSize(new Dimension(15, 45));
        this.add(statusPanel, BorderLayout.WEST);

        // --- CENTRE : INFOS (NOM ET @TAG) ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(32, 33, 36));

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagLabel.setForeground(new Color(95, 99, 104));

        infoPanel.add(nameLabel);
        infoPanel.add(tagLabel);
        this.add(infoPanel, BorderLayout.CENTER);

        // --- EFFET DE SURVOL ---
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(idleColor);
            }
        });
    }

    // Peinture des coins arrondis et de la bordure légère
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond arrondi
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        
        // Bordure fine
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
        
        g2.dispose();
    }

    public void addSelectionListener(MouseAdapter adapter) {
        this.addMouseListener(adapter);
        // Propagation du clic aux composants enfants
        for (Component c : getComponents()) {
            c.addMouseListener(adapter);
            if (c instanceof Container) {
                for (Component sub : ((Container) c).getComponents()) {
                    sub.addMouseListener(adapter);
                }
            }
        }
    }
}