package ui;

import javax.swing.*;
import java.awt.*;

public class AccueilPage extends JFrame {
    private static final long serialVersionUID = -1230935483694996925L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Ludothèque - Accueil";
    private boolean isConnected = false; // Gestion de l'état de connexion
    private String userRole = "guest"; // Rôle par défaut pour les utilisateurs non connectés
    private JMenuItem menuItemCatalogue;

    public AccueilPage() {
        initializeWindow();
        setupMenuBar();
        setupWelcomeLabel();
    }

    private void initializeWindow() {
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuUtilisateur = createUserMenu();
        JMenu menuPrincipal = createMainMenu();

        menuBar.add(menuPrincipal);
        menuBar.add(menuUtilisateur);
        setJMenuBar(menuBar);
    }

    private JMenu createUserMenu() {
        JMenu menu = new JMenu("Utilisateur");
        JMenuItem menuItemInscription = new JMenuItem("Inscription");
        JMenuItem menuItemConnexion = new JMenuItem("Connexion");
        menu.add(menuItemInscription);
        menu.add(menuItemConnexion);

        menuItemInscription.addActionListener(e -> showInscriptionDialog());
        menuItemConnexion.addActionListener(e -> showConnexionDialog());

        return menu;
    }

    private JMenu createMainMenu() {
        JMenu menu = new JMenu("Menu");
        menuItemCatalogue = new JMenuItem("Catalogue de Jeux");
        menuItemCatalogue.setEnabled(isConnected); // Activé en fonction de l'état de connexion
        menuItemCatalogue.addActionListener(e -> {
            if (isConnected) {
                showCataloguePage();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez vous connecter pour accéder au catalogue.", "Accès refusé", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItemCatalogue);
        return menu;
    }

    private void setupWelcomeLabel() {
        JLabel welcomeLabel = new JLabel("Bienvenue à la Ludothèque", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        getContentPane().add(welcomeLabel, BorderLayout.CENTER);
    }

    private void showInscriptionDialog() {
        InscriptionDialog inscriptionDialog = new InscriptionDialog(this);
        inscriptionDialog.setVisible(true);
    }

    private void showConnexionDialog() {
        ConnexionDialog connexionDialog = new ConnexionDialog(this);
        connexionDialog.setVisible(true);
        if (connexionDialog.isSuccessful()) {
            isConnected = true;
            userRole = connexionDialog.getUserRole();
            menuItemCatalogue.setEnabled(isConnected); // Active le menu catalogue après la connexion
        }
    }

    private void showCataloguePage() {
        if ("admin".equals(userRole)) {
            CataloguePage cataloguePage = new CataloguePage(userRole);
            cataloguePage.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Accès au catalogue autorisé uniquement pour les administrateurs.", "Accès refusé", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new AccueilPage().setVisible(true));
    }
}