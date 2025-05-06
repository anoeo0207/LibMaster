package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import com.example.libmaster.Models.Person.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class MemberController {

    private static final String URL = "jdbc:mysql://localhost:3306/LibMasterServer";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, String> idColumn;
    @FXML
    private TableColumn<Member, String> nameColumn;
    @FXML
    private TableColumn<Member, String> genderColumn;
    @FXML
    private TableColumn<Member, String> dateOfBirthColumn;
    @FXML
    private TableColumn<Member, String> phoneColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;
    @FXML
    private TextField searchField;

    @FXML
    private void handleAddMemberButton(ActionEvent event) throws IOException {
        Main.changeScene("addMember.fxml");
    }

    private void deleteMemberFromDatabase(int id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member with ID " + id + " deleted.");
                fetchMembers("");
            } else {
                System.out.println("No member deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addContextMenuToTable() {
        memberTable.setRowFactory(tv -> {
            TableRow<Member> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem viewItem = new MenuItem("View");
            viewItem.setOnAction(event -> {
                Member selectedMember = row.getItem();
                if (selectedMember != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/libmaster/memberInfo.fxml"));
                        Parent root = loader.load();

                        MemberInfoController controller = loader.getController();
                        controller.setMember(selectedMember);

                        Stage currentStage = (Stage) memberTable.getScene().getWindow();
                        currentStage.setScene(new Scene(root));
                        currentStage.setTitle("LibMaster");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                Member selectedMember = row.getItem();
                if (selectedMember != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Confirmation");
                    alert.setHeaderText("Are you sure you want to delete this member?");
                    alert.setContentText("Member: " + selectedMember.getName());
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteMemberFromDatabase(selectedMember.getId());
                        }
                    });
                }
            });

            contextMenu.getItems().addAll(viewItem, deleteItem);
            row.setContextMenu(contextMenu);

            return row;
        });
    }

    public void fetchMembers(String keyword) {
        ObservableList<Member> members = FXCollections.observableArrayList();

        String sql = "SELECT id, name, gender, date_of_birth, phone, email FROM members WHERE id LIKE ? OR name LIKE ? OR phone LIKE ? OR email LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchQuery = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, searchQuery);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String dob = rs.getString("date_of_birth");
                String phone = rs.getString("phone");
                String email = rs.getString("email");

                members.add(new Member(id, name, dob, gender, phone, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        memberTable.setItems(members);
    }

    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        genderColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
        dateOfBirthColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateOfBirth()));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        searchField.setOnAction(event -> {
            String keyword = searchField.getText().trim();
            fetchMembers(keyword.isEmpty() ? "" : keyword);
        });

        addContextMenuToTable();
        fetchMembers("");
    }
}
