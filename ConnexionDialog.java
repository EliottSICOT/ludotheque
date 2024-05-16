package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class ConnexionDialog extends JDialog {
    private static final long serialVersionUID = 8720300844050691923L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private boolean successfulLogin = false;
    private String userRole = "guest";  // Default role
    private String userName;  // User's first name
    private int userId;  // User's ID

    public ConnexionDialog(Frame owner) {
        super(owner, "Connexion", true);
        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);
        add(inputPanel, BorderLayout.CENTER);

        // Login button
        loginButton = new JButton("Connexion");
        loginButton.addActionListener(e -> attemptLogin(emailField.getText(), new String(passwordField.getPassword())));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void attemptLogin(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT utilisateurs.id, utilisateurs.prenom, roles.role_name, utilisateurs.mot_de_passe FROM utilisateurs JOIN roles ON utilisateurs.role_id = roles.role_id WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("mot_de_passe");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    successfulLogin = true;
                    userId = rs.getInt("id");
                    userName = rs.getString("prenom");
                    userRole = rs.getString("role_name");
                    dispose(); // Close the dialog after successful login
                } else {
                    JOptionPane.showMessageDialog(this, "Identifiants incorrects", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                }
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

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return emailField.getText();
    }
}