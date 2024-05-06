package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GestionAvisDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4496153437488667873L;
	private JComboBox<String> comboBoxJeux; // Supposons que cela contient les titres des jeux
    private JTextArea avisTextArea;
    private JSpinner noteSpinner;

    public GestionAvisDialog(JFrame parent) {
        super(parent, "Laisser un avis", true);
        setLayout(new GridLayout(4, 2, 10, 10));

        comboBoxJeux = new JComboBox<>();
        // Remplir le comboBox avec les titres des jeux
        add(new JLabel("Jeu :"));
        add(comboBoxJeux);

        avisTextArea = new JTextArea(5, 20);
        add(new JLabel("Votre avis :"));
        add(new JScrollPane(avisTextArea));

        noteSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 10, 1));
        add(new JLabel("Note :"));
        add(noteSpinner);

        JButton soumettreAvisButton = new JButton("Soumettre");
        soumettreAvisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logique pour enregistrer l'avis dans la base de données
            }
        });
        add(soumettreAvisButton);

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
