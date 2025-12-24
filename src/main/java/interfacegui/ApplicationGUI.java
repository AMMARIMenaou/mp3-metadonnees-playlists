package interfacegui;

import causage.GestionBibliotheque;
import causage.GestionMetadata;
import causage.GestionPlaylist;
import modele.audio.AudioFile;
import modele.metadonnees.CoverImage;
import modele.metadonnees.Metadata;
import modele.playlist.PlaylistFormat;
import services.MetadonneesExtractor.ExternalId3LibraryAdapter;
import services.MetadonneesExtractor.LibraryMetadataExtractor;
import services.MetadonneesExtractor.MetadataExtractor;
import services.listelecture.FabriqueEcriturePlaylist;
import services.lecteur.LecteurAudio;
import services.lecteur.LecteurAudioJLayer;
import services.mime.MimeChecker;
import services.mime.MimeTypeAnalyzer;
import services.scan.DirectoryScanner;
import services.scan.FileFilter;
import services.scan.Mp3FileFilter;
import services.scan.RecursiveDirectoryScanner;
import services.systeme.FileSystemAdapter;
import services.systeme.FileSystemPort;
import services.systeme.FileUtils;
import services.systeme.MimeTypeDetector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;
/**
 * Interface graphique principale de l’application.
 * Permet l’exploration, l’analyse et la lecture de fichiers audio via une GUI Swing.
 *
 * @version 1.0
 */

public class  ApplicationGUI extends JFrame {

    private  GestionBibliotheque gestionBibliotheque;
    private  GestionMetadata gestionMetadata;
    private  GestionPlaylist gestionPlaylists;
    private  MimeChecker mimeChecker;
    private  LecteurAudio lecteurAudio;

    private Path dossierCourant;
    private List<AudioFile> fichiersCourants;

    private JButton btnOuvrirFichier;
    private JButton btnOuvrirDossier;
    private JButton btnExporterPlaylist;
    private JComboBox<PlaylistFormat> comboFormat;

    private DefaultListModel<AudioFile> listeModel;
    private JList<AudioFile> listeFichiers;

    private JLabel lblTitreVal;
    private JLabel lblArtisteVal;
    private JLabel lblAlbumVal;
    private JLabel lblGenreVal;
    private JLabel lblAnneeVal;
    private JLabel lblPisteVal;
    private JLabel lblDureeVal;
    private JLabel lblCheminVal;
    private JLabel lblMimeVal;
    private JLabel lblCover;

    private JLabel lblBitrateVal;
    private JLabel lblSampleRateVal;
    private JLabel lblChannelsVal;
    private JLabel lblFormatVal;
    private JLabel lblEncodingVal;

    private JButton btnLire;
    private JButton btnStop;
    private JButton btnEffacer;

    private JTextArea areaLog;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private static class Services {
        GestionBibliotheque gestionBibliotheque;
        GestionMetadata gestionMetadata;
        GestionPlaylist gestionPlaylists;
        MimeChecker mimeChecker;
    }

    public ApplicationGUI() {
        super("Audio Explorer");
        Services s = initialiserServices();
        this.gestionBibliotheque = s.gestionBibliotheque;
        this.gestionMetadata = s.gestionMetadata;
        this.gestionPlaylists = s.gestionPlaylists;
        this.mimeChecker = s.mimeChecker;
        this.lecteurAudio = new LecteurAudioJLayer();
        initialiserUI();
        majEtatBoutons();
    }

    private Services initialiserServices() {
        Services s = new Services();

        FileSystemPort fs = new FileSystemAdapter();
        MimeTypeDetector mimeDetector = new MimeTypeDetector();
        FileUtils fileUtils = new FileUtils();
        MimeChecker mimeChecker = new MimeTypeAnalyzer(mimeDetector);
        s.mimeChecker = mimeChecker;

        FileFilter filtreMp3 = new Mp3FileFilter(mimeChecker, fileUtils);
        DirectoryScanner scanner = new RecursiveDirectoryScanner(filtreMp3, fs);

        ExternalId3LibraryAdapter adaptateur = new ExternalId3LibraryAdapter();
        MetadataExtractor extracteur = new LibraryMetadataExtractor(mimeChecker, adaptateur);

        FabriqueEcriturePlaylist fabriquePlaylist = new FabriqueEcriturePlaylist();

        s.gestionBibliotheque = new GestionBibliotheque(scanner, extracteur);
        s.gestionMetadata = new GestionMetadata(extracteur);
        s.gestionPlaylists = new GestionPlaylist(fabriquePlaylist);

        return s;
    }

