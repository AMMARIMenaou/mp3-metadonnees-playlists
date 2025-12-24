package services.MetadonneesExtractor;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.nio.file.Path;

/**
 * cette class c'est un Adaptateur permettant d'interagir avec la librairie externe jaudiotagger.
 * Cette classe encapsule la lecture d’un fichier audio et l’extraction
 * des objets bas niveau générés par la librairie (Tag, AudioHeader, AudioFile).
 *
 * Elle isole ainsi le reste du code des dépendances directes à jaudiotagger,
 * ce qui facilite la maintenance et l'évolution du système.
 *
 */
public class ExternalId3LibraryAdapter {

    /**
     * Lit un fichier audio à l'aide de jaudiotagger et renvoie un conteneur
     * regroupant les informations brutes extraites :
     * l'objet AudioFile jaudiotagger
     * les métadonnées textuelles (Tag)
     * l’en-tête audio (AudioHeader)
     *
     * @param chemin chemin du fichier audio à analyser
     * @return un objet {@link ExternalRawTags} contenant toutes les données brutes
     *
     * @throws IllegalArgumentException si le chemin est null
     * @throws MetadataException si jaudiotagger rencontre une erreur de lecture ou de parsing
     * @version 2
     */
    public ExternalRawTags lireTagsBruts(Path chemin) {
        if (chemin == null) {
            throw new IllegalArgumentException("Le chemin ne doit pas être nul.");
        }

        try {
            File fichier = chemin.toFile();
            AudioFile audioFile = AudioFileIO.read(fichier);
            Tag tag = audioFile.getTag();
            AudioHeader header = audioFile.getAudioHeader();

            return new ExternalRawTags(audioFile, tag, header);

        } catch (Exception e) {
            throw new MetadataException(
                    "Erreur lors de l'extraction des métadonnées brutes pour : " + chemin,
                    e
            );
        }
    }
}
