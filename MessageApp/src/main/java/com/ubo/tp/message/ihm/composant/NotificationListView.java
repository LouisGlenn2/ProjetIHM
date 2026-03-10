package com.ubo.tp.message.ihm.composant;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.controller.MessageController;

/**
 * Vue gérant la pile de notifications (Toasts) flottantes.
 */
public class NotificationListView extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private final List<NotificationView> notifications = new ArrayList<>();
    private final JPanel stackPanel;
    private final JLabel counterLabel;
    private final MessageController controller;

    /**
     * Constructeur de la liste de notifications.
     * @param controller Le contrôleur de messages pour gérer les clics.
     */
    public NotificationListView(MessageController controller) {
        this.controller = controller;
        
        // Configuration du panel principal (transparent pour flotter sur l'UI)
        this.setOpaque(false); 
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel interne pour empiler les NotificationView
        stackPanel = new JPanel();
        stackPanel.setOpaque(false);
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));

        // Badge de compteur (Pastille rouge moderne)
        counterLabel = new JLabel();
        counterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        counterLabel.setOpaque(true);
        counterLabel.setBackground(new Color(231, 76, 60)); // Rouge corail
        counterLabel.setForeground(Color.WHITE);
        counterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Ajout d'arrondis via une bordure vide pour le padding interne
        counterLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        counterLabel.setVisible(false);

        // Assemblage
        // Le stackPanel au centre pour occuper l'espace
        this.add(stackPanel, BorderLayout.CENTER); 
        
        // Le compteur en bas à droite
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(counterLabel);
        this.add(footer, BorderLayout.SOUTH); 
    }

    /**
     * Ajoute une nouvelle notification à la pile.
     */
    public void addNotification(String text, Message msg, Object senderSource, Runnable onRemove) {
        final NotificationView[] itemWrapper = new NotificationView[1];
        
        // Création de la vue individuelle avec ses callbacks
        itemWrapper[0] = new NotificationView(text, msg, () -> {
            // Action lors du clic sur le texte : on change de destinataire
            if (senderSource instanceof User) {
                controller.setRecipient((User) senderSource);
            } else if (senderSource instanceof Channel) {
                controller.setRecipient((Channel) senderSource);
            }
            onRemove.run(); 
            removeNotification(itemWrapper[0]); 
        }, () -> {
            // Action lors du clic sur la croix (fermeture)
            onRemove.run(); 
            removeNotification(itemWrapper[0]); 
        });
        
        // On ajoute à la liste et on rafraîchit
        notifications.add(itemWrapper[0]);
        refreshUI();
    }

    /**
     * Supprime une notification spécifique de la liste.
     */
    private void removeNotification(NotificationView item) {
        if (item != null) {
            notifications.remove(item);
        } else if (!notifications.isEmpty()) {
            // Sécurité : supprime la plus ancienne si l'item est nul
            notifications.remove(0);
        }
        refreshUI();
    }

    /**
     * Met à jour l'affichage graphique de la pile.
     */
    private void refreshUI() {
        stackPanel.removeAll();
        
        // On utilise un Glue pour que les notifications "tombent" vers le bas
        stackPanel.add(Box.createVerticalGlue());
        
        int size = notifications.size();
        int maxDisplayed = 3; // On ne montre que les 3 dernières pour ne pas saturer l'écran

        if (size > maxDisplayed) {
            int extraCount = size - maxDisplayed;
            counterLabel.setText("+" + extraCount + " notifications");
            counterLabel.setVisible(true);
            
            // On affiche uniquement les 'maxDisplayed' derniers éléments de la liste
            for (int i = size - maxDisplayed; i < size; i++) {
                stackPanel.add(notifications.get(i));
                stackPanel.add(Box.createVerticalStrut(8)); // Espace entre les toasts
            }
        } else {
            // Moins de 3 notifications : on cache le compteur et on affiche tout
            counterLabel.setVisible(false);
            for (NotificationView item : notifications) {
                stackPanel.add(item);
                stackPanel.add(Box.createVerticalStrut(8));
            }
        }

        // Forcer Swing à recalculer le layout et redessiner
        stackPanel.revalidate();
        stackPanel.repaint();
    }
    
    /**
     * Getter pour connaître le nombre de notifications actives.
     */
    public int getNotificationCount() {
        return notifications.size();
    }
}