package com.librigo.Controller;

import com.librigo.Model.Adherent;
import com.librigo.Model.AdherentDAO;
import com.librigo.Model.Livre;
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

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LibraireListeAdherentsController {

    @FXML private TableView<Adherent> tableAdherents;
    @FXML private TableColumn<Adherent, String> colNum;
    @FXML private TableColumn<Adherent, String> colNom;
    @FXML private TableColumn<Adherent, String> colEmail;
    @FXML private TableColumn<Adherent, String> colDateNaissance;
    @FXML private TableColumn<Adherent, Void> colSupprimer;
    @FXML private Button backButton;
    @FXML private TextField searchField;

    private List<Adherent> allAdherents = new ArrayList<>();
    private ObservableList<Adherent> data = FXCollections.observableArrayList();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    AdherentDAO adherentDAO = new AdherentDAO();

    @FXML
    public void initialize() {
        allAdherents = AdherentDAO.getAllAdherents();

        setupColumns();

        allAdherents.sort(Comparator.comparing(a -> a.getNom().toLowerCase()));

        data.setAll(allAdherents);
        tableAdherents.setItems(data);

        // Barre de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateDisplayedAdherents());
    }

    private void updateDisplayedAdherents() {
        String keyword = searchField.getText().toLowerCase().trim();

        List<Adherent> filtered = allAdherents.stream()
                .filter(adherent -> {
                    String keywordLower = keyword.toLowerCase();

                    String dateNaissance = adherent.getDateNaissance().toLocalDate().format(formatter).toLowerCase();

                    return adherent.getNumAdherent().toLowerCase().contains(keywordLower)
                            || adherent.getNom().toLowerCase().contains(keywordLower)
                            || adherent.getPrenom().toLowerCase().contains(keywordLower)
                            || adherent.getEmail().toLowerCase().contains(keywordLower)
                            || dateNaissance.contains(keywordLower);
                })
                .sorted(Comparator.comparing(a -> a.getNom().toLowerCase()))
                .collect(Collectors.toList());

        data.setAll(filtered);
    }

    private void setupColumns() {

        // Numéro Adhérent
        colNum.setCellValueFactory(cell -> {
            Adherent a = cell.getValue();
            return new SimpleStringProperty("#" + a.getNumAdherent());
        });

        // Nom et Prénom
        colNom.setCellValueFactory(cell -> {
            Adherent a = cell.getValue();
            return new SimpleStringProperty(a.getNom() + " " + a.getPrenom());
        });

        // Email
        colEmail.setCellValueFactory(cell -> {
            Adherent a = cell.getValue();
            return new SimpleStringProperty(a.getEmail());
        });

        // Date de Naissance
        colDateNaissance.setCellValueFactory(cell -> {
            Date date = cell.getValue().getDateNaissance();
            String dateFormatted = date.toLocalDate().format(formatter);
            return new SimpleStringProperty(dateFormatted);
        });

        // Colonne Supprimer
        setupSupprimerColumn();
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

                Adherent adherent = getTableView().getItems().get(getIndex());

                if (adherent.getRole() == 1) {
                    setGraphic(null);
                    return;
                }

                supprimerButton.setText("Supprimer");
                supprimerButton.getStyleClass().setAll("delete-button");

                supprimerButton.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Voulez-vous vraiment supprimer cet adhérent ?");
                    alert.setContentText("Cette action est irréversible.");

                    ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                    ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(yesButton, noButton);

                    alert.showAndWait().ifPresent(type -> {
                        if (type == yesButton) {
                            // Vérification des emprunts en cours
                            List<Livre> emprunts = AdherentDAO.getLivresEmpruntesByAdherent(adherent.getId());

                            // Si des emprunts en cours existent, alors on ne peut pas supprimer l'adhérent
                            if (!emprunts.isEmpty()) {
                                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                                errorAlert.setTitle("Suppression impossible");
                                errorAlert.setHeaderText(null);
                                StringBuilder content = new StringBuilder("L'adhérent \"" + adherent.getNom() + " " + adherent.getPrenom() + "\" ne peut pas être supprimé car il a des emprunts en cours :\n");
                                for (Livre livre : emprunts) {
                                    content.append("- [")
                                            .append(livre.getISBN())
                                            .append("] ")
                                            .append(livre.getTitre())
                                            .append(" (")
                                            .append(livre.getAuteurPrenom())
                                            .append(" ")
                                            .append(livre.getAuteurNom())
                                            .append(")")
                                            .append("\n");
                                }
                                errorAlert.setContentText(content.toString());
                                errorAlert.showAndWait();
                                return;
                            }

                            // Suppression de l'adhérent
                            boolean success = AdherentDAO.deleteAdherent(adherent.getId());

                            if (!success) {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Erreur de suppression");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Une erreur est survenue lors de la suppression. Veuillez réessayer plus tard.");
                                errorAlert.showAndWait();
                                return;
                            }

                            data.removeIf(a -> a.getId() == adherent.getId());
                            allAdherents.removeIf(a -> a.getId() == adherent.getId());

                            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                            infoAlert.setTitle("Adhérent supprimé");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("L'adhérent \"" + adherent.getNom() + " " + adherent.getPrenom() + "\" a été supprimé avec succès.");
                            infoAlert.showAndWait();

                            tableAdherents.refresh();
                        }
                    });
                });

                HBox box = new HBox(supprimerButton);
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
