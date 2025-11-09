package com.librigo.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.librigo.Model.*;

import com.librigo.Model.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    Adherent adherent = Session.getInstance().getAdherent();

    @FXML private Label nameAccountLabel;
    @FXML private Label numberAccountLabel;
    @FXML private Button logoutButton;
    @FXML private Button spaceButton;
    @FXML private Button libraireButton;
    @FXML private GridPane genreContainer;
    @FXML private ComboBox<Genre> genreSelect;
    @FXML private TextField searchField;

    private List<Livre> allBooks = new ArrayList<>();

    @FXML
    public void initialize() {
        allBooks = LivreDAO.getAllLivres();

        if (adherent.getRole() != 1) {
            libraireButton.setVisible(false);
            libraireButton.setManaged(false);
        }

        nameAccountLabel.setText(adherent.getPrenom() + " " + adherent.getNom());
        numberAccountLabel.setText("#" + adherent.getNumAdherent());

        loadGenres();

        // Affichage initial
        allBooks.sort(Comparator.comparing(Livre::getTitre, String.CASE_INSENSITIVE_ORDER));
        loadBookCards(allBooks);

        // Filtrage par genre
        genreSelect.setOnAction(event -> updateDisplayedBooks());

        // Barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateDisplayedBooks());
    }

    private void updateDisplayedBooks() {
        Genre selectedGenre = genreSelect.getValue();
        String keyword = searchField.getText().toLowerCase().trim();

        List<Livre> filtered = allBooks.stream()
                .filter(livre -> (selectedGenre == null || selectedGenre.getId() == -1 || livre.getGenreId() == selectedGenre.getId()))
                .filter(livre -> livre.getTitre().toLowerCase().contains(keyword)
                        || livre.getAuteurNom().toLowerCase().contains(keyword)
                        || livre.getAuteurPrenom().toLowerCase().contains(keyword)
                        || livre.getDateParution().contains(keyword)
                        || livre.getISBN().contains(keyword))
                .sorted(Comparator.comparing(Livre::getTitre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        loadBookCards(filtered);
    }

    private void loadBookCards(List<Livre> livres) {
        genreContainer.getChildren().clear();

        int COLUMNS_PER_ROW = 2;
        int column = 0;
        int row = 0;

        for (Livre livre : livres) {
            HBox bookCard = createBookCard(livre);
            genreContainer.add(bookCard, column, row);

            column++;
            if (column == COLUMNS_PER_ROW) {
                column = 0;
                row++;
            }
        }
    }

    private HBox createBookCard(Livre livre) {
        VBox infoContainer = new VBox(5);

        infoContainer.getChildren().addAll(
                new Label(livre.getTitre()) {{ getStyleClass().add("book-title"); }},
                new Label(livre.getAuteurPrenom() + " " + livre.getAuteurNom()) {{ getStyleClass().add("book-author"); }},
                new Label(String.valueOf(livre.getDateParution())) {{ getStyleClass().add("book-year"); }},
                new Label(livre.getDisponibilite() == 1 ? "Disponible" : "Indisponible") {{
                    getStyleClass().add("book-availability");
                    if (livre.getDisponibilite() == 1) getStyleClass().add("book-available");
                    else getStyleClass().add("book-unavailable");
                }}
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewButton = new Button("Voir");
        viewButton.getStyleClass().add("book-button");
        viewButton.setOnAction(event -> handleBookPage(livre));


        HBox card = new HBox(20);
        card.getStyleClass().add("book-card");
        card.getChildren().addAll(infoContainer, spacer, viewButton);
        card.setStyle("-fx-alignment: CENTER_LEFT;");

        return card;
    }

    private void loadGenres() {
        LivreDAO livreDAO = new LivreDAO();
        List<Genre> genres = livreDAO.getAllGenres();

        genres.sort((g1, g2) -> g1.getNom().compareToIgnoreCase(g2.getNom()));

        genreSelect.getItems().add(new Genre(-1, "Tous les genres"));
        genreSelect.getItems().addAll(genres);

        genreSelect.setValue(genreSelect.getItems().getFirst());
    }

    @FXML
    private void handleBookPage(Livre livre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/consulterLivre.fxml"));
            Parent root = loader.load();

            LivreController controller = loader.getController();
            controller.setLivre(livre);

            Stage stage = (Stage) genreContainer.getScene().getWindow();
            stage.setTitle("LibriGo - Détails du livre");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButton() {
        Session.getInstance().closeSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("LibriGo - Connexion");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLibraireButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/libraireDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) libraireButton.getScene().getWindow();
            stage.setTitle("LibriGo - Espace Libraire");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSpaceButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/userSpace.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) spaceButton.getScene().getWindow();
            stage.setTitle("LibriGo - Espace Adhérent");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}