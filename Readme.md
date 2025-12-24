# MP3 Métadonnées & Playlists (CLI + GUI) — Java 21

Application Java permettant :
1) d’extraire les **métadonnées ID3** d’un fichier MP3,
2) de **scanner récursivement** un dossier en filtrant **uniquement les vrais MP3** (extension + type MIME),
3) de générer des playlists **XSPF (XML)**, **JSPF (JSON)** et **M3U8 (UTF-8)**,
   avec deux interfaces : **CLI (console)** et **GUI (graphique)**.

---

## Liens GitHub (à adapter)

Repository :
- https://github.com/<TON_USER>/<TON_REPO>

Releases (JARs téléchargeables) :
- https://github.com/<TON_USER>/<TON_REPO>/releases

Issues / suivi bugs :
- https://github.com/<TON_USER>/<TON_REPO>/issues

---

## Sommaire
- [Fonctionnalités](#fonctionnalités)
- [Bibliothèques externes utilisées](#bibliothèques-externes-utilisées)
- [Formats de sortie](#formats-de-sortie)
- [Pré-requis](#pré-requis)
- [Génération des JAR (CLI + GUI) — détaillé](#génération-des-jar-cli--gui--détaillé)

---

## Fonctionnalités

### A) Extraction métadonnées MP3 (ID3)
- Lecture des tags ID3 (selon bibliothèque de lecture utilisée) :
    - titre, artiste, album, numéro de piste, année, genre, durée (si dispo), etc.
- Gestion des cas réels :
    - tags manquants → valeurs vides/“Unknown” sans crash
    - fichiers illisibles → erreur contrôlée et message clair
- Mode fichier unique : analyse d’un MP3 précis (option `-f`)

### B) Scan récursif d’un dossier (réellement robuste)
- Parcours récursif via `Files.walkFileTree` (DFS) :
    - visite de chaque entrée
    - filtrage strict des fichiers réguliers (ignore dossiers, liens spéciaux, sockets…)
- Filtrage MP3 fiable :
    - **extension `.mp3`**
    - **type MIME** via `Files.probeContentType(path)` (si disponible sur l’OS)
- Gestion des erreurs sans arrêt global :
    - droits insuffisants / fichier corrompu / chemin inaccessible → on continue le scan
- Résultat : une liste ordonnée des pistes MP3 détectées + compteur total

### C) Génération de playlists (3 formats)
- **XSPF (XML)** : structure conforme (trackList + location)
- **JSPF (JSON)** : structure playlist JSON (tracks + location)
- **M3U8 (UTF-8)** : texte encodé en UTF-8, une ligne par piste (chemins)
- Sortie contrôlée :
    - par défaut : fichier généré avec un nom standard (si `-o` absent, selon implémentation)
    - sinon : sortie explicite via **`-o <fichier>`**

### D) Deux interfaces
- **CLI** :
    - rapide, scriptable, idéal pour démonstration Terminal
- **GUI** :
    - sélection dossier/fichier
    - affichage liste des pistes détectées
    - export playlist via boutons/actions

---

## Bibliothèques externes utilisées

### Lecture des métadonnées (ID3) — **Jaudiotagger**
- **Jaudiotagger** est utilisé pour lire les métadonnées **ID3** des fichiers MP3 :
    - titre (*Title*), artiste (*Artist*), album (*Album*), numéro de piste (*Track*),
      année (*Year*), genre (*Genre*), etc.
- Avantages :
    - extraction fiable des tags ID3 (cas réels : tags manquants, champs incomplets),
    - API simple pour récupérer les champs et gérer les erreurs proprement.

### Lecture audio (MP3) — **JLayer (JavaZoom) / Player**
- **JLayer (javazoom)** est utilisé pour permettre la **lecture audio** de fichiers MP3 dans l’application
  (fonction “Play” / aperçu d’une piste).
- Rôle :
    - décodage MP3 côté Java,
    - lecture via un `Player` (stream MP3 → audio).
- Remarque :
    - la lecture audio est indépendante de l’extraction ID3 : Jaudiotagger gère les tags,
      JLayer gère la lecture du son.

---

## Formats de sortie

### XSPF (XML)
- Avantages : standard, compatible avec plusieurs lecteurs
- Contenu : `<playlist>`, `<trackList>`, `<track>`, `<location>…`

### JSPF (JSON)
- Avantages : facile à manipuler côté web/outils JSON
- Contenu : `{ playlist: { track: [ { location, title, creator, ... } ] } }`

### M3U8 (UTF-8)
- Avantages : format très simple, très utilisé
- Contenu : chemins/URI en UTF-8, une piste par ligne

---

## Pré-requis
- **Java 21**
- **Maven 3.9+** recommandé

---

## Génération des JAR (CLI + GUI) — détaillé

Cette section explique comment produire des JAR exécutables pour :
- le **mode CLI** (console) ;
- le **mode GUI** (graphique) ;
  et comment garantir que ça marche **chez le prof** (donc avec dépendances intégrées).

### 1) Prérequis pour générer les JAR
Dans un terminal, vérifie que Java et Maven sont installés :

```bash
java -version
mvn -version
