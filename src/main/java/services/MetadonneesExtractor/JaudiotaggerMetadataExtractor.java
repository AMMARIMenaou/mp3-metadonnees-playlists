package services.MetadonneesExtractor;
import modele.metadonnees.ID3TagSet;
import modele.metadonnees.Metadata;

import java.nio.file.Path;

/**
 * Implémentation concrète utilisant une librairie .
 */
public class JaudiotaggerMetadataExtractor implements MetadataExtractor {

    @Override
    public Metadata extraire(Path chemin) throws MetadataException {
        throw new UnsupportedOperationException("À implémenter");
    }

    @Override
    public ID3TagSet extraireTagsBruts(Path chemin) throws MetadataException {

        throw new UnsupportedOperationException("À implémenter");
    }
}
