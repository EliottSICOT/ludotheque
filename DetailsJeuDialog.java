package ui;

import javax.swing.*;
import java.awt.*;

public class DetailsJeuDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2167829862421792547L;

	public DetailsJeuDialog(JFrame parent, String titre, String categorie, String description) {
        super(parent, "Détails du Jeu", true);
        setLayout(new BorderLayout());

        JLabel labelTitre = new JLabel("Titre : " + titre);
        labelTitre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel labelCategorie = new JLabel("Catégorie : " + categorie);
        labelCategorie.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea textAreaDescription = new JTextArea(5, 20);
        textAreaDescription.setText(description);
        textAreaDescription.setWrapStyleWord(true);
        textAreaDescription.setLineWrap(true);
        textAreaDescription.setOpaque(false);
        textAreaDescription.setEditable(false);
        textAreaDescription.setFocusable(false);
        textAreaDescription.setBackground(UIManager.getColor("Label.background"));
        textAreaDescription.setFont(UIManager.getFont("Label.font"));
        textAreaDescription.setBorder(BorderFactory.createCompoundBorder(labelCategorie.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        add(labelTitre, BorderLayout.NORTH);
        add(labelCategorie, BorderLayout.CENTER);
        add(new JScrollPane(textAreaDescription), BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}

