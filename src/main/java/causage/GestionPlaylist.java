package causage;

import modele.audio.AudioFile;
import modele.playlist.Playlist;
import modele.playlist.PlaylistEntry;
import modele.playlist.PlaylistFormat;
import services.listelecture.FabriqueEcriturePlaylist;
import services.listelecture.PlaylistWriteException;
import services.listelecture.PlaylistWriter;

import java.nio.file.Path;
import java.util.List;

/**
 * Cas d’usage de génération de playlists audio.
 * Construit une playlist et la persiste sur disque selon un format donné.
 *
 * @version 1.0
 */
public class GestionPlaylist {

    /**
     * Fabrique fournissant le writer adapté au format de playlist.
     */
    private final FabriqueEcriturePlaylist writerFactory;

    /**
     * Initialise le gestionnaire avec une fabrique de writers.
     *
     * @param writerFactory fabrique de writers (non nulle)
     * @throws IllegalArgumentException si la fabrique est nulle
     */
    public GestionPlaylist(FabriqueEcriturePlaylist writerFactory) {
        if (writerFactory == null) {
            throw new IllegalArgumentException("La factory de writer ne doit pas être nulle.");
        }
        this.writerFactory = writerFactory;
    }

    /**
     * Génère un fichier de playlist à partir d’une liste de fichiers audio.
     *
     * @param fichiers liste des fichiers audio
     * @param format format de playlist
     * @param out chemin du fichier de sortie
     * @throws PlaylistWriteException en cas d’erreur d’écriture
     */
    public void genererPlaylist(List<AudioFile> fichiers,
                                PlaylistFormat format,
                                Path out) throws PlaylistWriteException {

        if (fichiers == null || fichiers.isEmpty()) {
            throw new IllegalArgumentException("La liste de fichiers ne doit pas être vide.");
        }
        if (format == null) {
            throw new IllegalArgumentException("Le format de playlist ne doit pas être nul.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Le chemin de sortie ne doit pas être nul.");
        }

        Playlist playlist = new Playlist("Playlist générée");

        int indice = 1;
        for (AudioFile fichier : fichiers) {
            if (fichier == null) continue;
            PlaylistEntry entry = new PlaylistEntry(fichier, indice++);
            playlist.ajouterElement(entry);
        }

        PlaylistWriter writer = writerFactory.pourFormat(format);
        writer.ecrire(playlist, out);
    }
}
