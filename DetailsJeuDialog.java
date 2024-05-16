package ui;

import javax.swing.*;
import java.awt.*;

public class DetailsJeuDialog extends JDialog {
    private static final long serialVersionUID = 2167829862421792547L;
    private static final int MARGIN = 10;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    public DetailsJeuDialog(JFrame parent, String titre, String categorie, String description) {
        super(parent, "Détails du Jeu", true);
        setupUI(titre, categorie, description);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(parent);
    }

    private void setupUI(String titre, String categorie, String description) {
        setLayout(new BorderLayout());

        JLabel labelTitre = createLabel("Titre : " + titre);
        JLabel labelCategorie = createLabel("Catégorie : " + categorie);
        JTextArea textAreaDescription = createTextArea(description);

        add(labelTitre, BorderLayout.NORTH);
        add(labelCategorie, BorderLayout.CENTER);
        add(new JScrollPane(textAreaDescription), BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        return label;
    }

    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(5, 20);
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(UIManager.getColor("Label.background"));
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN),
            BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN)
        ));
        return textArea;
    }
}
