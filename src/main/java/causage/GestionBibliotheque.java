package causage;

import modele.audio.AudioFile;
import modele.audio.MP3File;
import services.MetadonneesExtractor.MetadataExtractor;
import services.scan.DirectoryScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cas d’usage d’importation d’une bibliothèque musicale.
 * Coordonne le scan d’un répertoire et le chargement des métadonnées audio.
 * @version 1.0
 */
public class GestionBibliotheque {

    /**
     * Service de scan récursif des répertoires.
     */
    private DirectoryScanner scanner;

    /**
     * Service d’extraction des métadonnées audio.
     */
    private MetadataExtractor extracteur;

    /**
     * Initialise le gestionnaire avec les services requis.
     *
     * @param scanner service de scan (non nul)
     * @param extracteur service d’extraction (non nul)
     * @throws IllegalArgumentException si un paramètre est nul
     */
    public GestionBibliotheque(DirectoryScanner scanner, MetadataExtractor extracteur) {
        if (scanner == null) {
            throw new IllegalArgumentException("Le scanner ne doit pas être nul.");
        }
        if (extracteur == null) {
            throw new IllegalArgumentException("L'extracteur de métadonnées ne doit pas être nul.");
        }
        this.scanner = scanner;
        this.extracteur = extracteur;
    }

    /**
     * Importe un répertoire de musique et charge les métadonnées des fichiers MP3.
     *
     * @param repertoire répertoire à analyser
     * @return liste des fichiers audio importés
     * @throws IOException en cas d’erreur d’entrée/sortie
     */
    public List<AudioFile> importerDossier(Path repertoire) throws IOException {
        if (repertoire == null) {
            throw new IllegalArgumentException("Le répertoire ne doit pas être nul.");
        }

        List<Path> chemins = scanner.scanner(repertoire);
        List<AudioFile> fichiers = new ArrayList<>();

        for (Path chemin : chemins) {
            MP3File mp3 = new MP3File(chemin);
            mp3.chargerMetadonnees(extracteur);
            fichiers.add(mp3);
        }

        return fichiers;
    }
}
