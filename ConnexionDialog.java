package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ConnexionDialog extends JDialog {
    private static final long serialVersionUID = 8720300844050691923L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean successfulLogin = false;
    private String userRole = "guest";  // Valeur par défaut

    public ConnexionDialog(Frame owner) {
        super(owner, "Connexion", true);
        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panneau pour les champs de saisie
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);
        add(inputPanel, BorderLayout.CENTER);

        // Bouton de connexion
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attemptLogin(emailField.getText(), new String(passwordField.getPassword()));
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Tente une connexion
    public void attemptLogin(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT role_name FROM utilisateurs JOIN roles ON utilisateurs.role_id = roles.role_id WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                successfulLogin = true;
                userRole = rs.getString("role_name");
                dispose(); // Ferme le dialogue après une connexion réussie
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants incorrects", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccessful() {
        return successfulLogin;
    }

    public String getUserRole() {
        return userRole;
    }
}
