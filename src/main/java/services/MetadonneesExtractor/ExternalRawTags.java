package services.MetadonneesExtractor;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

/**
 * Conteneur regroupant les objets "bruts" fournis par la librairie externe
 * jaudiotagger lors de la lecture d’un fichier audio.
 * Ce type sert d’abstraction pour éviter que le reste du programme

 */
public class ExternalRawTags {

    private AudioFile audioFile;
    private Tag tag;
    private AudioHeader audioHeader;

    /**
     * Construit un conteneur regroupant toutes les données jaudiotagger.
     *
     * @param audioFile   objet représentant le fichier audio brut
     * @param tag         métadonnées textuelles extraites
     * @param audioHeader informations techniques (durée, bitrate, etc.)
     */
    public ExternalRawTags(AudioFile audioFile, Tag tag, AudioHeader audioHeader) {
        this.audioFile = audioFile;
        this.tag = tag;
        this.audioHeader = audioHeader;
    }

    /** Retourne l'objet AudioFile brut fourni par jaudiotagger. */
    public AudioFile getAudioFile() {
        return audioFile;
    }

    /** Retourne le tag contenant les métadonnées textuelles. */
    public Tag getTag() {
        return tag;
    }

    /** Retourne l’en-tête audio contenant les informations techniques. */
    public AudioHeader getAudioHeader() {
        return audioHeader;
    }
}
