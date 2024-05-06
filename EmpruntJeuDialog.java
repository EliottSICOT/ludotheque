package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EmpruntJeuDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1037581564781367620L;
	private JComboBox<String> comboBoxJeux; // Supposons que cela contient les titres des jeux
    private JSpinner dateEmpruntSpinner;
    private JSpinner dateRetourSpinner;

    public EmpruntJeuDialog(JFrame parent) {
        super(parent, "Emprunter un Jeu", true);
        setLayout(new GridLayout(4, 2, 10, 10));

        comboBoxJeux = new JComboBox<>();
        // Ici, tu devrais remplir le comboBox avec les jeux disponibles
        add(new JLabel("Jeu :"));
        add(comboBoxJeux);

        dateEmpruntSpinner = new JSpinner(new SpinnerDateModel());
        add(new JLabel("Date d'emprunt :"));
        add(dateEmpruntSpinner);

        dateRetourSpinner = new JSpinner(new SpinnerDateModel());
        add(new JLabel("Date de retour prévue :"));
        add(dateRetourSpinner);

        JButton emprunterButton = new JButton("Emprunter");
        emprunterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour enregistrer l'emprunt dans la base de données
            }
        });
        add(emprunterButton);

        setSize(400, 200);
        setLocationRelativeTo(parent);
    }
}

