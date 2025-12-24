package services.lecteur;
import java.nio.file.Path;

/**
 * Contrat générique pour un lecteur audio simple.
 * Gestion très minimale lire un fichier, arrêter, savoir si quelque chose est en lecture.
 */
public interface LecteurAudio {

    /**
     * Lance la lecture d'un fichier audio.
     * Si un autre fichier était en cours de lecture, il est d'abord arrêté.
     *
     * @param chemin chemin du fichier audio
     */
    void lire(Path chemin) throws Exception;

    /**
     * Arrête la lecture en cours (si existante).
     */
    void arreter();

    /**
     * Indique si un fichier est actuellement en cours de lecture.
     */
    boolean estEnLecture();
}
