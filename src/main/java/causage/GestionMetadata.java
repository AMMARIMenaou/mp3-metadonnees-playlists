package causage;

import modele.metadonnees.ID3TagSet;
import modele.metadonnees.Metadata;
import services.MetadonneesExtractor.*;

import java.nio.file.Path;

/**
 * Cas d’usage de lecture des métadonnées d’un fichier audio.
 * Centralise l’accès aux métadonnées métier et aux tags bruts ID3.
 *
 * @version 1.0
 */
public class GestionMetadata {

    /**
     * Service d’extraction des métadonnées audio.
     */
    private MetadataExtractor extracteur;

    /**
     * Initialise le gestionnaire avec un extracteur de métadonnées.
     *
     * @param extracteur extracteur utilisé (non nul)
     * @throws IllegalArgumentException si l’extracteur est nul
     */
    public GestionMetadata(MetadataExtractor extracteur) {
        if (extracteur == null) {
            throw new IllegalArgumentException("L'extracteur de métadonnées ne doit pas être nul.");
        }
        this.extracteur = extracteur;
    }

    /**
     * Lit les métadonnées métier d’un fichier audio.
     *
     * @param fichier chemin du fichier audio
     * @return métadonnées extraites ou null si absentes
     */
    public Metadata lireMetadata(Path fichier) {
        if (fichier == null) {
            throw new IllegalArgumentException("Le fichier ne doit pas être nul.");
        }
        return extracteur.extraire(fichier);
    }

    /**
     * Lit les tags bruts ID3 d’un fichier audio.
     *
     * @param fichier chemin du fichier audio
     * @return ensemble des tags bruts ID3
     */
    public ID3TagSet lireTagsBruts(Path fichier) {
        if (fichier == null) {
            throw new IllegalArgumentException("Le fichier ne doit pas être nul.");
        }
        return extracteur.extraireTagsBruts(fichier);
    }
}
