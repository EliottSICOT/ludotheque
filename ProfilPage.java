package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class ProfilPage extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton modifierButton;
    private JButton annulerButton;

    private int userId;

    public ProfilPage(int userId) {
        this.userId = userId;

        setTitle("Profil Utilisateur");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));
        setLocationRelativeTo(null);

        initUI();
        loadData();
    }

    private void initUI() {
        add(new JLabel("Nom :"));
        nomField = new JTextField();
        add(nomField);

        add(new JLabel("Prénom :"));
        prenomField = new JTextField();
        add(prenomField);

        add(new JLabel("Email :"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Nouveau Mot de passe :"));
        passwordField = new JPasswordField();
        add(passwordField);

        modifierButton = new JButton("Modifier");
        modifierButton.addActionListener(e -> modifierProfil());
        add(modifierButton);

        annulerButton = new JButton("Annuler");
        annulerButton.addActionListener(e -> dispose());
        add(annulerButton);
    }

    private void loadData() {
        String sql = "SELECT nom, prenom, email FROM utilisateurs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nomField.setText(rs.getString("nom"));
                prenomField.setText(rs.getString("prenom"));
                emailField.setText(rs.getString("email"));
            } else {
                JOptionPane.showMessageDialog(this, "Utilisateur introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void modifierProfil() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String newPassword = new String(passwordField.getPassword());

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Cryptage du nouveau mot de passe
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, mot_de_passe = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, userId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Profil modifié avec succès");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la modification du profil", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            int userId = 1; // Remplacer 1 par l'ID de l'utilisateur connecté
            new ProfilPage(userId).setVisible(true);
        });
    }
}
