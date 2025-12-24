package modele.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe abstraite représentant la structure de base d'une playlist.
 *
 * Une playlist est définie par
 * un nom permettant de l’identifier
 * une collection d’entrées {@link PlaylistEntry} représentant les fichiers audio
 * un ensemble d'opérations fondamentales communes à toutes les playlists.
 * Les classes concrètes dérivées peuvent spécialiser ou étendre ce comportement
 * @version 2
 */
public abstract class AbstractPlaylist {

    /** Nom lisible de la playlist (peut être null). */
    protected String nom;

    /** Liste interne des entrées composant la playlist. */
    protected List<PlaylistEntry> elements;

    /**
     * Construit une playlist sans nom.
     * Le nom est initialisé à null.
     */
    protected AbstractPlaylist() {
        this(null);
    }

    /**
     * Construit une playlist avec un nom initial.
     *
     * @param nom nom de la playlist
     */
    public AbstractPlaylist(String nom) {
        this.nom = nom;
        this.elements = new ArrayList<>();
    }

    /**
     * Retourne le nom actuel de la playlist.
     *
     * @return nom de la playlist
     */
    public List<PlaylistEntry> obtenerElements() {
        return elements;
    }


    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de la playlist
     *
     * @param nom nouveau nom (peut être null)
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne la liste interne des entrées.
     * Cette liste est modifiable par les classes dérivées ou appelantes.
     *
     * @return liste des entrées
     */
    public List<PlaylistEntry> obtenerElements(int index) {
        return Collections.unmodifiableList(elements.subList(index, elements.size()));
    }

    /**
     * Ajoute une entrée à la playlist.
     *
     * @param e entrée à ajouter ; si null, l’opération est ignorée
     */
    public void ajouterElement(PlaylistEntry e) {
        if (e == null) {
            return;
        }
        elements.add(e);
    }

    /**
     * Retourne le nombre total d’entrées contenues dans la playlist.
     * @return taille de la playlist
     */
    public int taille() {
        return elements.size();
    }

    /**
     * Calcule la durée totale de lecture de la playlist en secondes.
     * La durée correspond à la somme des durées de toutes les entrées.
     *
     * @return durée totale en secondes
     */
    public double dureeTotalSecondes() {
        double totalSecondes = 0;
        for (PlaylistEntry e : elements) {
            totalSecondes += e.obtenirDureeSecondes();
        }
        return totalSecondes;
    }
}
