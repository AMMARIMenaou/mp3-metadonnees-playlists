package interfacegui;

import causage.GestionBibliotheque;
import causage.GestionMetadata;
import causage.GestionPlaylist;
import modele.audio.AudioFile;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.List;

/**
 * Fenêtre principale de l'application "Audio Explorer".
 *Rôle :
 *Initialiser les services (scan, extraction métadonnées, export playlists)
 *Assembler la vue : barre d'actions + liste + détails/journal
 * Gérer les actions utilisateur (ouvrir fichier/dossier, exporter, lecture)
 *
 * @version 1.0
 */
public class FenetreAudioExplorer extends JFrame {

    public GestionBibliotheque gestionBibliotheque;
    public GestionMetadata gestionMetadata;
    public GestionPlaylist gestionPlaylists;
    public MimeChecker mimeChecker;
    public LecteurAudio lecteurAudio;

    public Path dossierCourant;
    public List<AudioFile> fichiersCourants;

    public PanneauBarreActions panneauBarreActions;
    public PanneauListeFichiers panneauListeFichiers;
    public PanneauDetailsEtJournal panneauDetailsEtJournal;

    /**
     * Structure interne de regroupement des services.
     */
    public static class Services {
        public GestionBibliotheque gestionBibliotheque;
        public GestionMetadata gestionMetadata;
        public GestionPlaylist gestionPlaylists;
        public MimeChecker mimeChecker;
    }

