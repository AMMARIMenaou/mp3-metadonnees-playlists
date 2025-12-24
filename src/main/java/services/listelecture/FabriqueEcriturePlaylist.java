package services.listelecture;

import modele.playlist.PlaylistFormat;

/**
 * Fabrique de writers de playlists en fonction du format demandé.
 */
public class FabriqueEcriturePlaylist {

    /**
     * Retourne un writer adapté au format demandé.
     *
     * @param format format de playlist (ne doit pas être {@code null})
     * @return implémentation de {@link PlaylistWriter} correspondant
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    public PlaylistWriter pourFormat(PlaylistFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Le format ne doit pas être nul.");
        }

        switch (format) {
            case XSPF:
                return new XSPFWriter();
            case JSPF:
                return new JSPFWriter();
            case M3U8:
                return new M3U8Writer();
            default:
                throw new IllegalArgumentException("Format de playlist non supporté : " + format);
        }
    }
}

