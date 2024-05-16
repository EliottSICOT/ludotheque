package ui;

import javax.swing.*;
import java.awt.*;

public class GestionAvisDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldJeu;
    private JTextArea textAreaAvis;
    private JButton btnSubmit;

    public GestionAvisDialog(JFrame parent) {
        super(parent, "Gestion des Avis", true);
        setupUI();
    }

    private void setupUI() {
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel panelCenter = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCenter.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCenter.add(new JLabel("Jeu:"));
        textFieldJeu = new JTextField();
        panelCenter.add(textFieldJeu);
        panelCenter.add(new JLabel("Avis:"));
        textAreaAvis = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textAreaAvis);
        panelCenter.add(scrollPane);

        add(panelCenter, BorderLayout.CENTER);

        btnSubmit = new JButton("Soumettre");
        btnSubmit.addActionListener(e -> submitAvis());
        JPanel panelSouth = new JPanel();
        panelSouth.add(btnSubmit);
        add(panelSouth, BorderLayout.SOUTH);
    }

    private void submitAvis() {
        String jeu = textFieldJeu.getText().trim();
        String avis = textAreaAvis.getText().trim();

        if (jeu.isEmpty() || avis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Logique pour soumettre l'avis
    }
}
