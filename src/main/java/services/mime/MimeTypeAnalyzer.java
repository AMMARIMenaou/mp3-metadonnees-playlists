package services.mime;

import services.systeme.MimeTypeDetector;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Analyseur de type MIME basé sur un détecteur technique (MimeTypeDetector).
 * Permet également de déterminer si un fichier correspond à un MP3 / audio supporté.
 * @version 1
 */
public class MimeTypeAnalyzer implements MimeChecker {

    /** Détecteur technique du type MIME */
    private final MimeTypeDetector detecteur;

    /**
     * Construit un analyseur MIME.
     *
     * @param detecteur détecteur technique utilisé pour déterminer le type MIME
     * @throws IllegalArgumentException si le détecteur est null
     */
    public MimeTypeAnalyzer(MimeTypeDetector detecteur) {
        if (detecteur == null) {
            throw new IllegalArgumentException("Le détecteur MIME ne doit pas être nul.");
        }
        this.detecteur = detecteur;
    }
    @Override
    public boolean estAudio(Path chemin) {
        String mime =detecterTypeMime(chemin);
        if (mime == null) {
            return false;

        }
        return mime.startsWith("audio/");
    }

    /**
     * Vérifie si le fichier indiqué correspond à un MP3 / audio supporté.
     *
     * @param chemin chemin du fichier à analyser
     * @return true si le fichier est reconnu comme un MP3 ou un format audio supporté, false sinon
     */
    @Override
    public boolean estMp3(Path chemin) {
        if (chemin == null) {
            return false;
        }

        // Vérification via NIO : le fichier doit exister et être un vrai fichier
        if (!Files.exists(chemin) || !Files.isRegularFile(chemin)) {
            return false;
        }

        //Vérification via le type MIME détecté
        String typeMime = detecterTypeMime(chemin);
        if (estMimeAudioSupporte(typeMime)) {
            return true;
        }

        //Fallback via l'extension si le type MIME ne permet pas de conclure
        String nom = chemin.getFileName() != null
                ? chemin.getFileName().toString().toLowerCase()
                : "";

        // Ici on reste strict : si ton prof veut "que" MP3, laisse seulement .mp3
        return nom.endsWith(".mp3");
    }

    /**
     * Détecte le type MIME du fichier, avec un fallback par extension si besoin.
     *
     * @param chemin chemin du fichier
     * @return type MIME détecté ou null si indéterminé
     * @throws IllegalArgumentException si le chemin est null
     */
    @Override
    public String detecterTypeMime(Path chemin) {
        if (chemin == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }

        String mime = detecteur.detecterTypeMime(chemin);

        // Fallback si le détecteur ne renvoie rien
        if (mime == null) {
            String nom = chemin.toString().toLowerCase();
            if (nom.endsWith(".mp3")) {
                mime = "audio/mpeg";
            } else if (nom.endsWith(".flac")) {
                mime = "audio/flac";
            } else if (nom.endsWith(".wav")) {
                mime = "audio/wav";
            }
        }

        return mime;
    }

    /**
     * Indique si un type MIME correspond à un format audio supporté par l'application.
     *
     * @param mime type MIME (peut être null)
     * @return true si supporté, false sinon
     */
    private boolean estMimeAudioSupporte(String mime) {
        if (mime == null) {
            return false;
        }

        mime = mime.toLowerCase();

        return mime.equals("audio/mpeg")
                || mime.equals("audio/mp3")
                || mime.equals("audio/flac")
                || mime.equals("audio/x-flac")
                || mime.equals("audio/wav")
                || mime.equals("audio/x-wav");

    }
}
