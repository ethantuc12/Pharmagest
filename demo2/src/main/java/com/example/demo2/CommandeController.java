package com.example.demo2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class CommandeController {

    @FXML
    private TableView<Commande> CommandeTable;

    @FXML
    private TableColumn<Commande, Integer> idmedicament;

    @FXML
    private TableColumn<Commande, String> nom;

    @FXML
    private TableColumn<Commande, String> fournisseur;

    @FXML
    private TableColumn<Commande, Integer> quantiteenstock;

    @FXML
    private TableColumn<Commande, Integer> quantitemincommande;

    @FXML
    private TableColumn<Commande, Integer> quantitemaxstock;
    @FXML
    private TableColumn<Commande, Integer> seuildecommande;

    @FXML
    private TableColumn<Commande, Double> prixunitaire;
    @FXML
    private Label labelquantiteenstock;

    @FXML
    private Label labelquantitemaxstock;

    @FXML
    private TextField quantitecommande;

    public void initialize() {
        // Initialize columns
        idmedicament.setCellValueFactory(cellData -> cellData.getValue().idmedicamentProperty().asObject());
        nom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        fournisseur.setCellValueFactory(cellData -> cellData.getValue().fournisseurProperty());
        quantiteenstock.setCellValueFactory(cellData -> cellData.getValue().quantiteenstockProperty().asObject());
        quantitemincommande.setCellValueFactory(cellData -> cellData.getValue().quantitemincommandeProperty().asObject());
        quantitemaxstock.setCellValueFactory(cellData -> cellData.getValue().quantitemaxstockProperty().asObject());
        seuildecommande.setCellValueFactory(cellData -> cellData.getValue().seuildecommandeProperty().asObject());
        prixunitaire.setCellValueFactory(cellData -> cellData.getValue().prixunitaireProperty().asObject());

        // Populate the table with data
        try {
            populateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Add a listener to the TableView selection
        CommandeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update the labels and text field with the selected item's values
                labelquantiteenstock.setText(newValue.getQuantiteenstock() + " €");
                labelquantitemaxstock.setText(newValue.getQuantitemaxstock() + " €");
                quantitecommande.setText(String.valueOf(newValue.getQuantitemincommande()));
            }
        });
    }

    private void populateTable() throws SQLException, ClassNotFoundException {
        // Establish database connection using DBUtil
        Connection conn = DBUtil.dbConnect();

        if (conn != null) {
            try {
                // Fetch data from the database
                String query = "SELECT * FROM medicament";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                // Populate data into ObservableList
                ObservableList<Commande> commandes = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    Commande commande = new Commande(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("fournisseur"),
                            resultSet.getInt("quantiteenstock"),
                            resultSet.getInt("quantitemincommande"),
                            resultSet.getInt("quantitemaxstock"),
                            resultSet.getInt("seuildecommande"),
                            resultSet.getDouble("prixcommande")
                    );
                    commandes.add(commande);
                }

                // Set items to the table
                CommandeTable.setItems(commandes);

                // Close resources
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the connection
                conn.close();
            }
        }
    }
}
