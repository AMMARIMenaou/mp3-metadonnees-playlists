package services.listelecture;

/**
 * Exception levée en cas de problème lors de l'écriture d'une playlist
 * sur le système de fichiers.
 */
public class PlaylistWriteException extends Exception {

  public PlaylistWriteException(String message) {
    super(message);
  }

  public PlaylistWriteException(String message, Throwable cause) {
    super(message, cause);
  }
}
