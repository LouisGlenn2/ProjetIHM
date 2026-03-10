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
        
        this.setOpaque(false); 
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        stackPanel = new JPanel();
        stackPanel.setOpaque(false);
        stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));

        counterLabel = new JLabel();
        counterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        counterLabel.setOpaque(true);
        counterLabel.setBackground(new Color(231, 76, 60)); 
        counterLabel.setForeground(Color.WHITE);
        counterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        counterLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        counterLabel.setVisible(false);

        this.add(stackPanel, BorderLayout.CENTER); 
        
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
        
        itemWrapper[0] = new NotificationView(text, msg, () -> {
            if (senderSource instanceof User) {
                controller.setRecipient((User) senderSource);
            } else if (senderSource instanceof Channel) {
                controller.setRecipient((Channel) senderSource);
            }
            onRemove.run(); 
            removeNotification(itemWrapper[0]); 
        }, () -> {
            onRemove.run(); 
            removeNotification(itemWrapper[0]); 
        });
        
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
            notifications.remove(0);
        }
        refreshUI();
    }

    /**
     * Met à jour l'affichage graphique de la pile.
     */
    private void refreshUI() {
        stackPanel.removeAll();
        
        stackPanel.add(Box.createVerticalGlue());
        
        int size = notifications.size();
        int maxDisplayed = 3; 

        if (size > maxDisplayed) {
            int extraCount = size - maxDisplayed;
            counterLabel.setText("+" + extraCount + " notifications");
            counterLabel.setVisible(true);
            
            for (int i = size - maxDisplayed; i < size; i++) {
                stackPanel.add(notifications.get(i));
                stackPanel.add(Box.createVerticalStrut(8)); 
            }
        } else {
            counterLabel.setVisible(false);
            for (NotificationView item : notifications) {
                stackPanel.add(item);
                stackPanel.add(Box.createVerticalStrut(8));
            }
        }
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