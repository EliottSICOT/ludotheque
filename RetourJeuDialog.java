package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RetourJeuDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JComboBox<ComboItem> comboBoxJeux;
    private JButton btnRetour;
    private int utilisateurId;

    public RetourJeuDialog(JFrame parent, int utilisateurId) {
        super(parent, "Retour d'un jeu", true);
        this.utilisateurId = utilisateurId;
        setupUI();
        loadGamesIntoComboBox();
    }

    private void setupUI() {
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel panelCenter = new JPanel(new GridLayout(1, 2, 10, 10));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCenter.add(new JLabel("Sélectionner le jeu:"));
        comboBoxJeux = new JComboBox<>();
        panelCenter.add(comboBoxJeux);

        add(panelCenter, BorderLayout.CENTER);

        btnRetour = new JButton("Retourner");
        btnRetour.addActionListener(e -> retournerJeu());
        JPanel panelSouth = new JPanel();
        panelSouth.add(btnRetour);
        add(panelSouth, BorderLayout.SOUTH);
    }

    private void loadGamesIntoComboBox() {
        String sql = "SELECT id, titre FROM emprunts WHERE utilisateur_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, utilisateurId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comboBoxJeux.addItem(new ComboItem(resultSet.getString("titre"), resultSet.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des jeux: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            // Log the error (consider using a logging framework)
        }
    }

    private void retournerJeu() {
        ComboItem selectedItem = (ComboItem) comboBoxJeux.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un jeu.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "DELETE FROM emprunts WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, selectedItem.getValue());
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Le jeu a été retourné avec succès.");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du retour du jeu: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            // Log the error (consider using a logging framework)
        }
    }

    class ComboItem {
        private String key;
        private int value;

        public ComboItem(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key;
        }

        public int getValue() {
            return value;
        }
    }
}
