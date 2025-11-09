package com.librigo.Controller;

import com.librigo.Model.Livre;
import com.librigo.Model.ReservationDAO;
import com.librigo.Model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LivreController {

    Livre livre;

    @FXML private Label bookTitle;
    @FXML private Label bookISBN;
    @FXML private Label bookAuthor;
    @FXML private Label bookGenre;
    @FXML private Label bookYear;
    @FXML private Label bookAvailability;
    @FXML private TextArea bookDescription;
    @FXML private Button borrowButton;
    @FXML private Button backButton;

    @FXML private void initialize() {
    }

    public void setLivre(Livre livre) {
        this.livre = livre;

        bookTitle.setText("📖  " + livre.getTitre());
        bookISBN.setText(livre.getISBN());
        bookAuthor.setText(livre.getAuteurPrenom() + " "+ livre.getAuteurNom());
        bookGenre.setText(livre.getGenreNom());
        bookYear.setText(livre.getDateParution());
        bookDescription.setText(livre.getDescription());
        if (livre.getDisponibilite() == 1) {
            bookAvailability.setText("Disponible ✅");
            bookAvailability.getStyleClass().add("book-available");
            borrowButton.setDisable(false);
        } else {
            bookAvailability.setText("Indisponible ❌");
            bookAvailability.getStyleClass().add("book-unavailable");
            borrowButton.setDisable(true);
        }

    }

    @FXML
    private void handleBorrowButton() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer l'emprunt");
        alert.setHeaderText("Emprunter ce livre ?");
        alert.setContentText("Souhaitez-vous vraiment emprunter \"" + livre.getTitre() + "\" ?");

        ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == yesButton) {
                boolean success = ReservationDAO.emprunterLivre(Session.getInstance().getAdherent().getId(), livre.getId());

                if (!success) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur d'emprunt");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Une erreur est survenue lors de l'emprunt. Veuillez réessayer plus tard.");
                    errorAlert.showAndWait();
                    return;
                }

                livre.setDisponibilite(0);

                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setTitle("Emprunt confirmé");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Vous avez emprunté \"" + livre.getTitre() + "\" avec succès !");
                confirmation.showAndWait();

                bookAvailability.setText("Indisponible ❌");
                bookAvailability.getStyleClass().removeAll("book-available");
                bookAvailability.getStyleClass().add("book-unavailable");
                borrowButton.setDisable(true);
            }
        });
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
