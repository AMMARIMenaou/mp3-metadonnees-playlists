# MP3 Métadonnées & Playlists (CLI + GUI) — Java 21

Application Java permettant :

1. d’extraire les **métadonnées ID3** d’un fichier MP3,
2. de **scanner récursivement** un dossier en filtrant **uniquement les vrais MP3** (extension + type MIME),
3. de générer des playlists **XSPF (XML)**, **JSPF (JSON)** et **M3U8 (UTF-8)**,

avec deux interfaces : **CLI (console)** et **GUI (graphique)**.

---

## Liens GitHub

- Dépôt principal : https://github.com/AMMARIMenaou/mp3-metadonnees-playlists
- Dépôt du binôme : https://github.com/Rozo-ow/Java-MP3

---

## Sommaire

- [Fonctionnalités](#fonctionnalités)
- [Bibliothèques externes utilisées](#bibliothèques-externes-utilisées)
- [Formats de sortie](#formats-de-sortie)
- [Mode d’exportation des playlists — détaillé](#mode-dexportation-des-playlists--détaillé)
- [Pré-requis](#pré-requis)
- [Génération des JAR (CLI + GUI) — détaillé](#génération-des-jar-cli--gui--détaillé)
- [Utilisation CLI](#utilisation-cli)
- [Utilisation GUI](#utilisation-gui)
- [Gestion d’erreurs et robustesse](#gestion-derreurs-et-robustesse)
- [Auteurs](#auteurs)

---

## Fonctionnalités

### A) Extraction des métadonnées MP3 (ID3)

- Lecture des tags ID3 :
  - titre, artiste, album, numéro de piste, année, genre, durée (si disponible), etc.
- Gestion des cas réels :
  - tags manquants → valeurs neutres sans crash
  - fichiers illisibles → erreur contrôlée et message clair
- Mode fichier unique : analyse d’un MP3 précis (option `-f`)

### B) Scan récursif d’un dossier (robuste)

- Parcours récursif via `Files.walkFileTree` (DFS) :
  - visite de chaque entrée
  - filtrage strict des fichiers réguliers (ignore dossiers, liens spéciaux, sockets…)
- Filtrage MP3 fiable :
  - extension `.mp3`
  - type MIME via `Files.probeContentType(path)` (si disponible sur l’OS)
- Gestion des erreurs sans arrêt global :
  - droits insuffisants / fichier corrompu / chemin inaccessible → on continue le scan
- Résultat : liste ordonnée des pistes MP3 détectées + compteur total

### C) Génération de playlists (3 formats)

- **XSPF (XML)** : structure conforme (trackList + location)
- **JSPF (JSON)** : structure playlist JSON (tracks + location)
- **M3U8 (UTF-8)** : texte encodé en UTF-8, une ligne par piste (chemins)
- Sortie contrôlée :
  - export refusé si la liste est vide (message clair)
  - sortie via `-o <fichier>` en CLI ou sélecteur de fichier en GUI

### D) Deux interfaces

- **CLI** :
  - rapide, scriptable, idéal pour démonstration Terminal
- **GUI** :
  - sélection dossier/fichier
  - affichage liste des pistes détectées
  - export playlist via actions/boutons
  - playlist par défaut (toutes les pistes chargées) + playlist personnalisée (pistes sélectionnées)

---

## Bibliothèques externes utilisées

### Lecture des métadonnées (ID3) — Jaudiotagger

- **Jaudiotagger** est utilisé pour lire les métadonnées **ID3** des fichiers MP3 :
  - titre (*Title*), artiste (*Artist*), album (*Album*), numéro de piste (*Track*),
    année (*Year*), genre (*Genre*), etc.
- Avantages :
  - extraction fiable des tags ID3 (cas réels : tags manquants, champs incomplets)
  - gestion d’erreurs propre (fichiers invalides / tags absents)

### Lecture audio (MP3) — JLayer (JavaZoom)

- **JLayer** est utilisé pour permettre la **lecture audio** de fichiers MP3 dans l’application (fonction “Play”).
- Rôle :
  - décodage MP3 côté Java
  - lecture via un `Player` (stream MP3 → audio)
- Remarque :
  - Jaudiotagger gère les tags
  - JLayer gère le son

---

## Formats de sortie

### XSPF (XML)

- Avantages : standard, compatible avec plusieurs lecteurs
- Contenu : `<playlist>`, `<trackList>`, `<track>`, `<location>…`
- Règle : les chemins sont exportés en URI, typiquement via `path.toUri().toString()`.

### JSPF (JSON)

- Avantages : facile à manipuler côté web/outils JSON
- Contenu : structure JSON avec `tracks` contenant au minimum `location`
- Champs additionnels (title/creator/album) possibles si disponibles.

### M3U8 (UTF-8)

- Avantages : format simple et très utilisé
- Contenu : une piste par ligne (chemins/URI), encodage UTF-8
- Ordre conservé (cohérence affichage/export).

---

## Mode d’exportation des playlists — détaillé

### Objectif général

Le module d’export est l’aboutissement logique de l’application : à partir d’une **liste fiable de pistes MP3 validées**, l’application génère un **fichier de playlist** dans un format standard (**XSPF**, **JSPF** ou **M3U8**) interopérable avec des lecteurs externes.

L’export est conçu pour être **découplé** :
- du **scan** (construction de la liste de fichiers candidats),
- de l’**extraction des métadonnées** (enrichissement optionnel),
- et de l’**interface** (CLI/GUI).

Cette séparation garantit :
- robustesse (une erreur d’I/O n’explose pas tout),
- cohérence (même moteur d’export CLI + GUI),
- maintenabilité (ajout d’un format futur sans casser l’existant).

### Données d’entrée de l’export (contrat)

Quel que soit le mode (CLI ou GUI), le module d’export manipule :
- une **liste de pistes** correspondant à des MP3 validés,
- un **format cible** (XSPF/JSPF/M3U8),
- un **chemin de sortie**.

Règle importante : **aucun export n’est effectué sur une liste vide**.  
Dans ce cas, l’application doit :
- informer l’utilisateur (message clair),
- refuser proprement l’export (sans fichier “vide” ambigu).

### Chaîne de traitement (vue algorithmique)

1. Récupération/constitution de la liste de pistes (issue du scan ou d’un choix utilisateur).
2. Vérification des préconditions :
  - liste non vide,
  - chemin de sortie valide,
  - format reconnu.
3. Sélection du générateur correspondant au format :
  - générateur XSPF,
  - générateur JSPF,
  - générateur M3U8.
4. Transformation des chemins en représentation exportable :
  - URI (`file:///...`) pour XSPF/JSPF,
  - chemins texte pour M3U8.
5. Écriture contrôlée du fichier :
  - encodage correct,
  - fermeture des flux,
  - message final de succès.

### Export XSPF (XML) — règles

- Conversion en URI valides : `path.toUri().toString()`
- Échappement XML si insertion de champs texte (titre/artiste/album)
- Métadonnées manquantes :
  - champs absents → omis ou valeur neutre
- Résultat : importable dans VLC et lecteurs compatibles.

### Export JSPF (JSON) — règles

- JSON strict (syntaxe correcte)
- Encodage UTF-8
- Liste de tracks avec au minimum `location`
- Champs complémentaires ajoutés uniquement si disponibles.

### Export M3U8 (UTF-8) — règles

- Une piste par ligne
- UTF-8
- Pas de caractères parasites
- Ordre conservé

---

## Pré-requis

- **Java 21**
- **Maven 3.9+** recommandé

---

## Génération des JAR (CLI + GUI) — détaillé

Cette section décrit une procédure reproductible pour produire des JAR exécutables pour :
- le mode **CLI** (console),
- le mode **GUI** (graphique),

avec un résultat autonome (**dépendances incluses**) afin d’éviter :
- `ClassNotFoundException`
- `NoClassDefFoundError`
- `Unable to initialize main class ...`

**Objectif :** obtenir **1 fichier JAR** par mode (**CLI** et **GUI**) qui se lance directement avec :

```bash
java -jar target/app-cli.jar
java -jar target/app-gui.jar
