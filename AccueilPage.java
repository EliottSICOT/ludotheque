package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccueilPage extends JFrame {
    private static final long serialVersionUID = -1230935483694996925L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Ludothèque - Accueil";
    private boolean isConnected = false; 
    private String userRole = "guest"; // Default role when not logged in
    private String userName = "guest"; // Default name when not logged in
    private JLabel welcomeLabel;
    private JMenuItem menuItemDeconnexion, menuItemConnexion, menuItemCatalogue, menuItemEmprunterJeu;
    private JMenuItem menuItemProfil;
    
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
        menuBar.add(createMainMenu());
        menuBar.add(createUserMenu());
              
        setJMenuBar(menuBar);
    }

    private JMenu createMainMenu() {
        JMenu menu = new JMenu("Menu");
        menuItemCatalogue = new JMenuItem("Catalogue de Jeux");
        menuItemCatalogue.setEnabled(isConnected);
        menuItemCatalogue.addActionListener(e -> showCataloguePage());

        menuItemEmprunterJeu = new JMenuItem("Emprunter un Jeu");
        menuItemEmprunterJeu.setEnabled(isConnected);
        menuItemEmprunterJeu.addActionListener(e -> showEmpruntJeuDialog());

        menu.add(menuItemCatalogue);
        menu.add(menuItemEmprunterJeu);
        return menu;
    }

    private JMenu createUserMenu() {
        JMenu menu = new JMenu("Utilisateur");
        JMenuItem menuItemInscription = new JMenuItem("Inscription");
        menuItemConnexion = new JMenuItem("Connexion");
        menuItemDeconnexion = new JMenuItem("Déconnexion");
        menuItemProfil = new JMenuItem("Profil"); // Création du JMenuItem pour accéder au profil

        menuItemInscription.addActionListener(e -> showInscriptionDialog());
        menuItemConnexion.addActionListener(e -> showConnexionDialog());
        menuItemDeconnexion.addActionListener(e -> logout());
        menuItemProfil.addActionListener(e -> showProfilPage()); // Ajout d'une action pour afficher le profil

        menu.add(menuItemInscription);
        menu.add(menuItemConnexion);
        menu.add(menuItemDeconnexion);
        menu.add(menuItemProfil); // Ajout du JMenuItem au menu Utilisateur  

        updateMenuItems(); // Initial visibility setup
        return menu;
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
            userName = connexionDialog.getUserName(); // Fetch the first name from ConnexionDialog
            updateMenuItems();
            updateWelcomeLabel();
        }
    }

    private void showCataloguePage() {
        CataloguePage cataloguePage = new CataloguePage(userRole);
        cataloguePage.setVisible(true);
    }

    private void showEmpruntJeuDialog() {
        EmpruntJeuDialog empruntJeuDialog = new EmpruntJeuDialog(this, getDefaultCloseOperation());
        empruntJeuDialog.setVisible(true);
    }
    
    private void showProfilPage() {
        ConnexionDialog connexionDialog = new ConnexionDialog(this);
        connexionDialog.setVisible(true);
        if (connexionDialog.isSuccessful()) {
            int userId = getUserIdByEmail(connexionDialog.getEmail()); // Récupérer l'ID de l'utilisateur
            ProfilPage profilPage = new ProfilPage(userId);
            profilPage.setVisible(true);
        }
    }
    
    private int getUserIdByEmail(String email) {
        int userId = -1; // Valeur par défaut si l'ID n'est pas trouvé
        String sql = "SELECT id FROM utilisateurs WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
            } else {
                JOptionPane.showMessageDialog(this, "Utilisateur introuvable pour l'email: " + email, "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur SQL: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return userId;
    }

    private void logout() {
        isConnected = false;
        userRole = "guest";
        userName = "guest";
        updateMenuItems();
        updateWelcomeLabel();
        JOptionPane.showMessageDialog(this, "Déconnexion réussie.", "Déconnexion", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupWelcomeLabel() {
        welcomeLabel = new JLabel("Bienvenue à la Ludothèque", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        getContentPane().add(welcomeLabel, BorderLayout.CENTER);
    }

    private void updateMenuItems() {
        menuItemDeconnexion.setVisible(isConnected);
        menuItemConnexion.setEnabled(!isConnected);
        menuItemCatalogue.setEnabled(isConnected);
        menuItemEmprunterJeu.setEnabled(isConnected);
    }

    private void updateWelcomeLabel() {
        if (isConnected) {
            welcomeLabel.setText("Bienvenue à la Ludothèque, " + userName); // Use first name
        } else {
            welcomeLabel.setText("Bienvenue à la Ludothèque");
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new AccueilPage().setVisible(true));
    }
}
