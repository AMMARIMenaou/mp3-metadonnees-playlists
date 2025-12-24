package services.MetadonneesExtractor;

import modele.metadonnees.CoverImage;
import modele.metadonnees.Id3Tag;
import modele.metadonnees.ID3TagSet;
import modele.metadonnees.Metadata;
import modele.metadonnees.MetadataBuilder;

import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.images.Artwork;

import services.mime.MimeChecker;

import java.nio.file.Path;
import java.util.List;

/**
 * Extracteur de métadonnées basé sur une bibliothèque externe ID3.
 * Convertit les tags bruts et informations techniques en objets métier exploitables.
 *
 * @author Ammari
 * @version 1.0
 */
public class LibraryMetadataExtractor implements MetadataExtractor {

    private  MimeChecker mimeChecker;
    private  ExternalId3LibraryAdapter adaptateur;

    /**
     * Construit un extracteur de métadonnées à partir d’un vérificateur MIME
     * et d’un adaptateur vers la bibliothèque ID3 externe.
     *
     * @param mimeChecker service de vérification du type MIME
     * @param adaptateur adaptateur de lecture des tags ID3 bruts
     */
    public LibraryMetadataExtractor(MimeChecker mimeChecker,
                                    ExternalId3LibraryAdapter adaptateur) {
        if (mimeChecker == null) throw new IllegalArgumentException("MimeChecker nul");
        if (adaptateur == null) throw new IllegalArgumentException("Adaptateur nul");

        this.mimeChecker = mimeChecker;
        this.adaptateur = adaptateur;
    }

    /**
     * Extrait les métadonnées métier complètes d’un fichier audio.
     *
     * @param chemin chemin du fichier audio
     * @return métadonnées extraites
     * @throws MetadataException si le fichier n’est pas audio ou en cas d’erreur
     */
    @Override
    public Metadata extraire(Path chemin) throws MetadataException {
        if (chemin == null)
            throw new IllegalArgumentException("Chemin nul");

        if (!mimeChecker.estAudio(chemin))
            throw new MetadataException("Fichier non audio : " + chemin);

        ExternalRawTags raw = adaptateur.lireTagsBruts(chemin);
        Tag tag = raw.getTag();
        AudioHeader header = raw.getAudioHeader();

        MetadataBuilder builder = new MetadataBuilder();

        if (tag != null) {
            builder.avecTitre(lireChamp(tag, FieldKey.TITLE))
                    .avecArtiste(lireChamp(tag, FieldKey.ARTIST))
                    .avecAlbum(lireChamp(tag, FieldKey.ALBUM))
                    .avecGenre(lireChamp(tag, FieldKey.GENRE))
                    .avecAnnee(lireEntier(tag, FieldKey.YEAR))
                    .avecNumeroPiste(lireEntier(tag, FieldKey.TRACK));

            CoverImage pochette = extrairePochette(tag);
            if (pochette != null && !pochette.estVide()) {
                builder.avecPochette(pochette);
            }
        }

        if (header != null) {
            builder.avecDureeSecondes(header.getTrackLength());

            try { builder.avecSampleRate(header.getSampleRateAsNumber()); } catch (Exception ignored) {}
            try { builder.avecChannels(header.getChannels()); } catch (Exception ignored) {}
            try { builder.avecFormat(header.getFormat()); } catch (Exception ignored) {}
            try { builder.avecEncodingType(header.getEncodingType()); } catch (Exception ignored) {}
        }

        return builder.construire();
    }

    /**
     * Extrait l’ensemble des tags ID3 bruts d’un fichier audio.
     *
     * @param chemin chemin du fichier audio
     * @return ensemble de tags ID3
     * @throws MetadataException en cas d’erreur de lecture
     */
    @Override
    public ID3TagSet extraireTagsBruts(Path chemin) throws MetadataException {
        if (chemin == null)
            throw new IllegalArgumentException("Chemin nul");

        ExternalRawTags raw = adaptateur.lireTagsBruts(chemin);
        Tag tag = raw.getTag();
        ID3TagSet set = new ID3TagSet();

        if (tag == null) return set;

        for (FieldKey key : FieldKey.values()) {
            try {
                List<TagField> fields = tag.getFields(key);
                if (fields == null || fields.isEmpty()) continue;

                String val = tag.getFirst(key);
                if (val == null || val.trim().isEmpty()) continue;

                set.ajouter(new Id3Tag(key.name(), key.name(), val));

            } catch (Exception ignored) {}
        }
        return set;
    }

    /**
     * Lit un champ texte depuis un tag ID3.
     *
     * @param tag tag ID3
     * @param key clé du champ
     * @return valeur normalisée ou null
     */
    private String lireChamp(Tag tag, FieldKey key) {
        try {
            String val = tag.getFirst(key);
            if (val == null) return null;
            val = val.trim();
            return val.isEmpty() ? null : val;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Lit un champ numérique depuis un tag ID3.
     *
     * @param tag tag ID3
     * @param key clé du champ
     * @return valeur entière ou 0 si invalide
     */
    private int lireEntier(Tag tag, FieldKey key) {
        String txt = lireChamp(tag, key);
        if (txt == null) return 0;

        try {
            return Integer.parseInt(txt.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Extrait la pochette intégrée au fichier audio.
     *
     * @param tag tag ID3
     * @return image de pochette ou null
     */
    private CoverImage extrairePochette(Tag tag) {
        try {
            Artwork art = tag.getFirstArtwork();
            if (art == null) return null;

            byte[] data = art.getBinaryData();
            if (data == null || data.length == 0) return null;

            return new CoverImage(
                    data,
                    art.getMimeType(),
                    art.getWidth(),
                    art.getHeight()
            );
        } catch (Exception e) {
            return null;
        }
    }
}
