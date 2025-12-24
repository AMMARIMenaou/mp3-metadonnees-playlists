package services.systeme;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *  cette Classe elle sert permettant d’interagir avec le système de fichiers.
 * Les méthodes ne sont plus statiques : l'utilisation se fait via une instance.
 */
public class FileUtils {

    /**
     * Vérifie si un chemin existe dans le système de fichiers.
     * @param path chemin à vérifier
     * @return true si le chemin existe, false sinon
     */
    public  boolean existe(Path path) {
        if (path == null) {
            return false;
        }
        return Files.exists(path);
    }

    /**
     * Vérifie si un chemin correspond à un fichier régulier.
     *
     * @param path chemin à vérifier
     * @return true si c’est un fichier, false sinon
     */
    public  boolean estFichier(Path path) {
        if (path == null) {
            return false;
        }
        return Files.isRegularFile(path);
    }

    /**
     * Vérifie si un chemin correspond à un répertoire.
     *
     * @param path chemin à vérifier
     * @return true si c’est un répertoire, false sinon
     */
    public boolean estRepertoire(Path path) {
        if (path == null) {
            return false;
        }
        return Files.isDirectory(path);
    }

    /**
     * Retourne la taille d’un fichier en octets.
     *
     * @param path chemin du fichier
     * @return taille en octets
     * @throws IllegalArgumentException si le chemin est null
     * @throws RuntimeException si la lecture échoue
     */
    public long taille(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }

        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire la taille du fichier : " + path, e);
        }
    }
}
