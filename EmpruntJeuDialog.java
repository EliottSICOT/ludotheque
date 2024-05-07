package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EmpruntJeuDialog extends JDialog {
    private static final long serialVersionUID = -1037581564781367620L;
    private JComboBox<String> comboBoxJeux;
    private JSpinner dateEmpruntSpinner;
    private JSpinner dateRetourSpinner;
    private JButton emprunterButton;

    public EmpruntJeuDialog(JFrame parent) {
        super(parent, "Emprunter un Jeu", true);
        setLayout(new GridLayout(4, 2, 10, 10));
        initializeComponents();
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void initializeComponents() {
        comboBoxJeux = new JComboBox<>();
        fillGameTitles();
        add(new JLabel("Jeu :"));
        add(comboBoxJeux);

        dateEmpruntSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deEmprunt = new JSpinner.DateEditor(dateEmpruntSpinner, "dd/MM/yyyy");
        dateEmpruntSpinner.setEditor(deEmprunt);
        add(new JLabel("Date d'emprunt :"));
        add(dateEmpruntSpinner);

        dateRetourSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deRetour = new JSpinner.DateEditor(dateRetourSpinner, "dd/MM/yyyy");
        dateRetourSpinner.setEditor(deRetour);
        add(new JLabel("Date de retour prévue :"));
        add(dateRetourSpinner);

        emprunterButton = new JButton("Emprunter");
        emprunterButton.addActionListener(e -> registerBorrowing());
        add(emprunterButton);
    }

    private void fillGameTitles() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT titre FROM jeux ORDER BY titre";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comboBoxJeux.addItem(rs.getString("titre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des jeux: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerBorrowing() {
        String selectedGame = (String) comboBoxJeux.getSelectedItem();
        Date empruntDate = (Date) ((SpinnerDateModel) dateEmpruntSpinner.getModel()).getDate();
        Date retourDate = (Date) ((SpinnerDateModel) dateRetourSpinner.getModel()).getDate();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO emprunts (jeu_id, utilisateur_id, date_emprunt, date_retour_prevue) VALUES ((SELECT id FROM jeux WHERE titre = ?), ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selectedGame);
            stmt.setInt(2, getCurrentUserId());
            stmt.setDate(3, new java.sql.Date(empruntDate.getTime()));
            stmt.setDate(4, new java.sql.Date(retourDate.getTime()));

            int result = stmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Emprunt enregistré avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de l'enregistrement de l'emprunt.", "Échec", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur SQL: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Dummy function to represent getting the current user's ID
    private int getCurrentUserId() {
        return 1; // Assume user ID 1 is logged in, replace with actual user session management logic
    }
}
