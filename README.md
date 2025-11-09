# 📚 LibriGo

> **LibriGo** est une application JavaFX de gestion de bibliothèque, développée dans le cadre du BTS SIO option **SLAM**.  
Elle permet la gestion des livres, des auteurs, des genres et des emprunts des adhérents via une interface moderne et ergonomique.

---

## ✨ Fonctionnalités principales

- 🔐 **Authentification des adhérents**
- 📖 **Consultation des livres disponibles**
- 🧾 **Suivi des emprunts et retours**
- 👤 **Espace personnel adhérent**
    - Modification des informations personnelles
    - Changement sécurisé du mot de passe
- 🧑‍💼 **Panel de gestion** (pour le personnel de la librairie)
- 🗄️ **Connexion sécurisée à une base de données MySQL**

---

## 🧰 Technologies utilisées

- **Language principal :** Java
- **Framework UI :** JavaFX
- **Gestion BDD :** MySQL

---

## 🧱 Architecture technique

L’application repose sur une structure **MVC (Model-View-Controller)** avec un accès aux données via le **pattern DAO**.

---

## 🗄️ Base de données

### Tables :

- `ADHERENT`
- `LIVRE`
- `AUTEUR`
- `GENRE`
- `RESERVATION`

### Schéma relationnel :

  ![Schéma de la base de données](docs/database/database.png)


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