package services.mime;

import java.nio.file.Path;

/**
 * Interface définissant les opérations liées à la détection du type MIME.
 * Permet notamment de vérifier si un fichier est un MP3 et d'obtenir
 * son type MIME exact.
 * @version 1
 */
public interface MimeChecker {

    /**
     * Indique si le fichier spécifié est reconnu comme un fichier MP3.
     * @param chemin chemin du fichier à tester (ne doit pas être null)
     * @return true si le fichier correspond à un MP3, false sinon
     */
    boolean estMp3(Path chemin);
    boolean estAudio(Path chemin);
    /**
     * Détecte le type MIME du fichier spécifié.
     * @param chemin chemin du fichier à analyser
     * @return type MIME détecté (ex : "audio/mpeg"), ou null si inconnu
     */

    String detecterTypeMime(Path chemin);
}
