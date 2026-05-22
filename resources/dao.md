# JDBC — Communication Java ↔ Base de données

> JDBC (Java Database Connectivity) est l'interface standard de Java pour communiquer avec une base de données relationnelle. Ce n'est pas une bibliothèque externe : c'est intégré dans Java (`java.sql`).

---

## Le problème que JDBC résout

Java et une base de données sont deux systèmes qui ne parlent pas la même langue. Java manipule des **objets**, la base manipule des **tables et des lignes**. JDBC est le traducteur entre les deux.

```
┌─────────────────┐          ┌──────────────┐         ┌──────────────────┐
│   Code Java     │──JDBC──▶ │    Driver    │──SQL───▶│  Base de données │
│  (objets, POJOs)│◀──JDBC── │  (traducteur)│◀──rows──│  (tables, lignes)│
└─────────────────┘          └──────────────┘         └──────────────────┘
```

Le **driver** est le seul élément qui change selon la base utilisée (PostgreSQL, H2, MySQL...). Le reste du code Java reste identique.

---

## Les 4 objets fondamentaux de JDBC

|         Objet       |                       Rôle                       |
|---------------------|--------------------------------------------------|
|     `Connection`    |           Le canal ouvert vers la base           |
| `PreparedStatement` | Une requête SQL paramétrée, prête à être envoyée |
|      `ResultSet`    |   Le curseur qui parcourt les lignes renvoyées   |
|   `DriverManager`   |      Le point d'entrée qui crée la connexion     |

---

## Flux d'une requête JDBC

```
Main.java
   │
   │  DriverManager.getConnection(url, user, pass)
   ▼
Connection  ──────────────────────────────────────────▶ Base de données
   │                                                         │
   │  conn.prepareStatement("SELECT * FROM utilisateurs      │
   │                         WHERE nom = ?")                 │
   ▼                                                         │
PreparedStatement                                            │
   │                                                         │
   │  ps.setString(1, "Alice")                               │
   │  ps.executeQuery()          ───── requête SQL ─────────▶│
   ▼                             ◀──── lignes résultat ───── │
ResultSet                                                    │
   │
   │  while (rs.next()) { ... }
   ▼
Objets Java (Utilisateur, Produit, Commande...)
```

---

## Le pattern DAO (Data Access Object)

Le DAO est une convention d'architecture qui **isole tout le code SQL** dans des classes dédiées. Le reste de l'application ne sait pas que SQL existe.

### Pourquoi

- Si tu changes de base de données (H2 → PostgreSQL), tu ne modifies que les DAO.
- Le code métier reste lisible et ne mélange pas logique et requêtes.
- Chaque DAO a une responsabilité unique : une table = un DAO.

### Structure

```
┌──────────────────────────────────────────────────────────┐
│                        Main.java                         │
│         initialise la connexion, instancie tout          │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────┐
│                MutationService.java                      │
│        logique métier — ne connaît pas SQL               │
│        appelle les DAO, travaille sur des objets Java    │
└──────────┬───────────────────────────┬───────────────────┘
           │                           │
           ▼                           ▼
┌──────────────────┐       ┌───────────────────────┐
│   BienDAO.java   │       │   LieuDAO.java        │
│   tout le SQL    │       │   tout le SQL         │
│   lié à la       │       │   lié à la            │
│   table Bien     │       │   table Lieu          │
└────────┬─────────┘       └──────────┬────────────┘
         │                            │
         └──────────────┬─────────────┘
                        ▼
┌──────────────────────────────────────────────────────────┐
│                  Base de données (H2 / PostgreSQL)       │
└──────────────────────────────────────────────────────────┘
```

---

## Le POJO / Model

Un POJO (Plain Old Java Object) est une classe Java simple qui représente **une ligne d'une table**. Aucune logique, aucune requête — juste des champs et des getters/setters. Ce sont nos models du dossier model. 

``` Ex : 
Table SQL                                                Classe Java (POJO)
───────────────────────────────────────────              ────────────────────────────────────────────────────
lieux                                              ←→    Lieu.java
   id_lieu     INTEGER PRIMARY KEY AUTOINCREMENT   ←→       public int id_lieu
   codePostal  INT                                 ←→       public int codePostal
   codeCommune INT                                 ←→       public int codeCommune
   commune     VARCHAR(100)                        ←→       public String commune
   codeDept    VARCHAR(50)                         ←→       public String codeDept
   department  VARCHAR(50)                         ←→       public String department
   codeRegion  INT                                 ←→       public int codeRegion
   region      VARCHAR(100)                        ←→       public String region

Le DAO lit les lignes du `ResultSet` et les convertit en objets POJO. C'est le seul endroit où cette conversion a lieu.

---

## Architecture générique d'une application Java + JDBC

```
src/main/java
├── Main.java                    ← point d'entrée
│                                   crée la connexion
│                                   instancie DAO + Service
│
├── database/
│   └── DatabaseManager.java    ← gère la connexion JDBC
│                                   exécute schema.sql et data.sql
|   └── DatabaseConnection.java
│
|   ├── model/                       ← POJOs (1 classe = 1 table)
│     ├── Bien.java
│     ├── Lieu.java
|     ├── Lot.java
|     ├── Mutation.java
|     └── Parcelle.java
│
|   ├── dao/                         ← tout le SQL (1 DAO = 1 table)
│     ├── BienDAO.java
|     ├── JointureDAO.java
│     ├── LieuDAO.java
|     ├── LotDAO.java
|     ├── MutationDAO.java
|     ├── ObjetDAO.java
|     └── ParcelleDAO.java
│
└── service/
    └── MutationService.java          ← logique métier
                                    appelle les DAO
                                    travaille sur des objets Java

src/main/resources/
├── schema.sql                   ← CREATE TABLE IF NOT EXISTS ...
└── index.sql                    ← Accélerer la dispersion des données

---

## Dépendances Maven

JDBC est natif Java — aucune dépendance. Seul le **driver** de la base choisie est à ajouter dans `pom.xml` :

Les imports dans le code Java sont toujours les mêmes, quel que soit le driver :

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
```

---

## Liens de référence

- [JDBC — tutoriel officiel Oracle](https://docs.oracle.com/javase/tutorial/jdbc/overview/index.html)
- [Driver PostgreSQL pour Java](https://jdbc.postgresql.org/documentation/)
- [Documentation H2](https://h2database.com/html/main.html)
- [Maven — versions du driver H2](https://mvnrepository.com/artifact/com.h2database/h2)
- [Maven — versions du driver PostgreSQL](https://mvnrepository.com/artifact/org.postgresql/postgresql)
