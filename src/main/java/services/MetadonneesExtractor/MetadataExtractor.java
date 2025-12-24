package services.MetadonneesExtractor;
import modele.metadonnees.ID3TagSet;
import modele.metadonnees.Metadata;

import java.nio.file.Path;

/**
 * Cette class  d'extraction des métadonnées à partir d'un fichier audio.
 * L'implémentation peut s'appuyer sur une librairie externe
 */
public interface MetadataExtractor {

    /**
     * Extrait les métadonnées  à partir d'un fichier audio.
     *
     * @param chemin chemin du fichier audio
     * @return métadonnées extraites
     * @throws MetadataException en cas d'erreur d'extraction
     */
    Metadata extraire(Path chemin) throws MetadataException;

    /**
     * Extrait un ensemble de tags bruts (ID3) à partir du fichier.
     *
     * @param chemin chemin du fichier audio
     * @return ensemble de tags bruts
     * @throws MetadataException en cas d'erreur d'extraction
     */
    ID3TagSet extraireTagsBruts(Path chemin) throws MetadataException;
}