    /**
     * Constructeur : initialise services .
     */
    public FenetreAudioExplorer() {
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

    /**
     * Instancie les services techniques + cas d'usages.
     *
     * @return services prêts à l'emploi
     */
    public Services initialiserServices() {
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

    /**
     * Initialise l'interface graphique .
     */
    public void initialiserUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.font", UIManager.getFont("Label.font").deriveFont(13f));
            UIManager.put("Button.font", UIManager.getFont("Button.font").deriveFont(13f));
            UIManager.put("ComboBox.font", UIManager.getFont("ComboBox.font").deriveFont(13f));
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(1150, 720));
        panneauBarreActions = new PanneauBarreActions(
                this::onOuvrirFichier,
                this::onOuvrirDossier,
                this::onExporterPlaylist,
                this::onExporterPlaylistSelection
        );

        panneauListeFichiers = new PanneauListeFichiers();
        panneauDetailsEtJournal = new PanneauDetailsEtJournal(
                this::onLireSelection,
                e -> onStopLecture(),
                this::onEffacerSelection
        );

        panneauListeFichiers.installerListenerSelection(sel -> {
            if (sel != null && !sel.isEmpty()) {
                afficherDetailsAudioFile(sel.get(0)); // afficher détails du 1er sélectionné
            } else {
                panneauDetailsEtJournal.effacerDetails();
            }
            majEtatBoutons();
        });

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(panneauBarreActions, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panneauListeFichiers,
                panneauDetailsEtJournal
        );
        split.setResizeWeight(0.38);
        split.setOneTouchExpandable(true);

        root.add(split, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }


    /**
     *   ACTIONS UTILISATEUR
     * */


    /**
     * Ouvre un fichier audio et affiche ses métadonnées.
     */
    public void onOuvrirFichier(ActionEvent e) {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path fichier = ch.getSelectedFile().toPath();
        setBusy(true, "Lecture métadonnées...");

        try {
            Metadata m = gestionMetadata.lireMetadata(fichier);
            panneauDetailsEtJournal.afficherMetadonnees(m, null, fichier, mimeChecker);

            panneauDetailsEtJournal.log("Fichier ouvert : " + fichier);
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            setBusy(false, "Prêt");
            majEtatBoutons();
        }
    }

    /**
     * Ouvre un dossier et scanne récursivement (SwingWorker).
     */
    public void onOuvrirDossier(ActionEvent e) {
        JFileChooser ch = new JFileChooser();
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (ch.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        dossierCourant = ch.getSelectedFile().toPath();

        SwingWorker<List<AudioFile>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AudioFile> doInBackground() throws Exception {
                return gestionBibliotheque.importerDossier(dossierCourant);
            }

            @Override
            protected void done() {
                try {
                    fichiersCourants = get();
                    panneauListeFichiers.remplirListe(fichiersCourants);

                    if (fichiersCourants != null && !fichiersCourants.isEmpty()) {
                        panneauListeFichiers.selectionnerIndex(0);
                    } else {
                        panneauDetailsEtJournal.effacerDetails();
                    }

                    panneauDetailsEtJournal.log("Dossier importé : " + dossierCourant + " ("
                            + (fichiersCourants == null ? 0 : fichiersCourants.size()) + " fichier(s))");
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

    /**
     * Export "playlist par défaut" : tous les fichiers chargés.
     */
    public void onExporterPlaylist(ActionEvent e) {
        if (fichiersCourants == null || fichiersCourants.isEmpty()) {
            afficherErreur("Aucun fichier chargé.");
            return;
        }

        JFileChooser ch = new JFileChooser();
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path out = ch.getSelectedFile().toPath();
        PlaylistFormat format = panneauBarreActions.getFormatSelectionne();

        setBusy(true, "Génération playlist (tout)...");
        try {
            gestionPlaylists.genererPlaylist(fichiersCourants, format, out);
            JOptionPane.showMessageDialog(this, "Playlist générée.");
            panneauDetailsEtJournal.log("Playlist (tout) générée : " + out + " (" + format + ")");
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            setBusy(false, "Prêt");
            majEtatBoutons();
        }
    }

    /**
     * Export "playlist personnalisée" : uniquement la sélection dans la liste.
     */
    public void onExporterPlaylistSelection(ActionEvent e) {
        List<AudioFile> selection = panneauListeFichiers.getSelection();
        if (selection == null || selection.isEmpty()) {
            afficherErreur("Sélection vide : sélectionne des morceaux dans la liste.");
            return;
        }

        JFileChooser ch = new JFileChooser();
        if (ch.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path out = ch.getSelectedFile().toPath();
        PlaylistFormat format = panneauBarreActions.getFormatSelectionne();

        setBusy(true, "Génération playlist (sélection)...");
        try {
            gestionPlaylists.genererPlaylist(selection, format, out);
            JOptionPane.showMessageDialog(this, "Playlist personnalisée générée.");
            panneauDetailsEtJournal.log("Playlist (sélection) générée : " + out + " (" + format
                    + ", " + selection.size() + " piste(s))");
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            setBusy(false, "Prêt");
            majEtatBoutons();
        }
    }

    /**
     * Lecture : lit le premier fichier sélectionné.
     */
    public void onLireSelection(ActionEvent e) {
        List<AudioFile> sel = panneauListeFichiers.getSelection();
        if (sel == null || sel.isEmpty()) return;

        AudioFile af = sel.get(0);
        try {
            lecteurAudio.lire(af.obtenirChemin());
            panneauDetailsEtJournal.log("Lecture : " + af.obtenirChemin());
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }

    /**
     * Stop lecture.
     */
    public void onStopLecture() {
        try {
            if (lecteurAudio.estEnLecture()) {
                lecteurAudio.arreter();
                panneauDetailsEtJournal.log("Lecture stoppée.");
            }
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }

    /**
     * Efface sélection + détails.
     */
    public void onEffacerSelection(ActionEvent e) {
        panneauListeFichiers.effacerSelection();
        panneauDetailsEtJournal.effacerDetails();
        panneauDetailsEtJournal.log("Détails effacés, prêt à choisir un nouveau fichier.");
        majEtatBoutons();
    }
    /**
     * Affiche les détails d'un AudioFile.
     */
    public void afficherDetailsAudioFile(AudioFile af) {
        try {
            panneauDetailsEtJournal.afficherMetadonnees(af.obtenirMetadonnees(), af, af.obtenirChemin(), mimeChecker);
        } catch (Exception ex) {
            afficherErreur(ex.getMessage());
        } finally {
            majEtatBoutons();
        }
    }


    /**
     * Met l'application en état "occupé" (désactivation contrôles + barre de progression).
     */
    public void setBusy(boolean busy, String status) {
        panneauBarreActions.setBusy(busy);
        panneauDetailsEtJournal.setStatus(status);

        boolean hasFiles = (fichiersCourants != null && !fichiersCourants.isEmpty());
        boolean hasSelection = (panneauListeFichiers.getSelection() != null && !panneauListeFichiers.getSelection().isEmpty());

        panneauBarreActions.setExportToutEnabled(!busy && hasFiles);
        panneauBarreActions.setExportSelectionEnabled(!busy && hasSelection);
    }

    /**
     * Met à jour l'état des boutons.
     */
    public void majEtatBoutons() {
        boolean hasFiles = (fichiersCourants != null && !fichiersCourants.isEmpty());
        boolean hasSelection = (panneauListeFichiers.getSelection() != null && !panneauListeFichiers.getSelection().isEmpty());

        panneauBarreActions.setExportToutEnabled(hasFiles);
        panneauBarreActions.setExportSelectionEnabled(hasSelection);

        panneauDetailsEtJournal.setLireEnabled(hasSelection);

        try {
            panneauDetailsEtJournal.setStopEnabled(lecteurAudio.estEnLecture());
        } catch (Exception ex) {
            panneauDetailsEtJournal.setStopEnabled(false);
        }
    }

    /**
     * Affiche une erreur à l'utilisateur.
     */
    public void afficherErreur(String message) {
        panneauDetailsEtJournal.log("[ERREUR] " + message);
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }



    }

