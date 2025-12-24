package services.listelecture;

import modele.playlist.Playlist;
import modele.playlist.PlaylistEntry;
import modele.playlist.PlaylistFormat;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writer de playlist pour le format M3U8.
 * Génère un fichier texte compatible avec les lecteurs audio standards.
 * @version 1.0
 */
public class M3U8Writer extends AbstractPlaylistWriter {

    /**
     * Retourne le format de playlist pris en charge par ce writer.
     *
     * @return format M3U8
     */
    @Override
    public PlaylistFormat obtenirFormat() {
        return PlaylistFormat.M3U8;
    }

    /**
     * Écrit l’en-tête du fichier M3U8
     *
     * @param writer flux d’écriture
     * @param playlist playlist concernée
     * @throws IOException en cas d’erreur d’E/S
     */
    @Override
    protected void ecrireEnTete(BufferedWriter writer, Playlist playlist) throws IOException {
        writer.write("#EXTM3U");
        writer.newLine();
    }

    /**
     * Écrit une entrée de playlist au format M3U8.
     *
     * @param writer flux d’écriture
     * @param entry entrée de playlist
     * @throws IOException en cas d’erreur d’E/S
     */
    @Override
    protected void ecrireElement(BufferedWriter writer, PlaylistEntry entry) throws IOException {

        double dureeSec = entry.obtenirDureeSecondes();
        int dureeInt = (dureeSec > 0) ? (int) Math.round(dureeSec) : -1;

        String titre = nettoyer(entry.obtenirTitreAffichage());
        if (titre == null || titre.isBlank()) {
            titre = "Piste sans titre";
        }

        String chemin = entry.obtenirEmplacement();
        if (chemin == null) {
            chemin = "";
        }

        writer.write("#EXTINF:");
        writer.write(Integer.toString(dureeInt));
        writer.write(",");
        writer.write(titre);
        writer.newLine();

        writer.write(chemin);
        writer.newLine();
    }

    /**
     * Écrit le pied du fichier M3U8.
     * Aucun contenu supplémentaire n’est requis pour ce format.
     *
     * @param writer flux d’écriture
     * @param playlist playlist concernée
     * @throws IOException en cas d’erreur d’Entree et de sortie
     */
    @Override
    protected void ecrirePied(BufferedWriter writer, Playlist playlist) throws IOException {
        // Aucun pied spécifique pour le format M3U8
    }
}
