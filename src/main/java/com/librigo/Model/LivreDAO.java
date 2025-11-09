package com.librigo.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    public static List<Livre> getAllLivres() {

        List<Livre> livres = new ArrayList<>();

        String query = "SELECT l.id, " +
                "l.ISBN, " +
                "l.titre, " +
                "l.auteur_id, " +
                "a.nom, " +
                "a.prenom, " +
                "l.genre_id, " +
                "g.nom, " +
                "l.date_parution, " +
                "l.description, " +
                "l.disponible " +
                "FROM LIVRE l " +
                "JOIN AUTEUR a ON l.auteur_id = a.id " +
                "JOIN GENRE g ON l.genre_id = g.id";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
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

    public static List<LivreReserved> getAllLivresWithReservationStatus() {
        List<LivreReserved> livres = new ArrayList<>();

        String query = "SELECT l.id, " +
                "l.ISBN, " +
                "l.titre, " +
                "l.auteur_id, " +
                "a.nom, " +
                "a.prenom, " +
                "l.genre_id, " +
                "g.nom, " +
                "l.date_parution, " +
                "l.description, " +
                "l.disponible, " +
                "r.id, " +
                "r.adherent_id, " +
                "r.date_reservation, " +
                "r.date_retour " +
                "FROM LIVRE l " +
                "JOIN AUTEUR a ON l.auteur_id = a.id " +
                "JOIN GENRE g ON l.genre_id = g.id " +
                "LEFT JOIN RESERVATION r ON r.livre_id = l.id AND r.date_retour IS NULL";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
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

                Reservation reservation = null;
                if (rs.getInt(12) != 0) {
                    reservation = new Reservation(
                            rs.getInt(12),
                            rs.getInt(13),
                            rs.getInt(1),
                            rs.getDate(14),
                            rs.getDate(15)
                    );
                }

                livres.add(new LivreReserved(livre, reservation));
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return livres;
    }

    public static List<LivreReserved> getBooksReservedByUser(int adherentId) {

        List<LivreReserved> livresReserved = new ArrayList<>();

        String query = "SELECT l.id, " +
                "l.ISBN, " +
                "l.titre, " +
                "l.auteur_id, " +
                "a.nom, " +
                "a.prenom, " +
                "l.genre_id, " +
                "g.nom, " +
                "l.date_parution, " +
                "l.description, " +
                "l.disponible, " +
                "r.id, " +
                "r.date_reservation, " +
                "r.date_retour " +
                "FROM LIVRE l " +
                "JOIN AUTEUR a ON l.auteur_id = a.id " +
                "JOIN GENRE g ON l.genre_id = g.id " +
                "JOIN RESERVATION r ON r.livre_id = l.id " +
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

                Reservation reservation = new Reservation(
                        rs.getInt(12),
                        adherentId,
                        rs.getInt(1),
                        rs.getDate(13),
                        rs.getDate(14)
                );

                livresReserved.add(new LivreReserved(livre, reservation));
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return livresReserved;
    }

    public List<Auteur> getAllAuteurs() {

        List<Auteur> auteurs = new ArrayList<>();

        String query = "SELECT id, nom, prenom FROM AUTEUR";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                auteurs.add(new Auteur(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return auteurs;
    }

    public List<Genre> getAllGenres() {

        List<Genre> genres = new ArrayList<>();

        String query = "SELECT id, nom FROM GENRE";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                genres.add(new Genre(rs.getInt(1), rs.getString(2)));
            }
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return genres;
    }

    public void updateLivreDisponibilite(int livreId, int disponibilite) {
        String query = "UPDATE LIVRE SET disponible = ? WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, disponibilite);
            stmt.setInt(2, livreId);
            int rowsAffected = stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
    }

    public boolean createLivre(String isbn, String titre, int auteurId, int genreId, String dateParution, String description) {
        int disponible = 1;
        String query = "INSERT INTO LIVRE (ISBN, titre, auteur_id, genre_id, date_parution, description, disponible) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, isbn);
            stmt.setString(2, titre);
            stmt.setInt(3, auteurId);
            stmt.setInt(4, genreId);
            stmt.setString(5, dateParution);
            stmt.setString(6, description);
            stmt.setInt(7, disponible);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteLivreById(int livreId) {
        String query = "DELETE FROM LIVRE WHERE id = ?";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, livreId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public boolean createAuteur(String nom, String prenom) {
        String query = "INSERT INTO AUTEUR (nom, prenom) VALUES (?, ?)";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

    public boolean createGenre(String nom) {
        String query = "INSERT INTO GENRE (nom) VALUES (?)";

        try (Connection conn = ConnectionSQL.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nom);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Erreur BDD : " + e.getMessage());
        }
        return false;
    }

}
