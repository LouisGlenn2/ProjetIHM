package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.DataManagerHelper;
import main.java.com.ubo.tp.message.core.database.IDatabase;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.controller.LoginController;
import main.java.com.ubo.tp.message.ihm.controller.NavigationController;
import main.java.com.ubo.tp.message.ihm.controller.SignupController;
import main.java.com.ubo.tp.message.ihm.controller.UserListController;

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
        this.mMainView.setLogoutCallback(this::handleLogout);
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
    private void handleLogout() {
        mSession.disconnect(); // Déconnecte l'utilisateur
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
        // Création du contrôleur pour la liste des utilisateurs
        UserListController userListController = new UserListController(mDatabase);
    
        // Création du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
    
        // Ajout de la vue de la liste des utilisateurs sur le côté gauche
        mainPanel.add(userListController.getListUserView(), BorderLayout.WEST);
    
        // Ajout d'un message de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue, " + mSession.getConnectedUser().getName() + " !");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
    
        // Ajout d'un panneau central pour le contenu principal
        JPanel contentPanel = new JPanel();
        contentPanel.add(new JLabel("Contenu principal ici"));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    
        // Affichage du panneau principal
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
        showLoginView(); // Redirige vers la page de connexion
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