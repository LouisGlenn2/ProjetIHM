package main.java.com.ubo.tp.message.ihm;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.DataManagerHelper;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.controller.LoginController;
import main.java.com.ubo.tp.message.ihm.controller.NavigationController;
import main.java.com.ubo.tp.message.ihm.controller.SignupController;

/**
 * Classe principale de l'application gérant la navigation et les sessions.
 */
public class MessageApp implements ISessionObserver {
    protected DataManager mDataManager;
    protected IDatabase mDatabase;
    protected Session mSession;
    
    // Vues et Contrôleurs
    protected MessageAppMainView mMainView;
    protected NavigationController mNavigationController;
    protected LoginController mLoginController;
    protected SignupController mSignupController;

    public MessageApp(DataManager dataManager, IDatabase database) {
        this.mDataManager = dataManager;
        this.mDatabase = database;
        this.mSession = new Session();
        this.mSession.addObserver(this);
    }

    public void init() {
        this.initLookAndFeel();
        this.initConsoleLogs();
        this.initGui();
        this.initDirectory();
    }

    protected void initLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initConsoleLogs() {
        DataManagerHelper.registerObserver(mDataManager, new ConsoleLogger());
        System.out.println("Logger initialisé.");
    }

    /**
     * Initialise l'interface et les contrôleurs de navigation.
     */
    protected void initGui() {
        this.mMainView = new MessageAppMainView();
        this.mNavigationController = new NavigationController(mMainView);
        
        // Initialisation des contrôleurs avec des callbacks de navigation
        this.mLoginController = new LoginController(mDatabase, mSession, this::showSignupView);
        this.mSignupController = new SignupController(mDatabase,mDataManager, this::showLoginView);

        // Affichage de la vue par défaut
        this.showLoginView();
        this.mMainView.showGUI();
    }
    public void show() {
        if (this.mMainView != null) {
            this.mMainView.setVisible(true);
        }
    }

    // --- Méthodes de Navigation ---

    /**
     * Affiche l'écran de connexion.
     */
    public void showLoginView() {
        mNavigationController.showPage(mLoginController.getLoginView());
    }

    /**
     * Affiche l'écran d'inscription.
     */
    public void showSignupView() {
        mNavigationController.showPage(mSignupController.getSignupView());
    }

    /**
     * Affiche le contenu principal (après connexion).
     */
    private void showMainContent() {
        // Pour l'instant, on affiche un panel vide ou un message de bienvenue
        JPanel mainPanel = new JPanel();
        mainPanel.add(new javax.swing.JLabel("Bienvenue sur MessageApp, " + mSession.getConnectedUser().getName()));
        
        mNavigationController.showPage(mainPanel);
    }

    // --- Gestion de la Session (ISessionObserver) ---

    @Override
    public void notifyLogin(User connectedUser) {
        System.out.println("Utilisateur connecté : " + connectedUser.getUserTag());
        showMainContent();
    }

    @Override
    public void notifyLogout() {
        System.out.println("Utilisateur déconnecté");
        showLoginView();
    }

    // --- Gestion du Répertoire ---

    protected void initDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Sélectionnez le répertoire d'échange");
        
        int returnVal = chooser.showOpenDialog(this.mMainView);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedDir = chooser.getSelectedFile();
            if (isValidExchangeDirectory(selectedDir)) {
                mDataManager.setExchangeDirectory(selectedDir.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(this.mMainView, "Répertoire invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                this.initDirectory();
            }
        } else {
            System.exit(0);
        }
    }

    protected boolean isValidExchangeDirectory(File directory) {
        return directory != null && directory.exists() && directory.isDirectory() && directory.canRead() && directory.canWrite();
    }
}