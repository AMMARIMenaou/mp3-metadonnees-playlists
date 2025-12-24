package services.scan;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Cette interface définit un mécanisme de parcours récursif de l’arborescence
 * d’un dossier : chaque sous-répertoire est exploré à son tour, et seuls les
 * fichiers satisfaisant un filtre externe sont retenus.
 * Elle isole ainsi la logique de scan du système de fichiers afin de permettre
 * des implémentations interchangeables et testables.
 */

public interface DirectoryScanner {

    /**
     * Scanne récursivement le répertoire racine et retourne la liste
     * des fichiers acceptés par le filtre.
     *
     * @param repertoire répertoire racine à scanner
     * @return liste des chemins de fichiers acceptés (liste vide si aucun)
     * @throws IllegalArgumentException si {@code racine} est nul ou ne désigne pas un répertoire
     */
    List<Path> scanner(Path repertoire) throws IOException;
}
