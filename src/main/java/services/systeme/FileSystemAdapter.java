package services.systeme;
import services.systeme.BinaryReader;
import services.systeme.FileSystemPort;
import services.systeme.FileUtils;
import services.systeme.PathUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation concrète de FileSystemPort utilisant java.nio.file.
 * S'appuie sur les services système (FileUtils, BinaryReader, PathUtils).
 */
public class FileSystemAdapter implements FileSystemPort {

    private FileUtils fileUtils;
    private BinaryReader binaryReader;
    private PathUtils pathUtils;

    /**
     * Construit un adaptateur du système de fichiers en injectant
     * les services nécessaires.
     */
    public FileSystemAdapter() {
        this.fileUtils = new FileUtils();
        this.binaryReader = new BinaryReader();
        this.pathUtils = new PathUtils();
    }

    @Override
    public boolean existe(Path path) {
        return fileUtils.existe(path);
    }

    @Override
    public boolean estFichier(Path path) {
        return fileUtils.estFichier(path);
    }

    @Override
    public boolean estRepertoire(Path path) {
        return fileUtils.estRepertoire(path);
    }

    @Override
    public byte[] lireTousOctets(Path path) {
        return binaryReader.lireTousOctets(path);
    }

    @Override
    public long taille(Path path) {
        return fileUtils.taille(path);
    }

    @Override
    public List<Path> listerRepertoire(Path path) {

        if (path == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }

        if (!fileUtils.estRepertoire(path)) {
            throw new IllegalArgumentException("Le chemin n'est pas un répertoire : " + path);
        }

        List<Path> resultat = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path enfant : stream) {
                resultat.add(enfant);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la liste du répertoire : " + path,
                    e
            );
        }

        return resultat;
    }
}
