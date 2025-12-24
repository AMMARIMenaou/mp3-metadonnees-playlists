package interfacecli;

import causage.GestionBibliotheque;
import causage.GestionMetadata;
import causage.GestionPlaylist;
import interfacegui.ApplicationGUI;
import modele.audio.AudioFile;
import modele.metadonnees.Metadata;
import modele.playlist.PlaylistFormat;
import services.MetadonneesExtractor.ExternalId3LibraryAdapter;
import services.MetadonneesExtractor.LibraryMetadataExtractor;
import services.MetadonneesExtractor.MetadataExtractor;
import services.listelecture.FabriqueEcriturePlaylist;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Interface en ligne de commande de l’application.
 * Interprète les arguments fournis et déclenche les cas d’usage correspondants.
 *
 * @author Ammari
 * @version 1.0
 */
public class ApplicationCLI {

    private  GestionBibliotheque gestionBibliotheque;
    private  GestionMetadata gestionMetadata;
    private  GestionPlaylist gestionPlaylist;
    private  MimeChecker mimeChecker;

    /**
     * Initialise l’application CLI et instancie l’ensemble des dépendances.
     */
    public ApplicationCLI() {
        FileSystemPort fs = new FileSystemAdapter();

        MimeTypeDetector mimeDetector = new MimeTypeDetector();
        this.mimeChecker = new MimeTypeAnalyzer(mimeDetector);

        FileFilter filtre = new Mp3FileFilter(mimeChecker, new FileUtils());
        DirectoryScanner scanner = new RecursiveDirectoryScanner(filtre, fs);

        ExternalId3LibraryAdapter ext = new ExternalId3LibraryAdapter();
        MetadataExtractor extractor = new LibraryMetadataExtractor(mimeChecker, ext);

        FabriqueEcriturePlaylist fabriquePlaylist = new FabriqueEcriturePlaylist();

        this.gestionBibliotheque = new GestionBibliotheque(scanner, extractor);
        this.gestionMetadata = new GestionMetadata(extractor);
        this.gestionPlaylist = new GestionPlaylist(fabriquePlaylist);
    }

    /**
     * Point d’entrée principal de l’application console.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        new ApplicationCLI().run(args);
    }

    /**
     * Analyse les arguments et exécute le mode demandé.
     *
     * @param args arguments fournis par l’utilisateur
     */
    public void run(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("Paramètres manquants. Tapez -h (ou --help) pour obtenir de l'aide.");
            afficherAide();
            return;
        }

        if (contains(args, "-h") || contains(args, "--help")) {
            afficherAide();
            return;
        }

        if (contains(args, "--gui")) {
            SwingUtilities.invokeLater(() -> {
                ApplicationGUI f = new ApplicationGUI();
                f.setVisible(true);
            });
            return;
        }

