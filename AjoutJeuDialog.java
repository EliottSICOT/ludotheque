package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AjoutJeuDialog extends JDialog {
    private static final long serialVersionUID = -3568410995856060343L;
    private JTextField txtTitre, txtDescription, txtImagePath;
    private JComboBox<Category> cmbCategorie;
    private JButton btnSelectImage, btnSubmit;
    private JTable tableJeuxDisponibles;

    public AjoutJeuDialog(JFrame owner) {
        super(owner, "Ajouter un jeu", true);
        setSize(600, 700);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitre = new JLabel("Titre:");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtTitre = new JTextField();
        txtTitre.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(lblTitre);
        inputPanel.add(txtTitre);

        JLabel lblCategorie = new JLabel("Catégorie:");
        lblCategorie.setFont(new Font("SansSerif", Font.BOLD, 16));
        cmbCategorie = new JComboBox<>(loadCategories());
        cmbCategorie.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(lblCategorie);
        inputPanel.add(cmbCategorie);

        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtDescription = new JTextField();
        txtDescription.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(lblDescription);
        inputPanel.add(txtDescription);

        JLabel lblImage = new JLabel("Image:");
        lblImage.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtImagePath = new JTextField();
        txtImagePath.setEditable(false);
        txtImagePath.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnSelectImage = new JButton("Choisir une image");
        btnSelectImage.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnSelectImage.addActionListener(e -> chooseImage());
        inputPanel.add(lblImage);
        inputPanel.add(btnSelectImage);
        inputPanel.add(new JLabel(""));
        inputPanel.add(txtImagePath);

        add(inputPanel, BorderLayout.NORTH);

        // Section des jeux disponibles
        JPanel jeuxPanel = new JPanel(new BorderLayout(10, 10));
        jeuxPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JLabel lblJeuxDisponibles = new JLabel("Jeux Disponibles:");
        lblJeuxDisponibles.setFont(new Font("SansSerif", Font.BOLD, 16));
        jeuxPanel.add(lblJeuxDisponibles, BorderLayout.NORTH);

        tableJeuxDisponibles = new JTable();
        tableJeuxDisponibles.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Titre", "Catégorie", "Description"}
        ));
        tableJeuxDisponibles.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tableJeuxDisponibles.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(tableJeuxDisponibles);
        jeuxPanel.add(scrollPane, BorderLayout.CENTER);

        add(jeuxPanel, BorderLayout.CENTER);

        // Bouton Ajouter
        btnSubmit = new JButton("Ajouter");
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSubmit.setBackground(new Color(50, 150, 50));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(e -> submitGame());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);

        loadAvailableGames();
        setLocationRelativeTo(owner);
    }

    private Category[] loadCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nom FROM categories");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return categories.toArray(new Category[0]);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String targetDirectory = "img/";
            File destFile = new File(targetDirectory + file.getName());

            try {
                // Check if file exists in target directory
                if (!file.toPath().equals(destFile.toPath())) {
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                txtImagePath.setText(destFile.getPath());
            } catch (FileAlreadyExistsException ex) {
                JOptionPane.showMessageDialog(this, "Un fichier avec le même nom existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la copie du fichier: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void submitGame() {
        String titre = txtTitre.getText();
        Category categorie = (Category) cmbCategorie.getSelectedItem();
        String description = txtDescription.getText();
        String imagePath = txtImagePath.getText();

        if (titre.isEmpty() || description.isEmpty() || categorie == null || imagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO jeux (titre, categorie_id, description, image_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titre);
            stmt.setInt(2, categorie.getId());
            stmt.setString(3, description);
            stmt.setString(4, imagePath);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Jeu ajouté avec succès!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Aucune ligne ajoutée, vérifiez votre saisie", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableGames() {
        DefaultTableModel model = (DefaultTableModel) tableJeuxDisponibles.getModel();
        model.setRowCount(0); // Clear existing rows

        String sql = "SELECT j.id, j.titre, c.nom AS categorie, j.description FROM jeux j JOIN categories c ON j.categorie_id = c.id WHERE j.disponible = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("categorie"),
                        rs.getString("description")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    class Category {
        private int id;
        private String name;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
