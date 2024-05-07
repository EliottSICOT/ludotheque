package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class InscriptionDialog extends JDialog {
    private static final long serialVersionUID = 4307788889046048092L;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField emailField;
    private JPasswordField motDePasseField;
    private JButton btnInscrire;
    private JButton btnAnnuler;

    public InscriptionDialog(JFrame parent) {
        super(parent, "Inscription", true);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Nom :"));
        nomField = new JTextField();
        add(nomField);

        add(new JLabel("Prénom :"));
        prenomField = new JTextField();
        add(prenomField);

        add(new JLabel("Email :"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Mot de passe :"));
        motDePasseField = new JPasswordField();
        add(motDePasseField);

        btnInscrire = new JButton("S'inscrire");
        btnInscrire.addActionListener(e -> inscrireUtilisateur());
        add(btnInscrire);

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> this.dispose());
        add(btnAnnuler);

        pack();
        setLocationRelativeTo(parent);
    }

    private void inscrireUtilisateur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String motDePasse = new String(motDePasseField.getPassword());

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = BCrypt.hashpw(motDePasse, BCrypt.gensalt());

        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, hashedPassword);
            stmt.setInt(5, 1); // ID de rôle pour 'utilisateur'

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun utilisateur ajouté, vérifiez vos données", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key value")) {
                JOptionPane.showMessageDialog(this, "Cet email est déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
