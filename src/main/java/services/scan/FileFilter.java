package services.scan;
import java.nio.file.Path;

/**
 * Filtre générique de fichiers basé sur leur chemin.
 * Permet de décider si un fichier doit être retenu ou ignoré.
 */
public interface FileFilter {

    /**
     * Indique si le chemin passé en paramètre est accepté par ce filtre.
     *
     * @param path chemin à tester
     * @return {@code true} si le chemin est accepté, {@code false} sinon
     */
    boolean accepter(Path path);
}
