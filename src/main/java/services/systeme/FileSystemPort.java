package services.systeme;
import java.nio.file.Path;
import java.util.List;

/**
 * Port d'accès au système de fichiers.
 * Permet d'abstraire les opérations de base sur les fichiers et répertoires.
 */
public interface FileSystemPort {

    /**
     * Indique si le chemin existe.
     * @param path chemin à tester
     * @return {@code true} si le chemin existe, {@code false} sinon
     */
    boolean existe(Path path);

    /**
     * Indique si le chemin désigne un fichier.
     * @param path chemin à tester
     * @return  true si le chemin désigne un fichier,false sinon
     */
    boolean estFichier(Path path);

    /**
     * Indique si le chemin désigne un répertoire.
     * @param path chemin à tester
     * @return  true si le chemin désigne un répertoire, sinon false
     */
    boolean estRepertoire(Path path);

    /**
     * Lit l'intégralité du contenu d'un fichier en mémoire.
     * @param path chemin du fichier
     * @return tableau d'octets représentant le contenu du fichier
     */
    byte[] lireTousOctets(Path path);

    /**
     * Liste les éléments directement contenus dans un répertoire.
     * @param path répertoire à lister
     * @return liste des chemins fils (liste vide si le répertoire est vide)
     */
    List<Path> listerRepertoire(Path path);

    /**
     * Retourne la taille du fichier en octets.
     * @param path chemin du fichier
     * @return taille en octets
     */
    long taille(Path path);
}
