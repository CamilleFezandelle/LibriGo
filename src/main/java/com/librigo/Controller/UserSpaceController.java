package com.librigo.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.librigo.Model.*;

import com.librigo.Model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserSpaceController {

    @FXML private GridPane genreContainer;
    @FXML private Button backButton;

    @FXML private TextField inputEmail;
    @FXML private TextField inputNumber;
    @FXML private TextField inputLastName;
    @FXML private TextField inputFirstName;
    @FXML private TextField inputAddress;
    @FXML private TextField inputPostalCode;
    @FXML private TextField inputCity;

    @FXML private PasswordField inputOldPassword;
    @FXML private PasswordField inputNewPassword;
    @FXML private PasswordField inputConfirmPassword;

    private List<LivreReserved> booksReserved = new ArrayList<>();

    @FXML
    public void initialize() {
        loadUserInfo();

        booksReserved = LivreDAO.getBooksReservedByUser(Session.getInstance().getAdherent().getId());

        LocalDate today = LocalDate.now();
        int daysAllowed = 30;

        booksReserved.sort(
                Comparator.comparingLong(lr -> calculerJoursRestants(lr, today, daysAllowed))
        );

        if (booksReserved.isEmpty()) {
            genreContainer.getChildren().clear();
            Label noBooksLabel = new Label("Vous n'avez aucun livre emprunté pour le moment.");
            noBooksLabel.getStyleClass().add("no-books-label");
            genreContainer.add(noBooksLabel, 0, 0);
            return;
        }

        loadBookCards(booksReserved);

    }

    private void loadBookCards(List<LivreReserved> livresReserved) {
        genreContainer.getChildren().clear();

        int COLUMNS_PER_ROW = 2;
        int column = 0;
        int row = 0;

        for (LivreReserved livreReserved : livresReserved) {
            HBox bookCard = createBookCard(livreReserved);
            genreContainer.add(bookCard, column, row);

            column++;
            if (column == COLUMNS_PER_ROW) {
                column = 0;
                row++;
            }
        }
    }

    private HBox createBookCard(LivreReserved livreReserved) {

        Livre livre = livreReserved.getLivre();
        Reservation reservation = livreReserved.getReservation();

        LocalDate dateReservation = reservation.getDateReservation().toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateReservationFormatted = dateReservation.format(formatter);

        LocalDate today = LocalDate.now();
        int daysAllowed = 30;
        long joursRestants = calculerJoursRestants(livreReserved, today, daysAllowed);

        VBox infoContainer = new VBox(5);

        infoContainer.getChildren().addAll(
                new Label(livre.getTitre()) {{ getStyleClass().add("book-title"); }},
                new Label(livre.getAuteurPrenom() + " " + livre.getAuteurNom()) {{ getStyleClass().add("book-author"); }},
                new Label(String.valueOf(livre.getDateParution())) {{ getStyleClass().add("book-year"); }},
                new Label("Livre emprunté le " + dateReservationFormatted) {{ getStyleClass().add("book-reservation-date"); }},
                new Label(
                        (joursRestants > 1)
                                ? "📅  À rendre dans " + joursRestants + " jours"
                                : (joursRestants == 1)
                                    ? "📅  À rendre demain"
                                    : (joursRestants == 0)
                                        ? "📅  À rendre aujourd'hui"
                                        : (joursRestants == -1)
                                            ? "⚠️  En retard depuis hier"
                                            : "⚠️  En retard de " + Math.abs(joursRestants) + " jours"
                ) {{
                    getStyleClass().add("book-days-status");
                    if (joursRestants >= 0) {
                        getStyleClass().add("book-days-left");
                    } else {
                        getStyleClass().add("book-days-late");
                    }

                }}

        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button returnBookButton = new Button("Rendre le livre");
        returnBookButton.getStyleClass().add("book-button");
        returnBookButton.setOnAction(event -> handleReturnBookButton(livre));


        HBox card = new HBox(20);
        card.getStyleClass().add("book-card");
        card.getChildren().addAll(infoContainer, spacer, returnBookButton);
        card.setStyle("-fx-alignment: CENTER_LEFT;");

        return card;
    }

    private long calculerJoursRestants(LivreReserved lr, LocalDate today, int daysAllowed) {
        LocalDate dateReservation = lr.getReservation().getDateReservation().toLocalDate();
        LocalDate dateLimite = dateReservation.plusDays(daysAllowed);
        return ChronoUnit.DAYS.between(today, dateLimite);
    }

    @FXML
    private void handleReturnBookButton(Livre livre) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer le retour");
        alert.setHeaderText("Rendre ce livre ?");
        alert.setContentText("Souhaitez-vous vraiment rendre \""+ livre.getTitre() + "\" ?");

        ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                boolean success = ReservationDAO.rendreLivre(Session.getInstance().getAdherent().getId(), livre.getId());

                if (!success) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Une erreur est survenue lors du retour du livre. Veuillez réessayer plus tard.");
                    errorAlert.showAndWait();
                    return;
                }

                livre.setDisponibilite(1);

                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setTitle("Retour réussi");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Le livre \"" + livre.getTitre() + "\" a été rendu avec succès !");
                confirmation.showAndWait();

                initialize();
            }
        });

    }

    private void loadUserInfo() {
        Adherent adherent = Session.getInstance().getAdherent();

        inputEmail.setText(adherent.getEmail());
        inputNumber.setText("#" + adherent.getNumAdherent());
        inputLastName.setText(adherent.getNom());
        inputFirstName.setText(adherent.getPrenom());
        inputAddress.setText(adherent.getAdresse());
        inputPostalCode.setText(adherent.getCP());
        inputCity.setText(adherent.getVille());
    }

    @FXML
    private void handleChangeInfoButton() {

        if (inputLastName.getText().isEmpty() ||
            inputFirstName.getText().isEmpty() ||
            inputAddress.getText().isEmpty() ||
            inputPostalCode.getText().isEmpty() ||
            inputCity.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
            return;
        }

        if (inputLastName.getText().length() > 60 || inputFirstName.getText().length() > 60) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le nom et le prénom ne doivent pas dépasser 60 caractères.");
            alert.showAndWait();
            return;
        }

        if (inputAddress.getText().length() > 100) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("L'adresse ne doit pas dépasser 100 caractères.");
            alert.showAndWait();
            return;
        }

        if (inputPostalCode.getText().length() > 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le code postal ne doit pas dépasser 10 caractères.");
            alert.showAndWait();
            return;
        }

        if (inputCity.getText().length() > 50) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La ville ne doit pas dépasser 50 caractères.");
            alert.showAndWait();
            return;
        }

        Adherent adherent = Session.getInstance().getAdherent();

        adherent.setNom(inputLastName.getText());
        adherent.setPrenom(inputFirstName.getText());
        adherent.setAdresse(inputAddress.getText());
        adherent.setCP(inputPostalCode.getText());
        adherent.setVille(inputCity.getText());

        boolean success = AdherentDAO.updateAdherentInfo(
                adherent.getId(),
                adherent.getNom(),
                adherent.getPrenom(),
                adherent.getAdresse(),
                adherent.getCP(),
                adherent.getVille()
        );

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la mise à jour des informations. Veuillez réessayer plus tard.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Vos informations ont été mises à jour avec succès.");
        alert.showAndWait();
    }

    @FXML
    private void handleChangePasswordButton() {

        if (inputOldPassword.getText().isEmpty() ||
            inputNewPassword.getText().isEmpty() ||
            inputConfirmPassword.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs du mot de passe.");
            alert.showAndWait();
            return;
        }

        Adherent adherent = Session.getInstance().getAdherent();

        if (!AdherentDAO.verifyPasswordById(adherent.getId(), inputOldPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("L'ancien mot de passe est incorrect.");
            alert.showAndWait();
            return;
        }

        if (!inputNewPassword.getText().equals(inputConfirmPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Le nouveau mot de passe et la confirmation ne correspondent pas.");
            alert.showAndWait();
            return;
        }

        boolean success = AdherentDAO.updatePasswordById(adherent.getId(), inputNewPassword.getText());

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la mise à jour du mot de passe. Veuillez réessayer plus tard.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Votre mot de passe a été mis à jour avec succès.");
        alert.showAndWait();

        inputOldPassword.clear();
        inputNewPassword.clear();
        inputConfirmPassword.clear();
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("LibriGo - Accueil");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
