package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EmpruntJeuDialog extends JDialog {
    private static final long serialVersionUID = 1046432724190753743L;
    private JComboBox<ComboItem> comboBoxJeux;
    private JTextField textFieldDateEmprunt;
    private JSpinner spinnerDateRetour;
    private JButton btnEmprunter;
    private Connection connection;
    private int utilisateurId;

    public EmpruntJeuDialog(JFrame parent, int utilisateurId) {
        super(parent, "Emprunter un jeu", true);
        this.utilisateurId = utilisateurId;
        setupUI();
        initializeDatabaseConnection();
        loadGamesIntoComboBox();
        setupActions();
    }

    private void setupUI() {
        setSize(500, 400);
        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblJeu = new JLabel("Sélectionner le jeu:");
        lblJeu.setFont(new Font("SansSerif", Font.BOLD, 16));
        comboBoxJeux = new JComboBox<>();
        comboBoxJeux.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mainPanel.add(lblJeu);
        mainPanel.add(comboBoxJeux);

        JLabel lblDateEmprunt = new JLabel("Date d'emprunt (dd-MM-yyyy):");
        lblDateEmprunt.setFont(new Font("SansSerif", Font.BOLD, 16));
        textFieldDateEmprunt = new JTextField(LocalDate.now().toString());
        textFieldDateEmprunt.setEditable(false);
        textFieldDateEmprunt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mainPanel.add(lblDateEmprunt);
        mainPanel.add(textFieldDateEmprunt);

        JLabel lblDateRetour = new JLabel("Date de retour prévue:");
        lblDateRetour.setFont(new Font("SansSerif", Font.BOLD, 16));
        spinnerDateRetour = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerDateRetour, "dd-MM-yyyy");
        spinnerDateRetour.setEditor(dateEditor);
        spinnerDateRetour.setValue(Date.from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        spinnerDateRetour.setFont(new Font("SansSerif", Font.PLAIN, 16));
        mainPanel.add(lblDateRetour);
        mainPanel.add(spinnerDateRetour);

        add(mainPanel, BorderLayout.CENTER);

        btnEmprunter = new JButton("Emprunter");
        btnEmprunter.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnEmprunter.setBackground(new Color(50, 150, 50));
        btnEmprunter.setForeground(Color.WHITE);
        btnEmprunter.setFocusPainted(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        buttonPanel.add(btnEmprunter);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeDatabaseConnection() {
        connection = DatabaseConnection.getConnection();
    }

    private void loadGamesIntoComboBox() {
        String sql = "SELECT id, titre FROM jeux WHERE disponible = TRUE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                comboBoxJeux.addItem(new ComboItem(resultSet.getString("titre"), resultSet.getInt("id")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des jeux: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupActions() {
        btnEmprunter.addActionListener(this::emprunterJeu);
    }

    private void emprunterJeu(ActionEvent event) {
        ComboItem selectedItem = (ComboItem) comboBoxJeux.getSelectedItem();
        String dateEmprunt = textFieldDateEmprunt.getText().trim();
        Date dateRetour = (Date) spinnerDateRetour.getValue();

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un jeu.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO emprunts (jeu_id, utilisateur_id, date_emprunt, date_retour_prevue) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE jeux SET disponible = FALSE WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, selectedItem.getValue());
            statement.setInt(2, this.utilisateurId);
            statement.setDate(3, java.sql.Date.valueOf(dateEmprunt));
            statement.setDate(4, new java.sql.Date(dateRetour.getTime()));
            statement.executeUpdate();

            updateStatement.setInt(1, selectedItem.getValue());
            updateStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Le jeu a été emprunté avec succès.");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'emprunt du jeu: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
