package services.listelecture;
import modele.playlist.Playlist;
import modele.playlist.PlaylistFormat;
import java.nio.file.Path;
public interface PlaylistWriter {
    /**
     * Interface définissant un service chargé de sérialiser une playlist
     * et de l’enregistrer sur le disque dans un format spécifique
     * sous les form suivant XSPF, JSPF ou M3U8
     *
     * Chaque implémentation de cette interface connaît un format particulier
     * et se charge de convertir une {@link Playlist} en sa représentation
     * correspondante avant de l’écrire dans un fichier.
     * @param playlist playlist à exporter (ne doit pas être nul)
     * @throws PlaylistWriteException si une erreur survient lors de l’écriture
     */
    void ecrire(Playlist playlist, Path out) throws PlaylistWriteException;

    /**
     * Indique le format de playlist pris en charge par cette implémentation.
     *
     * @return format supporté
     */
    PlaylistFormat obtenirFormat();
}
