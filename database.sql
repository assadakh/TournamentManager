-- ============================================================
--  TournamentManager — Création de la base de données SQLite
-- ============================================================

PRAGMA foreign_keys = ON;

-- ------------------------------------------------------------
--  Suppression des tables (ordre inverse des dépendances)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS tournament_players;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS players;

-- ------------------------------------------------------------
--  Table : players
-- ------------------------------------------------------------
CREATE TABLE players (
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT    NOT NULL CHECK(length(name) BETWEEN 2 AND 50),
    game TEXT    NOT NULL CHECK(length(game) BETWEEN 2 AND 50)
);

-- ------------------------------------------------------------
--  Table : tournaments
-- ------------------------------------------------------------
CREATE TABLE tournaments (
    id     INTEGER PRIMARY KEY AUTOINCREMENT,
    name   TEXT    NOT NULL CHECK(length(name) BETWEEN 2 AND 50),
    game   TEXT    NOT NULL CHECK(length(game) BETWEEN 2 AND 50),
    date   TEXT    NOT NULL CHECK(length(date) = 10),
    status TEXT    NOT NULL DEFAULT 'En attente'
                            CHECK(status IN ('En attente', 'En cours', 'Terminé'))
);

-- ------------------------------------------------------------
--  Table : matches
-- ------------------------------------------------------------
CREATE TABLE matches (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    tournament_id INTEGER NOT NULL,
    round         INTEGER NOT NULL CHECK(round >= 1),
    player1_id    INTEGER NOT NULL,
    player2_id    INTEGER NOT NULL,
    winner_id     INTEGER,
    CHECK(player1_id != player2_id),
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (player1_id)    REFERENCES players(id),
    FOREIGN KEY (player2_id)    REFERENCES players(id),
    FOREIGN KEY (winner_id)     REFERENCES players(id)
);

-- ------------------------------------------------------------
--  Table : tournament_players  (table de jointure)
-- ------------------------------------------------------------
CREATE TABLE tournament_players (
    tournament_id INTEGER NOT NULL,
    player_id     INTEGER NOT NULL,
    PRIMARY KEY (tournament_id, player_id),
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (player_id)     REFERENCES players(id)
);

-- ============================================================
--  Données de test (seed)
-- ============================================================

-- Joueurs
INSERT INTO players (name, game) VALUES
    ('Lucas Martin',  'FIFA 25'),
    ('Theo Bernard',  'FIFA 25'),
    ('Emma Dupont',   'FIFA 25'),
    ('Noah Simon',    'FIFA 25'),
    ('Jade Moreau',   'League of Legends'),
    ('Tom Petit',     'League of Legends'),
    ('Lea Garnier',   'League of Legends'),
    ('Hugo Leroy',    'League of Legends'),
    ('Camille Roy',   'Valorant'),
    ('Antoine Blond', 'Valorant');

-- Tournois
INSERT INTO tournaments (name, game, date, status) VALUES
    ('Open FIFA 2026',   'FIFA 25',           '15/06/2026', 'En attente'),
    ('Championnat LoL',  'League of Legends', '20/06/2026', 'En attente'),
    ('Tournoi Valorant', 'Valorant',          '25/06/2026', 'En attente');

-- Inscription des joueurs aux tournois
INSERT INTO tournament_players (tournament_id, player_id) VALUES
    -- Open FIFA 2026 (id=1) -> Lucas, Theo, Emma, Noah (ids 1-4)
    (1, 1), (1, 2), (1, 3), (1, 4),
    -- Championnat LoL (id=2) -> Jade, Tom, Lea, Hugo (ids 5-8)
    (2, 5), (2, 6), (2, 7), (2, 8),
    -- Tournoi Valorant (id=3) -> Camille, Antoine (ids 9-10)
    (3, 9), (3, 10);
