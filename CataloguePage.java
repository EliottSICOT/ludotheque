package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
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
        setResizable(false);

        initUI(userRole);
        loadData("");
    }

    private void initUI(String userRole) {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14)); 
        searchButton = new JButton("Rechercher");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14)); 
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);

        table = new JTable() {
            private static final long serialVersionUID = -7410007139800175343L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    component.setBackground(new Color(200, 200, 255));
                } else if (row % 2 == 0) {
                    component.setBackground(new Color(240, 240, 240));
                } else {
                    component.setBackground(Color.WHITE);
                }
                return component;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 14)); 
        table.setRowHeight(100); // Increase row height to accommodate images
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); 
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(this::searchAction);

        if ("admin".equals(userRole)) {
            addButton = new JButton("Ajouter un jeu");
            addButton.setFont(new Font("Arial", Font.PLAIN, 14)); 
            addButton.setBackground(new Color(50, 150, 50)); 
            addButton.setForeground(Color.WHITE); 
            addButton.setBorderPainted(false); 
            addButton.setFocusPainted(false); 
            addButton.addActionListener(this::addButtonAction);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            buttonPanel.add(addButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void loadData(String searchTerm) {
        String sql = "SELECT j.id, j.titre, c.nom AS categorie, j.description, j.image_path FROM jeux j JOIN categories c ON j.categorie_id = c.id"
                + (searchTerm.isEmpty() ? "" : " WHERE j.titre LIKE ?");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!searchTerm.isEmpty()) {
                stmt.setString(1, "%" + searchTerm + "%");
            }
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Titre", "Catégorie", "Description", "Image"}, 0);
            while (rs.next()) {
                String imagePath = "E:/Projet Ludothèque/Ludothèque/src/ui/img/";
                ImageIcon imageIcon = new ImageIcon(imagePath + rs.getString("image_path"));
                Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(image);
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("titre"), rs.getString("categorie"), rs.getString("description"), imageIcon});
            }
            table.setModel(model);
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(4).setCellRenderer(new ImageRenderer());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addButtonAction(ActionEvent e) {
        AjoutJeuDialog ajoutDialog = new AjoutJeuDialog(this);
        ajoutDialog.setVisible(true);
        loadData("");
    }

    private void searchAction(ActionEvent e) {
        loadData(searchField.getText().trim());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            String userRole = getUserRoleFromDB(1);
            new CataloguePage(userRole).setVisible(true);
        });
    }

    public static String getUserRoleFromDB(int userId) {
        String role = "utilisateur";
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

    private static class ImageRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -1028925336113040040L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            if (value != null) {
                label.setIcon((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
            }
            return label;
        }
    }
}
