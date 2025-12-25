package interfacegui;

import modele.playlist.PlaylistFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panneau (barre d'actions) en haut de la fenêtre :
 *
 * Ouvrir fichier
 * Ouvrir dossier
 * Choix format playlist
 * Exporter tout / Exporter sélection
 *
 * @version 1.0
 */
public class PanneauBarreActions extends JToolBar {

    public JButton btnOuvrirFichier;
    public JButton btnOuvrirDossier;

    public JComboBox<PlaylistFormat> comboFormat;

    public JButton btnExporterTout;
    public JButton btnExporterSelection;

    public JProgressBar progressBar;

    /**
     * Constructeur.
     *
     * @param ouvrirFichier action ouvrir fichier
     * @param ouvrirDossier action ouvrir dossier
     * @param exporterTout action export playlist par défaut
     * @param exporterSelection action export playlist personnalisée
     */
    public PanneauBarreActions(ActionListener ouvrirFichier,
                               ActionListener ouvrirDossier,
                               ActionListener exporterTout,
                               ActionListener exporterSelection) {
        super();
        setFloatable(false);
        setBorder(new EmptyBorder(4, 4, 4, 4));

        btnOuvrirFichier = new JButton("Ouvrir fichier");
        btnOuvrirDossier = new JButton("Ouvrir dossier");

        btnOuvrirFichier.setToolTipText("Charger un fichier audio et afficher ses métadonnées");
        btnOuvrirDossier.setToolTipText("Scanner un dossier (récursif) et lister les fichiers audio");

        btnOuvrirFichier.addActionListener(ouvrirFichier);
        btnOuvrirDossier.addActionListener(ouvrirDossier);

        add(btnOuvrirFichier);
        add(btnOuvrirDossier);
        addSeparator(new Dimension(16, 0));

        add(new JLabel("Format : "));
        comboFormat = new JComboBox<>(PlaylistFormat.values());
        comboFormat.setToolTipText("Format d’export de la playlist");
        add(comboFormat);

        btnExporterTout = new JButton("Exporter tout");
        btnExporterTout.setToolTipText("Générer une playlist avec tous les fichiers du dossier chargé");
        btnExporterTout.addActionListener(exporterTout);
        add(btnExporterTout);

        btnExporterSelection = new JButton("Exporter sélection");
        btnExporterSelection.setToolTipText("Générer une playlist personnalisée avec les fichiers sélectionnés");
        btnExporterSelection.addActionListener(exporterSelection);
        add(btnExporterSelection);

        add(Box.createHorizontalGlue());

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(180, 18));
        add(progressBar);
    }

    /** @return le format sélectionné. */
    public PlaylistFormat getFormatSelectionne() {
        return (PlaylistFormat) comboFormat.getSelectedItem();
    }

    public void setBusy(boolean busy) {
        btnOuvrirFichier.setEnabled(!busy);
        btnOuvrirDossier.setEnabled(!busy);
        comboFormat.setEnabled(!busy);

        progressBar.setVisible(busy);
        progressBar.setIndeterminate(busy);
    }

    /** Active/désactive "Exporter tout". */
    public void setExportToutEnabled(boolean enabled) {
        btnExporterTout.setEnabled(enabled);
    }

    /** Active/désactive "Exporter sélection". */
    public void setExportSelectionEnabled(boolean enabled) {
        btnExporterSelection.setEnabled(enabled);
    }
}
