package services.systeme;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * cette classe elle sert a  détecter le type MIME d'un fichier.
 * Utilise d'abord librairie Java, puis un fallback basé sur l'extension.
 */
public class MimeTypeDetector {

    /**
     * Détecte le type MIME d'un fichier.
     *
     * @param path chemin du fichier (ne doit pas être null)
     * @return type MIME comme : "audio/mpeg"  ou null si inconnu
     * @throws IllegalArgumentException si le chemin est null
     */
    public String detecterTypeMime(Path path) {

        if (path == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }


        try {
            String mime = Files.probeContentType(path);
            if (mime != null) {
                return mime;
            }
        } catch (IOException e) {

        }

        // Fallback pour la détection simple par extension
        String nom = path.getFileName() != null
                ? path.getFileName().toString().toLowerCase()
                : "";

        if (nom.endsWith(".mp3")) {
            return "audio/mpeg";
        }

        if (nom.endsWith(".flac")) {
            return "audio/flac";
        }

        if (nom.endsWith(".wav")) {
            return "audio/wav";
        }


        return null;
    }
}