    private void initialiserUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Légères améliorations “globales” sans lib externe
            UIManager.put("Label.font", UIManager.getFont("Label.font").deriveFont(13f));
            UIManager.put("Button.font", UIManager.getFont("Button.font").deriveFont(13f));
            UIManager.put("ComboBox.font", UIManager.getFont("ComboBox.font").deriveFont(13f));
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(1150, 720));

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(creerBarreActions(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                creerPanelListeFichiers(),
                creerPanelMetadonnees()
        );
        split.setResizeWeight(0.38);
        split.setOneTouchExpandable(true);
        root.add(split, BorderLayout.CENTER);

        root.add(creerPanelLogEtStatus(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JComponent creerBarreActions() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(new EmptyBorder(4, 4, 4, 4));

        btnOuvrirFichier = new JButton("Ouvrir fichier");
        btnOuvrirDossier = new JButton("Ouvrir dossier");

        btnOuvrirFichier.setToolTipText("Charger un fichier audio et afficher ses métadonnées");
        btnOuvrirDossier.setToolTipText("Scanner un dossier (récursif) et lister les fichiers audio");

        btnOuvrirFichier.addActionListener(this::onOuvrirFichier);
        btnOuvrirDossier.addActionListener(this::onOuvrirDossier);

        toolbar.add(btnOuvrirFichier);
        toolbar.add(btnOuvrirDossier);
        toolbar.addSeparator(new Dimension(16, 0));

        toolbar.add(new JLabel("Format : "));
        comboFormat = new JComboBox<>(PlaylistFormat.values());
        comboFormat.setToolTipText("Format d’export de la playlist");
        toolbar.add(comboFormat);

        btnExporterPlaylist = new JButton("Exporter playlist");
        btnExporterPlaylist.setToolTipText("Générer une playlist avec les fichiers chargés");
        btnExporterPlaylist.addActionListener(this::onExporterPlaylist);
        toolbar.add(btnExporterPlaylist);

        toolbar.add(Box.createHorizontalGlue());

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(180, 18));
        toolbar.add(progressBar);

        return toolbar;
    }

    private JComponent creerPanelListeFichiers() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new TitledBorder("Fichiers audio"));

        listeModel = new DefaultListModel<>();
        listeFichiers = new JList<>(listeModel);
        listeFichiers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeFichiers.setVisibleRowCount(16);
        listeFichiers.setFixedCellHeight(46);

        // Rendu plus lisible : titre + artiste (si dispo)
        listeFichiers.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);

                if (value instanceof AudioFile af) {
                    String nom = af.obtenirNomFichier();
                    String titre = nom;
                    String artiste = "";

                    try {
                        Metadata m = af.obtenirMetadonnees();
                        if (m != null) {
                            if (m.getTitre() != null && !m.getTitre().isBlank()) titre = m.getTitre().trim();
                            if (m.getArtiste() != null && !m.getArtiste().isBlank()) artiste = m.getArtiste().trim();
                        }
                    } catch (Exception ignored) {}

                    String line1 = (index + 1) + ". " + escapeHtml(titre);
                    String line2 = artiste.isBlank() ? escapeHtml(nom) : escapeHtml(artiste);

                    setText("<html><div style='font-weight:600'>" + line1 + "</div>"
                            + "<div style='font-size:11px; opacity:0.85'>" + line2 + "</div></html>");
                }
                return this;
            }
        });

        listeFichiers.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                AudioFile sel = listeFichiers.getSelectedValue();
                if (sel != null) afficherDetailsAudioFile(sel);
                else effacerDetails();
                majEtatBoutons();
            }
        });

        // Double-clic => lire
        listeFichiers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    AudioFile af = listeFichiers.getSelectedValue();
                    if (af != null) onLireSelection(new ActionEvent(btnLire, ActionEvent.ACTION_PERFORMED, "dblclick"));
                }
            }
        });

        JScrollPane sp = new JScrollPane(listeFichiers);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JComponent creerPanelMetadonnees() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new TitledBorder("Détails"));

        // ---- Métadonnées (scrollable) ----
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

        // ---- Pochette à droite ----
        JPanel pochettePanel = new JPanel(new BorderLayout());
        pochettePanel.setBorder(new TitledBorder("Pochette"));
        pochettePanel.setPreferredSize(new Dimension(320, 320));

        lblCover = new JLabel("Aucune pochette", SwingConstants.CENTER);
        lblCover.setBorder(new EmptyBorder(8, 8, 8, 8));
        lblCover.setPreferredSize(new Dimension(280, 280));
        pochettePanel.add(lblCover, BorderLayout.CENTER);

        // ---- Contrôles ----
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnLire = new JButton("Lire");
        btnStop = new JButton("Stop");
        btnEffacer = new JButton("Effacer");

        btnLire.setToolTipText("Lire le fichier sélectionné");
        btnStop.setToolTipText("Arrêter la lecture");
        btnEffacer.setToolTipText("Effacer la sélection et les détails affichés");

        btnLire.addActionListener(this::onLireSelection);
        btnStop.addActionListener(e -> onStopLecture());
        btnEffacer.addActionListener(this::onEffacerSelection);

        ctrl.add(btnLire);
        ctrl.add(btnStop);
        ctrl.add(btnEffacer);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JSeparator(), BorderLayout.NORTH);
        bottom.add(ctrl, BorderLayout.CENTER);


        panel.add(metaScroll, BorderLayout.CENTER);
        panel.add(pochettePanel, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel addRow(JPanel p, GridBagConstraints c, int row, String text) {
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

        // Menu clic-droit pour copier
        installerMenuCopie(v);

        // Meilleure gestion des textes longs : tooltip auto
        v.setToolTipText(null);

        p.add(v, c);

        return v;
    }

    private void installerMenuCopie(JLabel label) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem copier = new JMenuItem("Copier");
        copier.addActionListener(e -> {
            String t = label.getText();
            if (t == null) return;
            String plain = t.startsWith("<html>") ? label.getToolTipText() : t;
            if (plain == null) plain = t;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(plain), null);
            log("Copié : " + plain);
        });
        menu.add(copier);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(label, e.getX(), e.getY());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) menu.show(label, e.getX(), e.getY());
            }
        });
    }

    private JComponent creerPanelLogEtStatus() {
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

    private void onOuvrirFichier(ActionEvent e) {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path fichier = ch.getSelectedFile().toPath();
        setBusy(true, "Lecture métadonnées...");
        try {
            Metadata m = gestionMetadata.lireMetadata(fichier);
            afficherMetadonnees(m, null);
            setLabelText(lblCheminVal, fichier.toString());
            setLabelText(lblMimeVal, mimeChecker.detecterTypeMime(fichier));
            log("Fichier ouvert : " + fichier);
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            setBusy(false, "Prêt");
            majEtatBoutons();
        }
    }

    private void onOuvrirDossier(ActionEvent e) {
        JFileChooser ch = new JFileChooser();
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (ch.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        dossierCourant = ch.getSelectedFile().toPath();

        // SwingWorker pour ne pas bloquer l’UI
        SwingWorker<List<AudioFile>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AudioFile> doInBackground() throws Exception {
                return gestionBibliotheque.importerDossier(dossierCourant);
            }

            @Override
            protected void done() {
                try {
                    fichiersCourants = get();
                    listeModel.clear();
                    if (fichiersCourants != null) fichiersCourants.forEach(listeModel::addElement);

                    if (fichiersCourants != null && !fichiersCourants.isEmpty()) {
                        listeFichiers.setSelectedIndex(0);
                    } else {
                        effacerDetails();
                    }

                    log("Dossier importé : " + dossierCourant + " (" +
                            (fichiersCourants == null ? 0 : fichiersCourants.size()) + " fichier(s))");
                } catch (Exception ex) {
                    afficherErreur(ex.getMessage());
                } finally {
                    setBusy(false, "Prêt");
                    majEtatBoutons();
                }
            }
        };

        setBusy(true, "Scan dossier...");
        worker.execute();
    }

    private void onExporterPlaylist(ActionEvent e) {
        if (fichiersCourants == null || fichiersCourants.isEmpty()) {
            afficherErreur("Aucun fichier chargé.");
            return;
        }

        JFileChooser ch = new JFileChooser();
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path out = ch.getSelectedFile().toPath();
        PlaylistFormat format = (PlaylistFormat) comboFormat.getSelectedItem();

        setBusy(true, "Génération playlist...");
        try {
            gestionPlaylists.genererPlaylist(fichiersCourants, format, out);
            JOptionPane.showMessageDialog(this, "Playlist générée.");
            log("Playlist générée : " + out + " (" + format + ")");
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            setBusy(false, "Prêt");
            majEtatBoutons();
        }
    }

    private void onLireSelection(ActionEvent e) {
        AudioFile af = listeFichiers.getSelectedValue();
        if (af == null) return;

        try {
            lecteurAudio.lire(af.obtenirChemin());
            log("Lecture : " + af.obtenirChemin());
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }

    private void onStopLecture() {
        try {
            if (lecteurAudio.estEnLecture()) {
                lecteurAudio.arreter();
                log("Lecture stoppée.");
            }
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }

    private void onEffacerSelection(ActionEvent e) {
        listeFichiers.clearSelection();
        effacerDetails();
        log("Détails effacés, prêt à choisir un nouveau fichier.");
        majEtatBoutons();
    }

    private void afficherDetailsAudioFile(AudioFile af) {
        try {
            afficherMetadonnees(af.obtenirMetadonnees(), af);
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }

    private void effacerDetails() {
        setLabelText(lblTitreVal, "-");
        setLabelText(lblArtisteVal, "-");
        setLabelText(lblAlbumVal, "-");
        setLabelText(lblGenreVal, "-");
        setLabelText(lblAnneeVal, "-");
        setLabelText(lblPisteVal, "-");
        setLabelText(lblDureeVal, "-");
        setLabelText(lblCheminVal, "-");
        setLabelText(lblMimeVal, "-");
        lblCover.setIcon(null);
        lblCover.setText("Aucune pochette");

        setLabelText(lblBitrateVal, "-");
        setLabelText(lblSampleRateVal, "-");
        setLabelText(lblChannelsVal, "-");
        setLabelText(lblFormatVal, "-");
        setLabelText(lblEncodingVal, "-");
    }

    private void afficherMetadonnees(Metadata m, AudioFile f) {
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

        if (f != null) {
            setLabelText(lblCheminVal, f.obtenirChemin().toString());
            setLabelText(lblMimeVal, mimeChecker.detecterTypeMime(f.obtenirChemin()));
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

    private Image scaleToFit(Image img, int maxW, int maxH) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) return img;

        float r = Math.min((float) maxW / w, (float) maxH / h);
        int nw = Math.max(1, Math.round(w * r));
        int nh = Math.max(1, Math.round(h * r));
        return img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
    }

    private String texteOuTiret(String s) {
        if (s == null) return "-";
        s = s.trim();
        return s.isEmpty() ? "-" : s;
    }

    private void setLabelText(JLabel label, String text) {
        label.setText(text);
        // Tooltip utile pour les champs longs (chemin, mime, etc.)
        label.setToolTipText(text == null || text.equals("-") ? null : text);
    }

    private void setBusy(boolean busy, String status) {
        btnOuvrirFichier.setEnabled(!busy);
        btnOuvrirDossier.setEnabled(!busy);
        btnExporterPlaylist.setEnabled(!busy && fichiersCourants != null && !fichiersCourants.isEmpty());
        comboFormat.setEnabled(!busy);

        progressBar.setVisible(busy);
        progressBar.setIndeterminate(busy);

        lblStatus.setText(status);
    }

    private void majEtatBoutons() {
        boolean hasSelection = (listeFichiers != null && listeFichiers.getSelectedValue() != null);
        boolean hasFiles = (fichiersCourants != null && !fichiersCourants.isEmpty());

        btnExporterPlaylist.setEnabled(hasFiles);
        btnLire.setEnabled(hasSelection);
        btnEffacer.setEnabled(true);

        try {
            btnStop.setEnabled(lecteurAudio.estEnLecture());
        } catch (Exception ex) {
            btnStop.setEnabled(false);
        }
    }

    private void log(String m) {
        areaLog.append(m + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
        lblStatus.setText(m);
    }

    private void afficherErreur(String m) {
        log("[ERREUR] " + m);
        JOptionPane.showMessageDialog(this, m, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) {

        java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
        root.setLevel(java.util.logging.Level.OFF);
        for (var h : root.getHandlers()) {
            h.setLevel(java.util.logging.Level.OFF);
        }

        SwingUtilities.invokeLater(() -> {
            ApplicationGUI f = new ApplicationGUI();
            f.setVisible(true);
        });
    }
}
