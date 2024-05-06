package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AjoutJeuDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3568410995856060343L;
	private JTextField txtTitre, txtDescription;
    private JComboBox<Category> cmbCategorie;

    public AjoutJeuDialog(JFrame owner) {
        super(owner, "Ajouter un jeu", true);
        setSize(400, 300);
        setLayout(new BorderLayout(10, 10)); // Utilisation d'un BorderLayout

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // GridLayout pour les champs
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtTitre = new JTextField();
        inputPanel.add(new JLabel("Titre:"));
        inputPanel.add(txtTitre);

        cmbCategorie = new JComboBox<>(loadCategories());
        inputPanel.add(new JLabel("Catégorie:"));
        inputPanel.add(cmbCategorie);

        txtDescription = new JTextField();
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(txtDescription);

        add(inputPanel, BorderLayout.CENTER);

        JButton btnSubmit = new JButton("Ajouter");
        btnSubmit.addActionListener(e -> submitGame());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);

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

    private void submitGame() {
        String titre = txtTitre.getText();
        Category categorie = (Category) cmbCategorie.getSelectedItem();
        String description = txtDescription.getText();

        if (titre.isEmpty() || description.isEmpty() || categorie == null) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO jeux (titre, categorie_id, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titre);
            stmt.setInt(2, categorie.getId());
            stmt.setString(3, description);

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

    // Classe interne pour représenter les catégories avec un nom et un ID
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

        // Pour que le JComboBox affiche correctement le nom de la catégorie
        @Override
        public String toString() {
            return name;
        }
    }
}
