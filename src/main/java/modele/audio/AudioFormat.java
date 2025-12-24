package modele.audio;



/**
 * cette class Représente les formats audio que l'application est capable d'identifier
 * et de traiter. Chaque valeur de cette énumération correspond à un type
 * de fichier audio bien défini.
 * Les formats actuellement supportés sont :
 * MP3  : fichiers MPEG compressés ;
 * WAV  : fichiers PCM non compressés ;
 * FLAC : fichiers audio compressés sans perte ;
 * INCONNU : utilisé lorsqu'un format ne peut pas être déterminé ou n'est
 *  pas géré par l'application.
 */

public enum AudioFormat {
    MP3,
    WAV,
    FLAC,
    INCONNU
}
