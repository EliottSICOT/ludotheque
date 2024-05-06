package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InscriptionDialog extends JDialog {
    /**
	 * 
	 */
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
        btnInscrire.addActionListener(e -> inscrireUtilisateur(nomField.getText(), prenomField.getText(), emailField.getText(), new String(motDePasseField.getPassword())));
        add(btnInscrire);

        btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> this.dispose());
        add(btnAnnuler);

        pack();
        setLocationRelativeTo(parent);
    }

    private void inscrireUtilisateur(String nom, String prenom, String email, String motDePasse) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role) VALUES (?, ?, ?, ?, 'utilisateur')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, motDePasse);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !");
                this.dispose(); // Ferme le dialogue après une inscription réussie
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}

