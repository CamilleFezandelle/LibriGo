package com.librigo.Controller;

import com.librigo.Model.Auteur;
import com.librigo.Model.Genre;
import com.librigo.Model.LivreDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class LibraireCreateElementsController {

    @FXML private TextField livreTitle;
    @FXML private ComboBox<Auteur> livreAuteur;
    @FXML private ComboBox<Genre> livreGenre;
    @FXML private TextField livreDate;
    @FXML private TextField livreISBN;
    @FXML private TextArea livreDescription;

    @FXML private TextField auteurNom;
    @FXML private TextField auteurPrenom;

    @FXML private TextField genreNom;

    @FXML private Button backButton;

    @FXML
    private void initialize() {
        loadAuteurs();
        loadGenres();
    }

    private void loadAuteurs() {
        LivreDAO livreDAO = new LivreDAO();
        List<Auteur> auteurs = livreDAO.getAllAuteurs();

        auteurs.sort((a1, a2) -> a1.getNom().compareToIgnoreCase(a2.getNom()));

        livreAuteur.getItems().addAll(auteurs);

        livreAuteur.setValue(livreAuteur.getItems().getFirst());
    }

    private void loadGenres() {
        LivreDAO livreDAO = new LivreDAO();
        List<Genre> genres = livreDAO.getAllGenres();

        genres.sort((g1, g2) -> g1.getNom().compareToIgnoreCase(g2.getNom()));

        livreGenre.getItems().addAll(genres);

        livreGenre.setValue(livreGenre.getItems().getFirst());
    }

    @FXML
    private void handleCreateLivreButton() {
        String title = livreTitle.getText();
        Auteur auteur = livreAuteur.getValue();
        Genre genre = livreGenre.getValue();
        String date = livreDate.getText();
        String isbn = livreISBN.getText();
        String description = livreDescription.getText();

        if (title.isEmpty() || date.isEmpty() || isbn.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        if (title.length() > 120) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le titre ne doit pas dépasser 120 caractères.");
            alert.showAndWait();
            return;
        }

        if (!date.matches("^\\d{4}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer une date valide au format YYYY.");
            alert.showAndWait();
            return;
        }

        if (!isbn.matches("^\\d{13}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un ISBN valide de 13 chiffres.");
            alert.showAndWait();
            return;
        }

        if (description.length() > 2000) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La description ne doit pas dépasser 2000 caractères.");
            alert.showAndWait();
            return;
        }

        LivreDAO livreDAO = new LivreDAO();
        boolean success = livreDAO.createLivre(
                isbn,
                title,
                auteur.getId(),
                genre.getId(),
                date,
                description
        );

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la création du livre. Veuillez réessayer plus tard.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le livre a été créé avec succès.");
        alert.showAndWait();

        livreTitle.clear();
        livreDate.clear();
        livreISBN.clear();
        livreDescription.clear();
        livreAuteur.setValue(livreAuteur.getItems().getFirst());
        livreGenre.setValue(livreGenre.getItems().getFirst());
    }

    @FXML
    private void handleCreateAuteurButton() {
        String nom = auteurNom.getText();
        String prenom = auteurPrenom.getText();

        if (nom.isEmpty() || prenom.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        if (nom.length() > 60 || prenom.length() > 60) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le nom et le prénom ne doivent pas dépasser 60 caractères.");
            alert.showAndWait();
            return;
        }

        LivreDAO livreDAO = new LivreDAO();
        boolean success = livreDAO.createAuteur(nom, prenom);

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la création de l'auteur. Veuillez réessayer plus tard.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("L'auteur a été créé avec succès.");
        alert.showAndWait();

        auteurNom.clear();
        auteurPrenom.clear();

        livreAuteur.getItems().clear();
        loadAuteurs();
    }

    @FXML
    private void handleCreateGenreButton() {
        String nom = genreNom.getText();

        if (nom.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir le champ.");
            alert.showAndWait();
            return;
        }

        if (nom.length() > 40) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le nom du genre ne doit pas dépasser 40 caractères.");
            alert.showAndWait();
            return;
        }

        LivreDAO livreDAO = new LivreDAO();
        boolean success = livreDAO.createGenre(nom);

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la création du genre. Veuillez réessayer plus tard.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Le genre a été créé avec succès.");
        alert.showAndWait();

        genreNom.clear();

        livreGenre.getItems().clear();
        loadGenres();
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("LibriGo - Espace Libraire");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
