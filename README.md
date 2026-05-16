# Tournament Manager

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-007396?style=flat&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-3.46-003B57?style=flat&logo=sqlite&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat&logo=apachemaven&logoColor=white)
![Statut](https://img.shields.io/badge/Statut-Fonctionnel-brightgreen)

Application desktop JavaFX de gestion de tournois e-sport en élimination directe, développée dans le cadre du BTS SIO option SLAM.

---

## Fonctionnalités

- **Gestion des joueurs** — créer et supprimer des joueurs (pseudo + jeu)
- **Gestion des tournois** — créer des tournois, y inscrire des joueurs
- **Génération de bracket** — tirage aléatoire et génération automatique des matchs
- **Suivi des résultats** — enregistrement des gagnants avec avancement automatique au tour suivant
- **Export PDF** — bracket visuel horizontal exporté en A4 paysage

---

## Stack technique

| Couche | Technologie | Version |
|---|---|---|
| Langage | Java | 21 |
| UI | JavaFX (FXML) | 21 |
| Base de données | SQLite (jdbc) | 3.46 |
| Export PDF | Apache PDFBox | 3.0.1 |
| Build | Maven | 3.8+ |
| Tests | JUnit 5 | — |

---

## Lancer l'application

### Prérequis

- Java 21+
- Maven 3.8+
- IntelliJ IDEA (recommandé)

### Via IntelliJ IDEA

1. Sur l'écran d'accueil d'IntelliJ IDEA, cliquer sur `Get from VCS` (cloner depuis GitHub)
2. Coller l'URL du dépôt GitHub et cliquer sur `Clone`
3. IntelliJ détecte automatiquement la configuration Maven
4. Ouvrir le fichier `src/main/java/com/tournamentmanager/App.java`
5. Cliquer sur le bouton **Start** (▶) en haut de l'écran pour lancer l'application

### Via ligne de commande

```bash
mvn clean javafx:run
```

La base de données `tournament.db` est créée automatiquement à la racine du projet au premier lancement.

### Lancer les tests

```bash
mvn test
```

---

## Structure de la base de données

| Table | Rôle |
|---|---|
| `players` | Joueurs inscrits (pseudo + jeu) |
| `tournaments` | Tournois créés (nom, jeu, date, statut) |
| `tournament_players` | Association joueurs ↔ tournois |
| `matches` | Matchs générés avec gagnant et numéro de round |

---

## Sécurité

### 1. Validation et sanitisation des entrées

Toutes les saisies utilisateur passent par la classe `InputValidator` avant d'être traitées :

- Longueur comprise entre 2 et 50 caractères
- Caractères autorisés uniquement : lettres (accents inclus), chiffres, espaces, tirets, apostrophes — les caractères dangereux (`<`, `>`, `"`, `;`, etc.) sont rejetés
- Format de date `jj/mm/aaaa` vérifié via `LocalDate.parse()` — les dates impossibles (ex. 31/02) et les dates passées sont refusées

### 2. Gestion sécurisée des erreurs

Aucun détail technique (message SQL, trace d'exception) n'est affiché à l'utilisateur. Les erreurs internes sont absorbées silencieusement côté DAO ; l'utilisateur voit uniquement un message générique. Cela empêche la divulgation d'informations sur la structure de la base de données (**OWASP A09**).

### 3. Prévention des injections SQL

Toutes les requêtes utilisent des `PreparedStatement` avec paramètres liés (`?`). Aucune concaténation de chaînes dans les requêtes SQL.

### 4. Contraintes d'intégrité en base de données

Le schéma SQLite applique des contraintes `CHECK` indépendamment de l'application (défense en profondeur) :

| Table | Contrainte |
|---|---|
| `players` | `length(name) BETWEEN 2 AND 50` |
| `players` | `length(game) BETWEEN 2 AND 50` |
| `tournaments` | `length(name) BETWEEN 2 AND 50` |
| `tournaments` | `length(game) BETWEEN 2 AND 50` |
| `tournaments` | `length(date) = 10` |
| `tournaments` | `status IN ('En attente', 'En cours', 'Terminé')` |
| `matches` | `round >= 1` |
| `matches` | `player1_id != player2_id` |

### 5. Surface d'attaque réduite

L'application est entièrement **locale** : pas de serveur, pas de réseau, pas d'API exposée. La base de données SQLite réside sur le poste de l'utilisateur et n'est jamais transmise. Cette architecture limite structurellement les vecteurs d'attaque externes (pas d'interception réseau, pas d'injection à distance, pas de fuite vers un tiers).

### 6. Limites connues

| Limite | Justification |
|---|---|
| Pas d'authentification | Application mono-utilisateur en environnement local, hors scope du projet |
| Base de données non chiffrée | SQLite en clair sur le disque — acceptable pour un usage local sans données sensibles |

### 7. Tests unitaires de sécurité

La classe `InputValidatorTest` couvre 21 cas de test, dont des tentatives d'injection SQL, de balises HTML et de dépassement de longueur.

La classe `TournamentStatusTest` couvre 10 cas de test sur la logique de statut des tournois : détection de fin de tournoi, avancement automatique au round suivant, et état des matchs.

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
```

---

## Structure du projet

```
src/main/java/com/tournamentmanager/
├── App.java
├── controller/
│   ├── HomeController
│   ├── TournamentController
│   ├── NewTournamentController
│   ├── ManageTournamentController
│   ├── PlayerController
│   ├── NewPlayerController
│   ├── BracketController
│   └── ChooseWinnerController
├── model/
│   ├── Tournament
│   ├── Player
│   └── Match
├── dao/
│   ├── DatabaseManager
│   ├── TournamentDAO
│   ├── PlayerDAO
│   ├── MatchDAO
│   └── TournamentPlayerDAO
└── util/
    ├── BracketGenerator
    ├── InputValidator
    ├── PdfExporter
    └── Seeder

src/test/java/com/tournamentmanager/
├── model/
│   └── TournamentStatusTest
└── util/
    └── InputValidatorTest
```

---

## Navigation

```
Accueil
├── Tournois
│   ├── Nouveau tournoi
│   └── Gérer le tournoi
│       └── Bracket
│           └── Choisir le gagnant
└── Joueurs
    └── Nouveau joueur
```

---

## Export PDF

Le bracket est exporté en **A4 paysage** avec :

- Visualisation hiérarchique de gauche à droite (round 1 → finale)
- Gagnants mis en évidence (fond bleu, nom en gras)
- Étoile dorée sur le vainqueur de la finale
- Labels des rounds (Huitièmes / Quarts / Demi-finales / Finale)
- Nom du tournoi, jeu et date en en-tête

Le fichier est sauvegardé sous `bracket_<NomDuTournoi>.pdf` à la racine du projet.

---

## Développement

Développé dans le cadre du BTS SIO option SLAM.
