package com.librigo.Controller;

import com.librigo.Model.Genre;
import com.librigo.Model.Livre;
import com.librigo.Model.LivreDAO;
import com.librigo.Model.LivreReserved;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LibraireListeLivresController {

    @FXML private TableView<LivreReserved> tableLivres;
    @FXML private TableColumn<LivreReserved, String> colISBN;
    @FXML private TableColumn<LivreReserved, String> colLivre;
    @FXML private TableColumn<LivreReserved, String> colDispo;
    @FXML private TableColumn<LivreReserved, Void> colAction;
    @FXML private TableColumn<LivreReserved, Void> colSupprimer;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<Genre> genreSelect;

    private List<LivreReserved> allBooks = new ArrayList<>();
    private ObservableList<LivreReserved> data = FXCollections.observableArrayList();

    LivreDAO livreDAO = new LivreDAO();

    @FXML
    public void initialize() {
        allBooks = LivreDAO.getAllLivresWithReservationStatus();

        loadGenres();
        setupColumns();

        allBooks.sort(Comparator.comparing(l -> l.getLivre().getTitre(), String.CASE_INSENSITIVE_ORDER));

        data.setAll(allBooks);
        tableLivres.setItems(data);

        // Filtrage par genre
        genreSelect.setOnAction(event -> updateDisplayedBooks());

        // Barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateDisplayedBooks());

    }

    private void updateDisplayedBooks() {
        Genre selectedGenre = genreSelect.getValue();
        String keyword = searchField.getText().toLowerCase().trim();

        List<LivreReserved> filtered = allBooks.stream()
                .filter(livre -> (selectedGenre == null || selectedGenre.getId() == -1 || livre.getLivre().getGenreId() == selectedGenre.getId()))
                .filter(livre -> livre.getLivre().getTitre().toLowerCase().contains(keyword)
                        || livre.getLivre().getAuteurNom().toLowerCase().contains(keyword)
                        || livre.getLivre().getAuteurPrenom().toLowerCase().contains(keyword)
                        || livre.getLivre().getDateParution().contains(keyword)
                        || livre.getLivre().getISBN().contains(keyword))
                .sorted(Comparator.comparing(livre -> livre.getLivre().getTitre(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        data.setAll(filtered);
    }

    private void loadGenres() {
        LivreDAO livreDAO = new LivreDAO();
        List<Genre> genres = livreDAO.getAllGenres();

        genres.sort((g1, g2) -> g1.getNom().compareToIgnoreCase(g2.getNom()));

        genreSelect.getItems().add(new Genre(-1, "Tous les genres"));
        genreSelect.getItems().addAll(genres);

        genreSelect.setValue(genreSelect.getItems().getFirst());
    }

    private void setupColumns() {

        // ISBN
        colISBN.setCellValueFactory(cell -> {
            Livre l = cell.getValue().getLivre();
            return new SimpleStringProperty(l.getISBN());
        });

        // Livre
        colLivre.setCellValueFactory(cell -> {
            Livre l = cell.getValue().getLivre();
            return new SimpleStringProperty(l.getTitre() + " (" + l.getAuteurPrenom() + " " + l.getAuteurNom() + ")");
        });

        // Disponibilité
        colDispo.setCellValueFactory(cell -> {
            LivreReserved l = cell.getValue();
            if (l.getReservation() != null)
                return new SimpleStringProperty("\uD83D\uDCD8 Emprunté");
            else if (l.getLivre().getDisponibilite() == 1)
                return new SimpleStringProperty("\uD83D\uDCD7 Disponible");
            else
                return new SimpleStringProperty("\uD83D\uDCD5 Indisponible");
        });

        // Colonne Action
        setupActionColumn();

        // Colonne Supprimer
        setupSupprimerColumn();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                LivreReserved livreReserved = getTableView().getItems().get(getIndex());
                Livre livre = livreReserved.getLivre();

                // Cas 1️⃣ : emprunté → rien
                if (livreReserved.getReservation() != null) {
                    setGraphic(null);
                    return;
                }

                // Cas 2️⃣ : disponible → bouton pour rendre indisponible
                if (livre.getDisponibilite() == 1) {
                    actionButton.setText("Rendre indisponible");
                    actionButton.getStyleClass().setAll("action-button", "btn-unavailable");
                    actionButton.setOnAction(e -> toggleDisponibilite(livre, false));
                }
                // Cas 3️⃣ : indisponible → bouton pour rendre disponible
                else {
                    actionButton.setText("Rendre disponible");
                    actionButton.getStyleClass().setAll("action-button", "btn-available");
                    actionButton.setOnAction(e -> toggleDisponibilite(livre, true));
                }

                HBox box = new HBox(actionButton);
                box.setStyle("-fx-alignment: CENTER;");
                setGraphic(box);
            }

        });
    }

    private void setupSupprimerColumn() {
        colSupprimer.setCellFactory(param -> new TableCell<>() {
            private final Button supprimerButton = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                LivreReserved livreReserved = getTableView().getItems().get(getIndex());
                Livre livre = livreReserved.getLivre();

                if (livreReserved.getReservation() != null) {
                    setGraphic(null);
                    return;
                }

                supprimerButton.setText("Supprimer");
                supprimerButton.getStyleClass().setAll("delete-button");

                supprimerButton.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmer la suppression");
                    alert.setHeaderText("Supprimer le livre ?");
                    alert.setContentText("Souhaitez-vous vraiment supprimer \"" + livre.getTitre() + "\" de la bibliothèque ? Cette action est irréversible.");

                    ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                    ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(yesButton, noButton);

                    alert.showAndWait().ifPresent(type -> {
                        if (type == yesButton) {
                            boolean success = LivreDAO.deleteLivreById(livre.getId());

                            if (!success) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur de suppression");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Une erreur est survenue lors de la suppression. Veuillez réessayer plus tard.");
                                errorAlert.showAndWait();
                                return;
                            }

                            data.removeIf(lr -> lr.getLivre().getId() == livre.getId());
                            allBooks.removeIf(lr -> lr.getLivre().getId() == livre.getId());

                            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                            infoAlert.setTitle("Livre supprimé");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Le livre \"" + livre.getTitre() + "\" a été supprimé avec succès.");
                            infoAlert.showAndWait();

                            tableLivres.refresh();
                        }
                    });
                });

                HBox box = new HBox(supprimerButton);
                box.setStyle("-fx-alignment: CENTER;");
                setGraphic(box);
            }

        });
    }

    private void toggleDisponibilite(Livre livre, boolean disponible) {
        livre.setDisponibilite(disponible ? 1 : 0);
        livreDAO.updateLivreDisponibilite(livre.getId(), livre.getDisponibilite());

        allBooks = LivreDAO.getAllLivresWithReservationStatus();
        updateDisplayedBooks();
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
