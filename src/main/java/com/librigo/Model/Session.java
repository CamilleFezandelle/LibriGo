package com.librigo.Model;

public class Session {

    private static Session instance;
    private Adherent AdherentConnecte;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void createSession(Adherent adherent) {
        if (this.AdherentConnecte != null) {
            return;
        }
        this.AdherentConnecte = adherent;
    }

    public Adherent getAdherent() {
        return this.AdherentConnecte; // Retourne l'adhérent connecté
    }

    public boolean isConnected() {
        return this.AdherentConnecte != null; // Vérifie si une session est active
    }

    public void closeSession() {
        this.AdherentConnecte = null; // Ferme la session
    }
}
