package interfacegui;

import modele.audio.AudioFile;
import modele.metadonnees.CoverImage;
import modele.metadonnees.Metadata;
import services.mime.MimeChecker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

/**
 * Panneau de droite : détails des métadonnées + pochette,
 * + journal (logs) et barre de statut, + contrôles lecture.
 *
 * @version 1.0
 */
public class PanneauDetailsEtJournal extends JPanel {

    // Labels pour  metadonnées ----
    public JLabel lblTitreVal, lblArtisteVal, lblAlbumVal, lblGenreVal, lblAnneeVal, lblPisteVal, lblDureeVal;
    public JLabel lblCheminVal, lblMimeVal;
    public JLabel lblBitrateVal, lblSampleRateVal, lblChannelsVal, lblFormatVal, lblEncodingVal;

    //  Pochette
    public JLabel lblCover;

    // Contrôles
    public JButton btnLire, btnStop, btnEffacer;
    public JTextArea areaLog;
    public JLabel lblStatus;

    /**
     * Constructeur.
     *
     * @param actionLire action lire
     * @param actionStop action stop
     * @param actionEffacer action effacer
     */
    public PanneauDetailsEtJournal(ActionListener actionLire,
                                   ActionListener actionStop,
                                   ActionListener actionEffacer) {
        super(new BorderLayout(8, 8));
        setBorder(new TitledBorder("Détails"));

        add(creerZoneDetails(), BorderLayout.CENTER);
        add(creerZoneJournalEtStatus(), BorderLayout.SOUTH);

        // actions
        btnLire.addActionListener(actionLire);
        btnStop.addActionListener(actionStop);
        btnEffacer.addActionListener(actionEffacer);
    }

    /** Créations  la zone centrale : métadonnées (scroll) + pochette + contrôles. */
    public JComponent creerZoneDetails() {
        JPanel container = new JPanel(new BorderLayout(8, 8));

        // panneau pour  metadonnées
        JPanel meta = new JPanel(new GridBagLayout());
        meta.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        lblTitreVal = addRow(meta, c, row++, "Titre :");
        lblArtisteVal = addRow(meta, c, row++, "Artiste :");
        lblAlbumVal = addRow(meta, c, row++, "Album :");
        lblGenreVal = addRow(meta, c, row++, "Genre :");
        lblAnneeVal = addRow(meta, c, row++, "Année :");
        lblPisteVal = addRow(meta, c, row++, "Piste :");
        lblDureeVal = addRow(meta, c, row++, "Durée (s) :");
        lblCheminVal = addRow(meta, c, row++, "Chemin :");
        lblMimeVal = addRow(meta, c, row++, "Type MIME :");
        lblBitrateVal = addRow(meta, c, row++, "Bitrate :");
        lblSampleRateVal = addRow(meta, c, row++, "Sample rate :");
        lblChannelsVal = addRow(meta, c, row++, "Canaux :");
        lblFormatVal = addRow(meta, c, row++, "Format :");
        lblEncodingVal = addRow(meta, c, row++, "Encodage :");

        JScrollPane metaScroll = new JScrollPane(meta);
        metaScroll.setBorder(null);
        metaScroll.getVerticalScrollBar().setUnitIncrement(16);

        // panel pour  pochette
        JPanel pochettePanel = new JPanel(new BorderLayout());
        pochettePanel.setBorder(new TitledBorder("Pochette"));
        pochettePanel.setPreferredSize(new Dimension(320, 320));

        lblCover = new JLabel("Aucune pochette", SwingConstants.CENTER);
        lblCover.setBorder(new EmptyBorder(8, 8, 8, 8));
        lblCover.setPreferredSize(new Dimension(280, 280));
        pochettePanel.add(lblCover, BorderLayout.CENTER);

        //  contrôles  le panel
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnLire = new JButton("Lire");
        btnStop = new JButton("Stop");
        btnEffacer = new JButton("Effacer");

        btnLire.setToolTipText("Lire le fichier sélectionné (le premier si sélection multiple)");
        btnStop.setToolTipText("Arrêter la lecture");
        btnEffacer.setToolTipText("Effacer la sélection et les détails affichés");

        ctrl.add(btnLire);
        ctrl.add(btnStop);
        ctrl.add(btnEffacer);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JSeparator(), BorderLayout.NORTH);
        bottom.add(ctrl, BorderLayout.CENTER);

        container.add(metaScroll, BorderLayout.CENTER);
        container.add(pochettePanel, BorderLayout.EAST);
        container.add(bottom, BorderLayout.SOUTH);

