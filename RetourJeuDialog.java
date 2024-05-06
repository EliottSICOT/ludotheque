package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RetourJeuDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1974562319998244617L;
	private JComboBox<String> comboBoxJeuxEmpruntes; // Supposons que cela contient les titres des jeux empruntés

    public RetourJeuDialog(JFrame parent) {
        super(parent, "Retourner un Jeu", true);
        setLayout(new GridLayout(2, 2, 10, 10));

        comboBoxJeuxEmpruntes = new JComboBox<>();
        // Ici, tu devrais remplir le comboBox avec les jeux empruntés par l'utilisateur
        add(new JLabel("Jeu emprunté :"));
        add(comboBoxJeuxEmpruntes);

        JButton retournerButton = new JButton("Retourner");
        retournerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour marquer le jeu comme retourné dans la base de données
            }
        });
        add(retournerButton);

        setSize(400, 150);
        setLocationRelativeTo(parent);
    }
}

