# MP3 Métadonnées & Playlists (CLI + GUI) — Java 21

Application Java permettant :
1) d’extraire les **métadonnées ID3** d’un fichier MP3,
2) de **scanner récursivement** un dossier en filtrant **uniquement les vrais MP3** (extension + type MIME),
3) de générer des playlists **XSPF (XML)**, **JSPF (JSON)** et **M3U8 (UTF-8)**,
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

## Mode d’exportation des playlists — détaillé

### Objectif général
Le **mode d’exportation** constitue l’aboutissement logique de l’application :  
à partir d’une **liste fiable de pistes MP3 validées**, l’application génère un **fichier de playlist** dans un format standard (**XSPF**, **JSPF** ou **M3U8**), **interopérable** avec des lecteurs audio externes.

L’export est pensé pour être **découplé** :
- du **scan** (construction de la liste de fichiers candidats),
- de l’**extraction des métadonnées** (enrichissement optionnel),
- et de l’**interface** (CLI/GUI).

Cette séparation garantit :
- robustesse (une erreur d’I/O n’explose pas tout),
- cohérence (même moteur d’export CLI + GUI),
- maintenabilité (ajout d’un format futur sans casser l’existant).

### Données d’entrée de l’export (contrat)
Quel que soit le mode (CLI ou GUI), le module d’export manipule :
- une **liste de pistes** (chemins) correspondant à des MP3 **validés**,
- un **format cible** (XSPF/JSPF/M3U8),
- un **chemin de sortie** (ou un nom par défaut si non fourni).

Règle importante : **aucun export n’est effectué sur une liste vide**.  
Dans ce cas, l’application doit :
- informer l’utilisateur (message clair),
- refuser proprement l’export (sans générer un fichier “vide” ambigu).

### Chaîne de traitement (vue algorithmique)
1. Récupération/constitution de la liste de pistes (issue du scan ou d’un choix utilisateur).
2. Vérification des préconditions :
  - liste non vide,
  - chemin de sortie valide (répertoire parent accessible),
  - format reconnu.
3. Sélection du générateur correspondant au format :
  - générateur XSPF,
  - générateur JSPF,
  - générateur M3U8.
4. Transformation des chemins en représentation exportable :
  - **URI** (`file:///...`) pour XSPF/JSPF,
  - **chemins texte** pour M3U8.
5. Écriture atomique et propre du fichier de playlist :
  - encodage contrôlé,
  - fermeture correcte des flux,
  - message final de succès.

### Export XSPF (XML) — exigences et règles
Le XSPF est un format XML standard. La structure attendue est :

- racine `<playlist version="1" ...>`
- section `<trackList>`
- répétition de `<track>`
- élément `<location>` contenant un **URI de type file**.

Règles de conformité :
- conversion des chemins en URI valides (ex. `path.toUri().toString()`),
- échappement XML si insertion de champs texte (titre/artiste/album),
- absence de crash si métadonnées manquantes :
  - champs absents → omis ou remplacés par valeur neutre.

Résultat attendu : fichier importable dans VLC et lecteurs compatibles.

### Export JSPF (JSON) — exigences et règles
Le JSPF est une représentation JSON structurée d’une playlist.

Règles :
- JSON strict (pas de virgule finale, guillemets corrects),
- encodage UTF-8,
- une liste de tracks, chaque track au minimum avec `location`,
- champs complémentaires (title/creator/album) ajoutés uniquement si disponibles.

Ce format est particulièrement utile pour :
- intégration web,
- traitements automatiques,
- tests.

### Export M3U8 (UTF-8) — exigences et règles
Le M3U8 est un format texte :
- **une piste par ligne**,
- encodage UTF-8,
- ordre conservé (cohérence entre affichage et export).

Règles :
- pas de caractères parasites,
- chemins normalisés,
- fichier simple et immédiatement utilisable.

### Export en mode CLI — utilisation et déroulement
Exemples typiques :

```bash
java -jar cli.jar -d ./music --xspf -o playlist.xspf
java -jar cli.jar -d ./music --jspf -o playlist.json
java -jar cli.jar -d ./music --m3u8 -o playlist.m3u8
## Pré-requis
- **Java 21**
- **Maven 3.9+** recommandé

---

## Génération des JAR (CLI + GUI) — détaillé

Cette section décrit une procédure **reproductible** pour produire des JAR **exécutables** pour :
- le **mode CLI** (console) ;
- le **mode GUI** (graphique) ;  
avec un résultat **autonome** (**dépendances incluses**) afin d’éviter les erreurs classiques :
- `ClassNotFoundException`
- `NoClassDefFoundError`
- `Unable to initialize main class ...`

**Objectif :** obtenir **1 fichier JAR** par mode (**CLI** et **GUI**) qui se lance directement avec :

```bash
java -jar cli.jar
java -jar gui.jar
### Export en mode CLI — utilisation et déroulement

#### Déroulement interne
1. **Parsing des arguments** (`-d`, `-f`, `--xspf/--jspf/--m3u8`, `-o`)
2. **Validation** (options compatibles, un seul format choisi)
3. **Scan du dossier** si l’option `-d` est fournie
4. **Constitution de la liste** des MP3 validés (fichiers réguliers + filtre MP3)
5. **Appel du moteur d’export** avec le format demandé
6. **Écriture du fichier** puis **message de confirmation** à l’utilisateur

#### Propriétés du mode CLI
- **Reproductible** : mêmes arguments → même résultat
- **Scriptable** : intégrable dans scripts / automatisations
- **Adapté à une démonstration Terminal** : clair, rapide, traçable

---

### Export en mode GUI — utilisation et déroulement

#### Principe
1. L’utilisateur **sélectionne un dossier** (ou un fichier)
2. La GUI **affiche la liste** des pistes détectées
3. L’utilisateur **choisit un format** (XSPF / JSPF / M3U8)
4. La GUI **demande le fichier de sortie** (sélecteur / file chooser)
5. La GUI **déclenche l’export** via **le même moteur** que le CLI

#### Garanties
- **Pas de duplication** de la logique métier dans la GUI (même moteur d’export)
- **Messages d’erreur explicites** (liste vide, chemin invalide, accès refusé)
- **Confirmation visuelle** en cas de succès (message, popup, statut)

---

### Gestion d’erreurs et robustesse (export)

Le module d’export doit gérer **sans crash** :
- chemin de sortie non accessible,
- permission refusée,
- erreur d’écriture disque,
- liste vide,
- piste supprimée entre le scan et l’export.

#### Comportement attendu
- **message clair** (cause + action attendue),
- **arrêt propre** de l’export (aucune exception non gérée),
- **pas de fichier partiellement écrit** non détecté (écriture contrôlée).
