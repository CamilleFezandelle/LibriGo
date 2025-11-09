package com.librigo.Model;

public class Genre {

    private int id;
    private String nom;

    public Genre(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return nom;
    }

}
