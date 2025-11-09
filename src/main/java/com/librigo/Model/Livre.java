package com.librigo.Model;

public class Livre {

    private int id;
    private String ISBN;
    private String titre;
    private int auteurId;
    private String auteurNom;
    private String auteurPrenom;
    private int genreId;
    private String genreNom;
    private String dateParution;
    private String description;
    private int disponibilite;

    public Livre(int id, String ISBN, String titre, int auteurId, String auteurNom, String auteurPrenom, int genreId, String genreNom, String dateParution, String description, int disponibilite) {
        this.id = id;
        this.ISBN = ISBN;
        this.titre = titre;
        this.auteurId = auteurId;
        this.auteurNom = auteurNom;
        this.auteurPrenom = auteurPrenom;
        this.genreId = genreId;
        this.genreNom = genreNom;
        this.dateParution = dateParution;
        this.description = description;
        this.disponibilite = disponibilite;
    }

    // Getters
    public int getId() { return id; }
    public String getISBN() { return ISBN; }
    public String getTitre() { return titre; }
    public int getAuteurId() { return auteurId; }
    public String getAuteurNom() { return auteurNom; }
    public String getAuteurPrenom() { return auteurPrenom; }
    public int getGenreId() { return genreId; }
    public String getGenreNom() { return genreNom; }
    public String getDateParution() { return dateParution; }
    public String getDescription() { return description; }
    public int getDisponibilite() { return disponibilite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setAuteurId(int auteurId) { this.auteurId = auteurId; }
    public void setAuteurNom(String auteurNom) { this.auteurNom = auteurNom; }
    public void setAuteurPrenom(String auteurPrenom) { this.auteurPrenom = auteurPrenom; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
    public void setGenreNom(String genreNom) { this.genreNom = genreNom; }
    public void setDateParution(String dateParution) { this.dateParution = dateParution; }
    public void setDescription(String description) { this.description = description; }
    public void setDisponibilite(int disponibilite) { this.disponibilite = disponibilite; }

}
