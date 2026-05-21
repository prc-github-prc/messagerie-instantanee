CREATE TABLE IF NOT EXISTS User (
    id_user         INTEGER PRIMARY KEY AUTOINCREMENT,
    username        VARCHAR(25),
    password_hash   VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS Messages (
    id_message      INTEGER PRIMARY KEY AUTOINCREMENT,
    contenu         VARCHAR(250),
    datage          DATE,
    horo            TIME,
    id_user         INTEGER NOT NULL,
    id_discussion   INTEGER NOT NULL,
    FOREIGN KEY (id_discussion) REFERENCES Discussion(id_discussion)
    FOREIGN KEY (id_user) REFERENCES User(id_user)
);

CREATE TABLE IF NOT EXISTS Discussion(
    id_discussion   INTEGER PRIMARY KEY AUTOINCREMENT,
    nom_discussion  VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Hide(
    id_user         INTEGER NOT NULL,
    id_discussion   INTEGER NOT NULL,
    PRIMARY KEY (id_message,id_discussion)
    FOREIGN KEY (id_user) REFERENCES User(id_user)
    FOREIGN KEY (id_discussion) REFERENCES Discussion(id_discussion)
);

CREATE TABLE IF NOT EXISTS Tag(
    nom_tag         VARCHAR(25) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS Tags(
    nom_tag         VARCHAR(25) NOT NULL,
    id_discussion   INTEGER NOT NULL,
    PRIMARY KEY (nom_tag, id_discussion)
    FOREIGN KEY (nom_tag) REFERENCES Tag(nom_tag)
    FOREIGN KEY (id_discussion) REFERENCES Discussion(nom_discussion)
);

CREATE TABLE IF NOT EXISTS Enum_Role(
    id_enum_role    INTEGER PRIMARY KEY,
    nom_role        VARCHAR(25)
);

INSERT INTO Enum_Role VALUES(0, 'USER'), (1, 'ADMIN');

CREATE TABLE IF NOT EXISTS Roles(
    id_user         INTEGER NOT NULL,
    id_discussion   INTEGER NOT NULL,
    roles           INTEGER NOT NULL
    PRIMARY KEY (id_message,id_discussion)
    FOREIGN KEY (id_user) REFERENCES User(id_user)
    FOREIGN KEY (id_discussion) REFERENCES Discussion(id_discussion)
    FOREIGN KEY (roles) REFERENCES Enum_Role(id_enum_role)
);