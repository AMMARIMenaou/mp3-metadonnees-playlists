package services.listelecture;

import modele.playlist.Playlist;
import modele.playlist.PlaylistEntry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Classe abstraite définissant le squelette d’écriture d’une playlist sur disque.
 * @version 1.0
 */
public abstract class AbstractPlaylistWriter implements PlaylistWriter {

    /**
     * Écrit une playlist sur disque en suivant les étapes génériques d’export.
     *
     * @param playlist playlist à écrire
     * @param out chemin du fichier de sortie
     * @throws PlaylistWriteException en cas d’erreur d’écriture
     */
    @Override
    public void ecrire(Playlist playlist, Path out) throws PlaylistWriteException {
        if (playlist == null) {
            throw new IllegalArgumentException("La playlist ne doit pas être nulle.");
        }
        if (out == null) {
            throw new IllegalArgumentException("Le chemin de sortie ne doit pas être nul.");
        }

        try (BufferedWriter writer =
                     Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {

            ecrireEnTete(writer, playlist);

            for (PlaylistEntry entree : playlist.obtenirElements()) {
                ecrireElement(writer, entree);
            }

            ecrirePied(writer, playlist);

            writer.flush();

        } catch (IOException e) {
            throw new PlaylistWriteException(
                    "Erreur lors de l'écriture de la playlist : " + out, e
            );
        }
    }

    /**
     * Nettoie une chaîne de caractères pour un usage sûr dans un fichier texte.
     *
     * @param texte chaîne brute
     * @return chaîne nettoyée
     */
    public String nettoyer(String texte) {
        if (texte == null) {
            return "";
        }
        return texte
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Écrit l’en-tête du fichier de playlist.
     *
     * @param writer flux d’écriture
     * @param playlist playlist concernée
     * @throws IOException en cas d’erreur d’E/S
     */
    protected abstract void ecrireEnTete(BufferedWriter writer, Playlist playlist) throws IOException;

    /**
     * Écrit un élément de playlist.
     *
     * @param writer flux d’écriture
     * @param entry entrée de playlist
     * @throws IOException en cas d’erreur d’E/S
     */
    protected abstract void ecrireElement(BufferedWriter writer, PlaylistEntry entry) throws IOException;

    /**
     * Écrit le pied du fichier de playlist.
     *
     * @param writer flux d’écriture
     * @param playlist playlist concernée
     * @throws IOException en cas d’erreur d’E/S
     */
    protected abstract void ecrirePied(BufferedWriter writer, Playlist playlist) throws IOException;
}
