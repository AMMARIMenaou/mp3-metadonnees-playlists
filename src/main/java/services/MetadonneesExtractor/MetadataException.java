package services.MetadonneesExtractor;
/**
 * Exception spécifique personnaliser aux erreurs d'extraction de métadonnées.
 * Elle étend RuntimeException pour simplifier l'utilisation
 * les méthodes qui l'utilisent ne sont pas obligées de la déclarer.
 */
public class MetadataException extends RuntimeException {

  public MetadataException(String message) {
    super(message);
  }

  public MetadataException(String message, Throwable cause) {
    super(message, cause);
  }
}
