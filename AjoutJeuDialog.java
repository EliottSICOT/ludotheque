package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class AjoutJeuDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3568410995856060343L;
	private JTextField txtTitre, txtDescription;
    private JComboBox<Category> cmbCategorie; // Utilisation d'une classe interne Category pour stocker à la fois le nom et l'ID

    public AjoutJeuDialog(JFrame owner) {
        super(owner, "Ajouter un jeu", true);
        setSize(400, 300);
        setLayout(new GridLayout(4, 2, 10, 10));  // Improved layout

        add(new JLabel("Titre:"));
        txtTitre = new JTextField();
        add(txtTitre);

        add(new JLabel("Catégorie:"));
        cmbCategorie = new JComboBox<>(loadCategories());
        add(cmbCategorie);

        add(new JLabel("Description:"));
        txtDescription = new JTextField();
        add(txtDescription);

        JButton btnSubmit = new JButton("Ajouter");
        btnSubmit.addActionListener(e -> submitGame());
        add(btnSubmit);

        setLocationRelativeTo(owner);
    }

    private Category[] loadCategories() {
        Vector<Category> categories = new Vector<>();
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
