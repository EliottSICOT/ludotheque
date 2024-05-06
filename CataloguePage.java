package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CataloguePage extends JFrame {
    private static final long serialVersionUID = 8754395055976767116L;
    private JTable table;
    private JTextField searchField;
    private JButton searchButton, addButton;

    public CataloguePage(String userRole) {
        setTitle("Catalogue des Jeux");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initUI(userRole);
        loadData("");
    }

    private void initUI(String userRole) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        System.out.println("Rôle actuel de l'utilisateur: " + userRole);
        
        if ("admin".equals(userRole)) {
            addButton = new JButton("Ajouter un jeu");
            searchPanel.add(addButton);
            addButton.addActionListener(this::addButtonAction);
        }

        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(searchPanel, BorderLayout.NORTH);

        table = new JTable() {
            private static final long serialVersionUID = -7410007139800175343L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (row % 2 == 0) {
                    component.setBackground(new Color(220, 220, 220));
                } else {
                    component.setBackground(Color.WHITE);
                }
                return component;
            }
        };
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(this::searchAction);
    }

    private void loadData(String searchTerm) {
        String sql = "SELECT j.id, j.titre, c.nom AS categorie, j.description FROM jeux j JOIN categories c ON j.categorie_id = c.id"
                     + (searchTerm.isEmpty() ? "" : " WHERE j.titre LIKE ?");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!searchTerm.isEmpty()) {
                stmt.setString(1, "%" + searchTerm + "%");
            }
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Titre", "Catégorie", "Description"}, 0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("titre"), rs.getString("categorie"), rs.getString("description")});
            }
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addButtonAction(ActionEvent e) {
        AjoutJeuDialog ajoutDialog = new AjoutJeuDialog(this);
        ajoutDialog.setVisible(true);
        loadData("");  // Actualiser les données après ajout
    }

    private void searchAction(ActionEvent e) {
        loadData(searchField.getText().trim());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            String userRole = getUserRoleFromDB(1); // Supposons que l'ID utilisateur est 1
            new CataloguePage(userRole).setVisible(true);
        });
    }

    public static String getUserRoleFromDB(int userId) {
        String role = "utilisateur";  // Valeur par défaut
        String sql = "SELECT r.role_name FROM utilisateurs u JOIN roles r ON u.role_id = r.role_id WHERE u.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role_name");
            } else {
                System.out.println("Aucun rôle trouvé pour l'utilisateur ID: " + userId);
            }
        } catch (SQLException e) {
            System.out.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return role;
    }
}