        Path fichier = null;
        Path dossier = null;
        Path sortie = null;
        PlaylistFormat format = PlaylistFormat.M3U8;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {

                case "-f":
                case "--fichier":
                    if (i + 1 >= args.length) {
                        afficherAide();
                        return;
                    }
                    fichier = Paths.get(args[++i]);
                    break;

                case "-d":
                case "--dossier":
                    if (i + 1 >= args.length) {
                        afficherAide();
                        return;
                    }
                    dossier = Paths.get(args[++i]);
                    break;

                case "-o":
                case "--sortie":
                    if (i + 1 >= args.length) {
                        afficherAide();
                        return;
                    }
                    sortie = Paths.get(args[++i]);
                    break;

                case "--xspf":
                    format = PlaylistFormat.XSPF;
                    break;

                case "--jspf":
                    format = PlaylistFormat.JSPF;
                    break;

                case "--m3u8":
                    format = PlaylistFormat.M3U8;
                    break;

                default:
                    afficherAide();
                    return;
            }
        }

        try {
            if (fichier != null) {
                analyserFichier(fichier);
                return;
            }

            if (dossier != null) {
                List<AudioFile> fichiers = gestionBibliotheque.importerDossier(dossier);

                if (sortie == null) {
                    listerEtAnalyserDossier(dossier, fichiers);
                    return;
                }

                PlaylistFormat fmtFinal = detecterFormat(sortie, format);
                genererPlaylist(fichiers, dossier, sortie, fmtFinal);
                return;
            }

            afficherAide();

        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
        }
    }

    /**
     * Analyse un fichier audio unique et affiche ses métadonnées.
     *
     * @param fichier chemin du fichier audio
     * @throws Exception en cas d’erreur d’analyse
     */
    public void analyserFichier(Path fichier) throws Exception {
        if (fichier == null) {
            throw new Exception("Fichier nul.");
        }
        if (!Files.exists(fichier)) {
            throw new Exception("Fichier introuvable : " + fichier);
        }

        Path abs = fichier.toAbsolutePath();
        System.out.println("Analyse : " + abs);

        Metadata m = gestionMetadata.lireMetadata(fichier);
        afficherMetadata(m, abs);
    }

    /**
     * Liste et analyse tous les fichiers audio d’un dossier.
     *
     * @param dossier répertoire analysé
     * @param fichiers fichiers audio trouvés
     * @throws Exception en cas d’erreur
     */
    public void listerEtAnalyserDossier(Path dossier, List<AudioFile> fichiers) throws Exception {
        if (dossier == null) throw new Exception("Dossier nul.");

        int total = (fichiers == null) ? 0 : fichiers.size();

        System.out.println("Scan : " + dossier.toAbsolutePath());
        System.out.println("Fichiers MP3 trouvés : " + total);

        if (total == 0) return;

        int i = 1;
        for (AudioFile af : fichiers) {
            Path p = af.obtenirChemin().toAbsolutePath();
            System.out.println("\nAnalyse (" + i + "/" + total + ") : " + p);

            try {
                Metadata m = af.obtenirMetadonnees();
                afficherMetadata(m, p);
            } catch (Exception ex) {
                System.out.println("[ERREUR] Impossible de lire les métadonnées : " + ex.getMessage());
            }
            i++;
        }
    }

    /**
     * Génère une playlist à partir d’un ensemble de fichiers audio.
     *
     * @param fichiers fichiers audio
     * @param dossier dossier source
     * @param sortie chemin de sortie
     * @param format format de playlist
     * @throws Exception en cas d’erreur
     */
    public void genererPlaylist(List<AudioFile> fichiers, Path dossier, Path sortie, PlaylistFormat format)
            throws Exception {

        if (fichiers == null || fichiers.isEmpty()) {
            throw new Exception("Aucun fichier audio trouvé.");
        }

        System.out.println("Scan : " + dossier.toAbsolutePath());
        System.out.println("Fichiers trouvés : " + fichiers.size());
        System.out.println("Format playlist : " + format);
        System.out.println("Sortie : " + sortie.toAbsolutePath());

        gestionPlaylist.genererPlaylist(fichiers, format, sortie);

        System.out.println("Playlist générée : " + sortie.toAbsolutePath());
    }

    /**
     * Détermine le format de playlist à partir de l’extension du fichier.
     *
     * @param sortie chemin de sortie
     * @param defaut format par défaut
     * @return format détecté
     */
    public PlaylistFormat detecterFormat(Path sortie, PlaylistFormat defaut) {
        String s = sortie.toString().toLowerCase();
        if (s.endsWith(".xspf")) return PlaylistFormat.XSPF;
        if (s.endsWith(".jspf")) return PlaylistFormat.JSPF;
        if (s.endsWith(".m3u8")) return PlaylistFormat.M3U8;
        return defaut;
    }

    /**
     * Affiche les métadonnées d’un fichier audio.
     *
     * @param m métadonnées
     * @param fichierAbsolu chemin absolu du fichier
     */
    public void afficherMetadata(Metadata m, Path fichierAbsolu) {
        if (m == null) {
            System.out.println("=== METADATA ===");
            System.out.println("Aucune métadonnée.");
            return;
        }

        System.out.println("=== METADATA ===");
        System.out.println("Titre      : " + value(m.getTitre()));
        System.out.println("Artiste    : " + value(m.getArtiste()));
        System.out.println("Album      : " + value(m.getAlbum()));
        System.out.println("Genre      : " + value(m.getGenre()));
        System.out.println("Année      : " + (m.getAnnee() > 0 ? m.getAnnee() : "-"));
        System.out.println("Piste      : " + (m.getNumeroDePiste() > 0 ? m.getNumeroDePiste() : "-"));
        System.out.println("Durée      : " + (m.getDureeSeconde() > 0 ? (m.getDureeSeconde() + " s") : "-"));
        System.out.println("Chemin     : " + fichierAbsolu);

        try {
            String mime = mimeChecker.detecterTypeMime(fichierAbsolu);
            System.out.println("MIME       : " + mime);
        } catch (Exception ignored) {
            System.out.println("MIME       : -");
        }

        System.out.println("Bitrate    : " + (m.getBitrateKbps() > 0 ? m.getBitrateKbps() + " kb/s" : "-"));
        System.out.println("SampleRate : " + (m.getSampleRateHz() > 0 ? m.getSampleRateHz() + " Hz" : "-"));
        System.out.println("Canaux     : " + value(m.getChannels()));
        System.out.println("Format     : " + value(m.getFormat()));
        System.out.println("Encodage   : " + value(m.getEncodingType()));

        if (m.possedePochette() && m.getPochette() != null && !m.getPochette().estVide()) {
            System.out.println("Pochette   : oui (" + m.getPochette().getDonnees().length + " octets)");
        } else {
            System.out.println("Pochette   : non");
        }
    }

    /**
     * Normalise une chaîne pour l’affichage console.
     *
     * @param s chaîne brute
     * @return valeur formatée
     */
    public String value(String s) {
        return (s == null || s.isBlank()) ? "-" : s.trim();
    }

    /**
     * Affiche l’aide et les modes d’utilisation de l’application.
     */
    public void afficherAide() {
        System.out.println("""
Modes console :
  -h | --help                 : afficher l’aide
  -d <dossier>                : scanner récursivement un dossier, lister ET analyser tous les fichiers audio
  -f <fichier>                : analyser un fichier audio et afficher ses métadonnées
  -d <dossier> -o <playlist>  : générer une playlist à partir des fichiers trouvés dans le dossier
  --xspf | --jspf | --m3u8    : choisir le format de playlist (par défaut : M3U8)

Mode graphique :
  --gui                       : lancer l’interface graphique
""");
    }

    /**
     * Vérifie la présence d’un argument dans la liste fournie.
     *
     * @param arr tableau d’arguments
     * @param v valeur recherchée
     * @return vrai si présent
     */
    public boolean contains(String[] arr, String v) {
        for (String s : arr) {
            if (s.equalsIgnoreCase(v)) return true;
        }
        return false;
    }
}
