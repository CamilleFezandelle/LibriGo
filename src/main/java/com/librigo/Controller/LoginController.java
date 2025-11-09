package com.librigo.Controller;

import com.librigo.Model.Adherent;
import com.librigo.Model.AdherentDAO;
import com.librigo.Model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private AdherentDAO adherentDAO = new AdherentDAO();

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    private void handleLoginButton() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        Adherent adherent = adherentDAO.login(email, password);

        if (adherent != null) {
            Session session = Session.getInstance();
            session.createSession(adherent);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/dashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setTitle("LibriGo - Accueil");
                stage.setScene(new javafx.scene.Scene(root, 900, 600));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Erreur lors du chargement de la page.");
            }
        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void handleForgotPasswordLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/lostPassword.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("LibriGo - Mot de passe oublié");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page de récupération de mot de passe.");
        }
    }

    @FXML
    private void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("LibriGo - Inscription");
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement de la page d'inscription.");
        }
    }


}
