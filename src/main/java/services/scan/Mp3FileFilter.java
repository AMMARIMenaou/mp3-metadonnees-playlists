package services.scan;

import  services.mime.MimeChecker;
import  services.systeme.FileUtils;

import java.nio.file.Path;

/**
 * Filtre permettant de déterminer si un chemin représente un fichier MP3 valide.
 *
 * Ce filtre s'appuie sur deux composants :
 *  {@link FileUtils} pour vérifier l’existence et la nature du chemin,
 * pour vérifier le type MIME ou l’extension.
 *
 * Utilisé lors du scan d’un répertoire pour isoler uniquement
 * les fichiers MP3 exploitables.
 */
public class Mp3FileFilter implements FileFilter {
    private MimeChecker mimeChecker;
    private FileUtils fileUtils;
    /**
     * @param mimeChecker service de détection MIME (ne doit pas être null)
     * @param fileUtils utilitaire système pour tester les chemins (ne doit pas être null)
     * @throws IllegalArgumentException si l’un des paramètres est null
     */
    public Mp3FileFilter(MimeChecker mimeChecker, FileUtils fileUtils) {
        if (mimeChecker == null) {
            throw new IllegalArgumentException("Le MimeChecker ne doit pas être nul.");
        }
        if (fileUtils == null) {
            throw new IllegalArgumentException("Le FileUtils ne doit pas être nul.");
        }

        this.mimeChecker = mimeChecker;
        this.fileUtils = fileUtils;
    }

    /**
     * Indique si le chemin fourni est un fichier MP3 valide.
     *
     * Conditions d’acceptation ,
     * le chemin n’est pas null ,
     * le fichier existe ,
     * le chemin correspond à un fichier régulier ,
     * le fichier est reconnu comme MP3 par {@link MimeChecker}.
     *
     * @param path chemin à analyser
     * @return true si le chemin correspond à un MP3 valide, false sinon
     */
    @Override
    public boolean accepter(Path path) {

        if (path == null) {
            return false;
        }

        if (!fileUtils.existe(path)) {
            return false;
        }

        if (!fileUtils.estFichier(path)) {
            return false;
        }

        return mimeChecker.estMp3(path);
    }
}