        return container;
    }

    /** Créations  une  zone bas pour  journal et  status. */
    public JComponent creerZoneJournalEtStatus() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Journal");
        title.setBorder(new EmptyBorder(0, 2, 0, 0));

        JButton btnClear = new JButton("Vider");
        btnClear.setToolTipText("Effacer le journal");
        btnClear.addActionListener(e -> areaLog.setText(""));

        header.add(title, BorderLayout.WEST);
        header.add(btnClear, BorderLayout.EAST);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(areaLog);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        JPanel statusBar = new JPanel(new BorderLayout());
        lblStatus = new JLabel("Prêt");
        lblStatus.setBorder(new EmptyBorder(4, 2, 4, 2));
        statusBar.add(lblStatus, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(100, 210));

        return panel;
    }


    public JLabel addRow(JPanel p, GridBagConstraints c, int row, String text) {
        c.gridy = row;

        c.gridx = 0;
        c.weightx = 0;
        JLabel lab = new JLabel(text);
        lab.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lab, c);

        c.gridx = 1;
        c.weightx = 1;
        JLabel v = new JLabel("-");
        v.setBorder(new EmptyBorder(2, 6, 2, 6));
        v.setOpaque(false);

        installerMenuCopie(v);
        v.setToolTipText(null);

        p.add(v, c);
        return v;
    }

    /** Menu clic-droit "Copier" sur les labels. */
    public void installerMenuCopie(JLabel label) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem copier = new JMenuItem("Copier");
        copier.addActionListener(e -> {
            String t = label.getText();
            if (t == null) return;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(t), null);
            log("Copié : " + t);
        });
        menu.add(copier);

        label.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(label, e.getX(), e.getY());
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(label, e.getX(), e.getY());
            }
        });
    }

    /** Affiche les métadonnées (et met à jour chemin + mime si possible). */
    public void afficherMetadonnees(Metadata m, AudioFile fichier, Path chemin, MimeChecker mimeChecker) {
        if (m == null) {
            effacerDetails();
            return;
        }

        setLabelText(lblTitreVal, texteOuTiret(m.getTitre()));
        setLabelText(lblArtisteVal, texteOuTiret(m.getArtiste()));
        setLabelText(lblAlbumVal, texteOuTiret(m.getAlbum()));
        setLabelText(lblGenreVal, texteOuTiret(m.getGenre()));
        setLabelText(lblAnneeVal, m.getAnnee() > 0 ? String.valueOf(m.getAnnee()) : "-");
        setLabelText(lblPisteVal, m.getNumeroDePiste() > 0 ? String.valueOf(m.getNumeroDePiste()) : "-");
        setLabelText(lblDureeVal, m.getDureeSeconde() > 0 ? String.valueOf(m.getDureeSeconde()) : "-");

        if (chemin != null) {
            setLabelText(lblCheminVal, chemin.toString());
            if (mimeChecker != null) {
                try {
                    setLabelText(lblMimeVal, mimeChecker.detecterTypeMime(chemin));
                } catch (Exception ex) {
                    setLabelText(lblMimeVal, "-");
                }
            }
        }

        setLabelText(lblBitrateVal, m.getBitrateKbps() > 0 ? m.getBitrateKbps() + " kb/s" : "-");
        setLabelText(lblSampleRateVal, m.getSampleRateHz() > 0 ? m.getSampleRateHz() + " Hz" : "-");
        setLabelText(lblChannelsVal, texteOuTiret(m.getChannels()));
        setLabelText(lblFormatVal, texteOuTiret(m.getFormat()));
        setLabelText(lblEncodingVal, texteOuTiret(m.getEncodingType()));

        // Pochette
        try {
            if (m.possedePochette() && m.getPochette() != null && !m.getPochette().estVide()) {
                CoverImage cover = m.getPochette();
                ImageIcon icon = new ImageIcon(cover.getDonnees());
                Image img = icon.getImage();
                Image scaled = scaleToFit(img, 280, 280);
                lblCover.setIcon(new ImageIcon(scaled));
                lblCover.setText("");
            } else {
                lblCover.setIcon(null);
                lblCover.setText("Aucune pochette");
            }
        } catch (Exception e) {
            lblCover.setIcon(null);
            lblCover.setText("Aucune pochette");
        }
    }

    /** Efface les champs de détail. */
    public void effacerDetails() {
        setLabelText(lblTitreVal, "-");
        setLabelText(lblArtisteVal, "-");
        setLabelText(lblAlbumVal, "-");
        setLabelText(lblGenreVal, "-");
        setLabelText(lblAnneeVal, "-");
        setLabelText(lblPisteVal, "-");
        setLabelText(lblDureeVal, "-");
        setLabelText(lblCheminVal, "-");
        setLabelText(lblMimeVal, "-");

        setLabelText(lblBitrateVal, "-");
        setLabelText(lblSampleRateVal, "-");
        setLabelText(lblChannelsVal, "-");
        setLabelText(lblFormatVal, "-");
        setLabelText(lblEncodingVal, "-");

        lblCover.setIcon(null);
        lblCover.setText("Aucune pochette");
    }

    /** Redimensionne une image en conservant le ratio. */
    public Image scaleToFit(Image img, int maxW, int maxH) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return img;

        float r = Math.min((float) maxW / w, (float) maxH / h);
        int nw = Math.max(1, Math.round(w * r));
        int nh = Math.max(1, Math.round(h * r));
        return img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
    }

    /** Retourne "-" si chaîne vide. */
    public String texteOuTiret(String s) {
        if (s == null) return "-";
        s = s.trim();
        return s.isEmpty() ? "-" : s;
    }

    /** Met à jour un label + tooltip. */
    public void setLabelText(JLabel label, String text) {
        label.setText(text);
        label.setToolTipText(text == null || "-".equals(text) ? null : text);
    }

    /** Ajoute une ligne dans le journal. */
    public void log(String m) {
        areaLog.append(m + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
        lblStatus.setText(m);
    }

    /** Met à jour le statut. */
    public void setStatus(String status) {
        lblStatus.setText(status);
    }

    /** Active/désactive le bouton Lire. */
    public void setLireEnabled(boolean enabled) {
        btnLire.setEnabled(enabled);
    }

    /** Active/désactive le bouton Stop. */
    public void setStopEnabled(boolean enabled) {
        btnStop.setEnabled(enabled);
    }
}
