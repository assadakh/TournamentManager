# Tournament Manager

Application desktop JavaFX de gestion de tournois e-sport en élimination directe.

## Fonctionnalités

- **Gestion des joueurs** — créer et supprimer des joueurs (nom + jeu)
- **Gestion des tournois** — créer des tournois, y inscrire des joueurs
- **Génération de bracket** — tirage aléatoire et génération automatique des matchs
- **Suivi des résultats** — enregistrement des gagnants avec avancement automatique au tour suivant
- **Export PDF** — bracket visuel horizontal exporté en A4 paysage

## Stack technique

| Composant    | Technologie            |
|-------------|------------------------|
| Langage     | Java 21                |
| UI          | JavaFX 21 (FXML)       |
| Base de données | SQLite (jdbc 3.46) |
| Export PDF  | Apache PDFBox 3.0.1    |
| Build       | Maven                  |

## Structure du projet

```
src/main/java/com/tournamentmanager/
├── App.java                         # Point d'entrée JavaFX
├── controller/                      # Contrôleurs des vues
│   ├── HomeController
│   ├── TournamentController
│   ├── NewTournamentController
│   ├── ManageTournamentController
│   ├── PlayerController
│   ├── NewPlayerController
│   ├── BracketController
│   └── ChooseWinnerController
├── model/                           # Entités
│   ├── Tournament
│   ├── Player
│   └── Match
├── dao/                             # Accès aux données (DAO)
│   ├── DatabaseManager
│   ├── TournamentDAO
│   ├── PlayerDAO
│   ├── MatchDAO
│   └── TournamentPlayerDAO
└── util/
    ├── BracketGenerator             # Logique d'élimination directe
    └── PdfExporter                  # Génération du bracket PDF
```

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

## Lancer l'application

### Prérequis

- Java 21+
- Maven 3.8+

### Commande

```bash
mvn clean javafx:run
```

La base de données `tournament.db` est créée automatiquement à la racine du projet au premier lancement.

## Export PDF

Le bracket est exporté en **A4 paysage** avec :
- Visualisation hiérarchique de gauche à droite (round 1 → finale)
- Gagnants mis en évidence (fond bleu, nom en gras)
- Étoile dorée sur le vainqueur de la finale
- Labels des rounds (Huitièmes / Quarts / Demi-finales / Finale)
- Nom du tournoi, jeu et date en en-tête

Le fichier est sauvegardé sous `bracket_<NomDuTournoi>.pdf` à la racine du projet.
