package com.librigo.Model;

import java.sql.Date;

public class Adherent {

    private int id;
    private String numAdherent;
    private String email;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String adresse;
    private String CP;
    private String ville;
    private  int role;

    public Adherent(int id, String numAdherent, String email, String nom, String prenom, Date dateNaissance, String adresse, String CP, String ville, int role) {
        this.id = id;
        this.numAdherent = numAdherent;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.CP = CP;
        this.ville = ville;
        this.role = role;
    }

    // Getters
    public int getId() { return id; }
    public String getNumAdherent() { return numAdherent; }
    public String getEmail() { return email; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public Date getDateNaissance() { return dateNaissance; }
    public String getAdresse() { return adresse; }
    public String getCP() { return CP; }
    public String getVille() { return ville; }
    public int getRole() { return role; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNumAdherent(String numAdherent) { this.numAdherent = numAdherent; }
    public void setEmail(String email) { this.email = email; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setCP(String CP) { this.CP = CP; }
    public void setVille(String ville) { this.ville = ville; }
    public void setRole(int role) { this.role = role; }

}
