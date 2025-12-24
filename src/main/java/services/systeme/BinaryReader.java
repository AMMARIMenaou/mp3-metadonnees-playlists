package services.systeme;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service chargé de lire un fichier binaire en mémoire.
 * Permet de récupérer tout le contenu d'un fichier sous forme de tableau d'octets.
 */
public class BinaryReader {

    /**
     * Lit l'intégralité d'un fichier et retourne son contenu en mémoire.
     *
     * @param path chemin du fichier (ne doit pas être null)
     * @return tableau d'octets contenant le contenu du fichier
     * @throws IllegalArgumentException si le chemin est null
     * @throws RuntimeException si la lecture échoue
     */
    public byte[] lireTousOctets(Path path) {


        if (path == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la lecture du fichier : " + path,
                    e
            );
        }
    }
}
