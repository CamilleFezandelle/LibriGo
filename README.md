# 📚 LibriGo

> **LibriGo** est une application **JavaFX** de **gestion de bibliothèque**, développée dans le cadre du **BTS SIO – option SLAM**.  
Elle permet aux adhérents de consulter, emprunter et restituer des livres, tandis que les libraires peuvent gérer le catalogue, les genres, les auteurs et les comptes utilisateurs via une interface moderne et ergonomique.

---

## ✨ Fonctionnalités principales

- 🔐 **Authentification sécurisée des adhérents**
- 📖 **Consultation du catalogue** des livres disponibles
- 🧾 **Suivi des emprunts et des retours** en temps réel
- 👤 **Espace personnel adhérent**
    - Modification des informations personnelles
    - Changement du mot de passe
- 🧑‍💼 **Panel de gestion** (pour le personnel de la bibliothèque)
    - Gestion des livres, auteurs et genres
    - Suivi des emprunts en cours
    - Gestion des comptes adhérents
    - Conservation de l’historique : lorsqu’un adhérent est supprimé, son `adherent_id` devient `NULL` pour garder ses anciens emprunts.
- 🗄️ **Connexion sécurisée** à une base de données **MySQL**

---

## 🧰 Technologies utilisées

- **Langage principal :** Java
- **Interface graphique :** JavaFX
- **Base de données :** MySQL
- **Architecture :** MVC (Model-View-Controller)
- **Pattern d’accès aux données :** DAO
- **Sécurité :** JBCrypt (hachage des mots de passe)

---

## 🧱 Architecture technique

L’application repose sur une architecture claire et modulaire :
- **Model** : gestion des entités et logique métier
- **View** : interfaces JavaFX (FXML + CSS)
- **Controller** : logique de navigation et de traitement des actions
- **DAO** : accès structuré à la base de données

---

## 🗄️ Base de données

### Tables principales :

- `ADHERENT`
- `AUTEUR`
- `GENRE`
- `LIVRE`
- `RESERVATION`

### Schéma relationnel :

![Schéma de la base de données](docs/database/database.png)

---

## 🎥 Démonstration

Une **démo vidéo** présente l’interface et les principales fonctionnalités de l’application :  
[Voir la démo sur YouTube](https://youtu.be/_k0qSVl-Cjw)

---

## 📸 Captures d’écran

![Connexion](docs/screenshots/screen-1.png)
![Inscription](docs/screenshots/screen-2.png)
![Mot de passe oublié](docs/screenshots/screen-3.png)
![Dashboard](docs/screenshots/screen-4.png)
![Détails d'un livre](docs/screenshots/screen-5.png)
![Livres empruntés](docs/screenshots/screen-6.png)
![Informations personnelles](docs/screenshots/screen-7.png)
![Changement de mot de passe](docs/screenshots/screen-8.png)
![Ajouter un élément](docs/screenshots/screen-9.png)
![Gestion des livres](docs/screenshots/screen-10.png)
![Gestion des emprunts](docs/screenshots/screen-11.png)
![Gestion des comptes adhérents](docs/screenshots/screen-12.png)

---

## 👨‍💻 Auteur

**Camille Fezandelle**  
Étudiant en **BTS SIO – SLAM (Solutions Logicielles et Applications Métiers)**  
[Portfolio](https://camillefezandelle.cloud)
