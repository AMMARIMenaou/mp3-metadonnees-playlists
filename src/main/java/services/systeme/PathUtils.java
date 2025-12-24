package services.systeme;
import java.nio.file.Path;
/**
 * Service utilitaire pour la manipulation des chemins de fichiers
 * Permet de convertir un chemin en URI et de résoudre des chemins relatifs
 */
public class PathUtils {

    /**
     * Convertit un chemin local en URI utilisable dans une playlist.
     *
     * @param path chemin à convertir
     * @return URI du fichier sous forme de chaîne, ou "" si path est null
     */
    public String enUriFichier(Path path) {
        if (path == null) {
            return "";
        }
        return path.toUri().toString();
    }

    /**
     * Résout un chemin relatif par rapport à une racine.
     *
     * @param racine  répertoire de base (ne doit pas être null)
     * @param relatif chemin relatif (peut être null ou vide)
     * @return chemin résultant
     */
    public Path resoudre(Path racine, String relatif) {

        if (racine == null) {
            throw new IllegalArgumentException("La racine ne doit pas être nulle.");
        }

        if (relatif == null || relatif.trim().isEmpty()) {
            return racine;
        }

        return racine.resolve(relatif);
    }
}
