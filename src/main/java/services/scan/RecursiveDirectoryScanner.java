package services.scan;

import services.systeme.FileSystemPort;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Implémentation de {@link DirectoryScanner} qui parcourt récursivement
 * les sous-répertoires en s'appuyant sur {@link FileSystemPort}.
 */
public class RecursiveDirectoryScanner implements DirectoryScanner {

    private  FileFilter filtre;
    private  FileSystemPort fileSystem;

    /**
     * Construit un scanner récursif de répertoires.
     *
     * @param filtre      filtre à appliquer sur les fichiers (ne doit pas être {@code null})
     * @param fileSystem  port d'accès au système de fichiers (ne doit pas être {@code null})
     */
    public RecursiveDirectoryScanner(FileFilter filtre, FileSystemPort fileSystem) {
        if (filtre == null) {
            throw new IllegalArgumentException("Le filtre ne doit pas être nul.");
        }
        if (fileSystem == null) {
            throw new IllegalArgumentException("Le FileSystemPort ne doit pas être nul.");
        }
        this.filtre = filtre;
        this.fileSystem = fileSystem;
    }

    /**
     * Utilise une pile (DFS) pour parcourir récursivement la hiérarchie
     * de répertoires à partir de {@code racine de Arborécence des fichier}.
     */
    @Override
    public List<Path> scanner(Path racine) {
        if (racine == null) {
            throw new IllegalArgumentException("La racine ne doit pas être nulle.");
        }
        if (!fileSystem.existe(racine)) {
            throw new IllegalArgumentException("La racine n'existe pas : " + racine);
        }

        if (!fileSystem.estRepertoire(racine)) {
            throw new IllegalArgumentException("La racine n'est pas un répertoire : " + racine);
        }

        List<Path> resultats = new ArrayList<>();
        Deque<Path> pile = new ArrayDeque<>();

        pile.push(racine);

        while (!pile.isEmpty()) {
            Path courant = pile.pop();

            List<Path> contenus = fileSystem.listerRepertoire(courant);
            for (Path enfant : contenus) {
                if (fileSystem.estRepertoire(enfant)) {
                    // On empile les sous-répertoires pour les parcourir
                    pile.push(enfant);
                } else if (fileSystem.estFichier(enfant)) {
                    // On applique le filtre sur les fichiers
                    if (filtre.accepter(enfant)) {
                        resultats.add(enfant);
                    }
                }
            }
        }

        return resultats;
    }
}
