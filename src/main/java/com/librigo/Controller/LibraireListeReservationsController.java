package com.librigo.Controller;

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
import com.librigo.Model.*;

import com.librigo.Model.*;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LibraireListeReservationsController {

    @FXML private TableView<LivreReserved> tableReservations;
    @FXML private TableColumn<LivreReserved, String> colISBN;
    @FXML private TableColumn<LivreReserved, String> colLivre;
    @FXML private TableColumn<LivreReserved, String> colAdherent;
    @FXML private TableColumn<LivreReserved, String> colDate;
    @FXML private TableColumn<LivreReserved, String> colStatut;
    @FXML private TableColumn<LivreReserved, Void> colAction;
    @FXML private Button backButton;
    @FXML private TextField searchField;

    private List<LivreReserved> allReservations = new ArrayList<>();
    private ObservableList<LivreReserved> data = FXCollections.observableArrayList();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    LivreDAO livreDAO = new LivreDAO();

    @FXML
    public void initialize() {
        allReservations = ReservationDAO.getAllReservationsWithBooks();

        setupColumns();

        allReservations.sort(Comparator
                .comparing((LivreReserved l) -> l.getReservation().getDateRetour() != null)
                .thenComparing((LivreReserved l) -> l.getReservation().getDateReservation(), Comparator.reverseOrder())
        );

        data.setAll(allReservations);
        tableReservations.setItems(data);

        // Barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateDisplayedReservations());

    }

    private void updateDisplayedReservations() {
        String keyword = searchField.getText().toLowerCase().trim();

        List<LivreReserved> filtered = allReservations.stream()
                .filter(livre -> {
                    String keywordLower = keyword.toLowerCase();
                    String dateResa = "";
                    String dateRetour = "";

                    if (livre.getReservation().getDateReservation() != null) {
                        dateResa = livre.getReservation().getDateReservation().toLocalDate().format(formatter);
                    }

                    if (livre.getReservation().getDateRetour() != null) {
                        dateRetour = livre.getReservation().getDateRetour().toLocalDate().format(formatter);
                    }

                    return livre.getLivre().getTitre().toLowerCase().contains(keywordLower)
                            || livre.getLivre().getAuteurNom().toLowerCase().contains(keywordLower)
                            || livre.getLivre().getAuteurPrenom().toLowerCase().contains(keywordLower)
                            || livre.getLivre().getDateParution().contains(keywordLower)
                            || livre.getLivre().getISBN().toLowerCase().contains(keywordLower)
                            || livre.getReservation().getAdherentNom().toLowerCase().contains(keywordLower)
                            || livre.getReservation().getAdherentPrenom().toLowerCase().contains(keywordLower)
                            || livre.getReservation().getAdherentNumber().toLowerCase().contains(keywordLower)
                            || dateResa.contains(keywordLower)
                            || dateRetour.contains(keywordLower);
                })
                .sorted(Comparator
                        .comparing((LivreReserved l) -> l.getReservation().getDateRetour() != null)
                        .thenComparing((LivreReserved l) -> l.getReservation().getDateReservation(), Comparator.reverseOrder())
                )
                .collect(Collectors.toList());

        data.setAll(filtered);
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

        // Adhérent
        colAdherent.setCellValueFactory(cell -> {
            String a = cell.getValue().getReservation().getAdherentPrenom() + " " + cell.getValue().getReservation().getAdherentNom();
            return new SimpleStringProperty(a);
        });

        // Date de réservation
        colDate.setCellValueFactory(cell -> {
            Date date = cell.getValue().getReservation().getDateReservation();
            String dateResa = (date != null) ? date.toLocalDate().format(formatter) : "";
            return new SimpleStringProperty(dateResa);
        });

        // Statut
        colStatut.setCellValueFactory(cell -> {
            if (cell.getValue().getReservation().getDateRetour() == null) {
                return new SimpleStringProperty("En cours");
            } else {
                Date date = cell.getValue().getReservation().getDateRetour();
                String dateRetour = date.toLocalDate().format(formatter);
                return new SimpleStringProperty("Rendu le " + dateRetour);
            }
        });

        // Colonne Action
        setupActionColumn();
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
                Reservation reservation = livreReserved.getReservation();

                // Si la réservation est déjà rendue → rien
                if (reservation.getDateRetour() != null) {
                    setGraphic(null);
                    return;
                }

                // Si la réservation est en cours → bouton "Marquer comme rendu"
                actionButton.setText("Marquer comme rendu");
                actionButton.getStyleClass().setAll("action-button");
                actionButton.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmer le retour");
                    alert.setHeaderText("Confirmation du retour du livre");
                    alert.setContentText("Êtes-vous sûr de vouloir marquer le livre \"" + livre.getTitre() + "\" comme rendu ?");

                    ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                    ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(yesButton, noButton);

                    alert.showAndWait().ifPresent(type -> {
                        if (type == yesButton) {
                            boolean success = ReservationDAO.rendreLivre(reservation.getAdherentId(), livre.getId());

                            if (!success) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur de mise à jour");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Une erreur est survenue lors de la mise à jour de la réservation. Veuillez réessayer plus tard.");
                                errorAlert.showAndWait();
                                return;
                            }

                            Date today = new Date(System.currentTimeMillis());

                            reservation.setDateRetour(today);
                            livre.setDisponibilite(1);

                            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                            infoAlert.setTitle("Livre rendu");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Le livre \"" + livre.getTitre() + "\" a été marqué comme rendu.");
                            infoAlert.showAndWait();

                            updateDisplayedReservations();
                        }
                    });
                });

                HBox box = new HBox(actionButton);
                box.setStyle("-fx-alignment: CENTER;");
                setGraphic(box);
            }

        });
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
