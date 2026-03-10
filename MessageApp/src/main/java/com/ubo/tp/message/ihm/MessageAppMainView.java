package com.ubo.tp.message.ihm;



import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;



import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;

import javax.swing.JMenuBar;

import javax.swing.JMenuItem;

import javax.swing.JOptionPane;

import javax.swing.JPanel;

import javax.swing.SwingUtilities;

import com.ubo.tp.message.ihm.composant.NotificationListView;



/**

 * Classe de la vue principale de l'application.

 */

public class MessageAppMainView extends JFrame {



/**

* 

*/

private static final long serialVersionUID = 1L;
private NotificationListView notificationManager;



public MessageAppMainView() {

this.initGUI();

}



public void setContent(JPanel newPanel) {

    this.getContentPane().removeAll();

    this.add(newPanel, BorderLayout.CENTER);

    this.revalidate();

    this.repaint();

}



/**

* Initialisation de l'interface graphique.

*/

private void initGUI() {

this.setTitle("Messagerie UBO");

ImageIcon logo = new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\logo_20.png");

if (logo != null) {

this.setIconImage(logo.getImage());

}



this.setSize(new Dimension(800, 600));

this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

this.setLocationRelativeTo(null);

this.setJMenuBar(this.createMenuBar());

}



/**

* Affiche la fenêtre de manière thread-safe.

*/

public void showGUI() {

SwingUtilities.invokeLater(() -> {

this.setVisible(true);

});

}



/**

* Crée la barre de menu avec les icônes demandées.

*/

private Runnable onLogout;



public void setLogoutCallback(Runnable onLogout) {

this.onLogout = onLogout;

}



private Runnable onDeleteAccount;



public void setDeleteAccountCallback(Runnable onDeleteAccount) {

this.onDeleteAccount = onDeleteAccount;

}



private Runnable onUpdateAccount;



public void setUpdateAccountCallback(Runnable onUpdateAccount) {

this.onUpdateAccount = onUpdateAccount;

}





private JMenuBar createMenuBar() {

JMenuBar menuBar = new JMenuBar();



JMenu menuFile = new JMenu("Mon compte");

JMenuItem itemExit = new JMenuItem("Quitter",new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\exitIcon_20.png"));

itemExit.setToolTipText("Fermer l'application");

itemExit.addActionListener(e -> System.exit(0));

menuFile.add(itemExit);

// Bouton "Déconnexion"

JMenuItem itemLogout = new JMenuItem("Déconnexion", new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\logoutIcon_20.png"));

itemLogout.setToolTipText("Se déconnecter");

itemLogout.addActionListener(e -> {

if (onLogout != null) {

onLogout.run(); // Appelle le callback de déconnexion

}

});

menuFile.add(itemLogout);



// Bouton "Supprimer mon compte"

JMenuItem itemDeleteAccount = new JMenuItem("Supprimer mon compte", new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\deleteIcon_20.png"));

itemDeleteAccount.setToolTipText("Supprimer définitivement votre compte");

itemDeleteAccount.addActionListener(e -> {

if (onDeleteAccount != null) {

onDeleteAccount.run(); // Appelle le callback de suppression de compte

}

});

menuFile.add(itemDeleteAccount);



// Bouton "Modifier mon compte"

JMenuItem itemupdateAccount = new JMenuItem("Modifier mon compte", new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\deleteIcon_20.png"));

itemupdateAccount.setToolTipText("Modifier mon compte");

itemupdateAccount.addActionListener(e -> {

if (onUpdateAccount != null) {

onUpdateAccount.run(); // Appelle le callback de suppression de compte

}

});

menuFile.add(itemupdateAccount);



JMenu menuHelp = new JMenu("?");

JMenuItem itemAbout = new JMenuItem("À propos", new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\editIcon_20.png"));

itemAbout.addActionListener(e -> showAboutDialog());

menuHelp.add(itemAbout);



menuBar.add(menuFile);

menuBar.add(menuHelp);

return menuBar;

}

public void initNotificationManager(NotificationListView manager) {
    this.notificationManager = manager;
    
    this.getLayeredPane().add(notificationManager, JLayeredPane.POPUP_LAYER);
    updateNotificationBounds();
    this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            updateNotificationBounds();
        }
    });
}

/**
 * Calcule et applique la position du panneau de notification
 */
private void updateNotificationBounds() {
    if (notificationManager != null) {
        int width = 180;
        int height = 400; 
        int margin = 1;
        int x = margin;
        int totalHeight = this.getContentPane().getHeight();
        int y = totalHeight - height - margin;

        notificationManager.setBounds(x, y, width, height);
    }
}



/**

* Affiche la boîte de dialogue 'À propos'.

*/

private void showAboutDialog() {

JOptionPane.showMessageDialog(this, 

"UBO M2-TIIL\nDépartement Informatique", 

"À propos",

JOptionPane.INFORMATION_MESSAGE, 

new ImageIcon("C:\\Ihm\\MessageApp\\bin\\main\\resources\\images\\logo_50.png"));

}



/**

* Charge une icône depuis les ressources.

*/

@SuppressWarnings("unused")

private ImageIcon loadIcon(String path) {

System.out.println("DEBUG: Root Path: " + getClass().getResource("/")); 

    URL imgURL = getClass().getResource(path);

    if (imgURL == null) {

        System.err.println("ERREUR: Image non trouvée au chemin: " + path);

    }

return (imgURL != null) ? new ImageIcon(imgURL) : null;

}


public void setMainComponent(JPanel panel) {

    this.getContentPane().removeAll();

    this.add(panel);

    this.revalidate();

    this.repaint();
    if (notificationManager != null) {
        this.getLayeredPane().moveToFront(notificationManager);
    }

}

}