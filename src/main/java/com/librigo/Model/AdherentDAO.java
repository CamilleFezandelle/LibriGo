package com.librigo.Model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdherentDAO {

    private static final Random random = new Random();

    public Adherent login(String email, String password) {

        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "SELECT * FROM ADHERENT WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("mot_de_passe");
                if (PasswordUtils.verifyPassword(password, dbPassword)) {
                    return new Adherent(
                            rs.getInt("id"),
                            rs.getString("numero_adherent"),
                            rs.getString("email"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getDate("date_naissance"),
                            rs.getString("adresse"),
                            rs.getString("cp"),
                            rs.getString("ville"),
                            rs.getInt("role")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return null;
    }

    public static boolean verifyPasswordById(int adherentId, String password) {
        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "SELECT mot_de_passe FROM ADHERENT WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adherentId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("mot_de_passe");
                return PasswordUtils.verifyPassword(password, dbPassword);
            }

        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public static boolean updatePasswordById(int adherentId, String password) {

        String hashedPassword = PasswordUtils.hashPassword(password);
        String query = "UPDATE ADHERENT SET mot_de_passe = ? WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, adherentId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM ADHERENT WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public String generateUniqueAdherentNumber() {
        String num;
        do {
            int randomNum = random.nextInt(100000);
            num = String.format("%05d", randomNum);
        } while (adherentExists(num));

        return num;
    }

    private boolean adherentExists(String numeroAdherent) {
        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM ADHERENT WHERE numero_adherent = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, numeroAdherent);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public void registerAdherent(String nom, String prenom, String email, String hashedPassword,
                                 Date dateNaissance, String adresse, String cp, String ville, String numeroAdherent) {

        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "INSERT INTO ADHERENT (numero_adherent, email, mot_de_passe, nom, prenom, date_naissance, adresse, cp, ville, role) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, numeroAdherent);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, nom);
            stmt.setString(5, prenom);
            stmt.setDate(6, dateNaissance);
            stmt.setString(7, adresse);
            stmt.setString(8, cp);
            stmt.setString(9, ville);
            stmt.setInt(10, 0);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePasswordByEmail(String email, String hashedPassword) {
        try (Connection conn = ConnectionSQL.getConnection()) {
            String query = "UPDATE ADHERENT SET mot_de_passe = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, hashedPassword);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
    }

    public static List<Adherent> getAllAdherents() {
        List<Adherent> adherents = new ArrayList<>();

        String query = "SELECT id, numero_adherent, email, nom, prenom, date_naissance, adresse, cp, ville, role FROM ADHERENT";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                adherents.add(new Adherent(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getInt(10)
                ));
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }

        return adherents;
    }

    public static List<Livre> getLivresEmpruntesByAdherent(int adherentId) {
        List<Livre> livres = new ArrayList<>();

        String query = "SELECT l.id, " +
                        "l.ISBN, " +
                        "l.titre, " +
                        "l.auteur_id, " +
                        "a.nom AS auteur_nom, " +
                        "a.prenom AS auteur_prenom, " +
                        "l.genre_id, " +
                        "g.nom AS genre_nom, " +
                        "l.date_parution, " +
                        "l.description, " +
                        "l.disponible, " +
                        "r.id AS reservation_id, " +
                        "r.date_reservation, " +
                        "r.date_retour " +
                        "FROM RESERVATION r " +
                        "JOIN LIVRE l ON r.livre_id = l.id " +
                        "JOIN AUTEUR a ON l.auteur_id = a.id " +
                        "JOIN GENRE g ON l.genre_id = g.id " +
                        "WHERE r.adherent_id = ? " +
                        "AND r.date_retour IS NULL";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adherentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Livre livre = new Livre(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getInt(11)
                );
                livres.add(livre);
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }

        return livres;
    }

    public static boolean updateAdherentInfo(int adherentId, String nom, String prenom, String adresse, String cp, String ville) {

        String query = "UPDATE ADHERENT SET nom = ?, prenom = ?, adresse = ?, cp = ?, ville = ? WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, adresse);
            stmt.setString(4, cp);
            stmt.setString(5, ville);
            stmt.setInt(6, adherentId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteAdherent(int adherentId) {
        String query = "DELETE FROM ADHERENT WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adherentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
            return false;
        }
    }

}
