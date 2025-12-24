package modele.audio;

import modele.metadonnees.Metadata;
import java.nio.file.Path;

/**
 * Interface définissant le contrat  pour représenter un fichier audio.
 * Toute classe qui implémente cette interface doit fournir un accès cohérent
 * et standardisé aux informations essentielles d’un fichier audio
 * chemin, nom, type MIME, métadonnées, durée et format.
 * @version 1
 */
public interface AudioFile {

    /**
     * Retourne le chemin absolu du fichier audio dans le système de fichiers.
     *
     * @return le chemin du fichier (jamais {@code null})
     */
    Path obtenirChemin();

    /**
     * Retourne uniquement le nom du fichier (sans son chemin).
     *
     * @return le nom du fichier, ou {@code null} si le chemin est indisponible
     */
    String obtenirNomFichier();

    /**
     * Retourne le type MIME détecté pour ce fichier audio.
     * @return le type MIME, ou {@code null} si la détection a échoué
     */
    String obtenirTypeMime();

    /**
     * Retourne les métadonnées extraites du fichier audio
     * @return les métadonnées, ou {@code null} si elles n'ont pas encore été chargées
     */
    Metadata obtenirMetadonnees();

    /**
     * Retourne la durée totale du fichier audio en secondes.
     *
     * @return la durée en secondes, ou {@code 0} si elle est inconnue
     */
    double obtenirDureeSecondes();

    /**
     * Retourne le format audio du fichier
     * @return le format audio (jamais {@code null})
     */
    AudioFormat obtenirFormat();
}
