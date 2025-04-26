package com.example.libmaster.Controllers;

import com.example.libmaster.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.libmaster.Models.Members;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class MemberController {

    @FXML
    private void handleAddMemberButton(ActionEvent event) throws IOException {
        Main.changeScene("addMember.fxml");
    }

    @FXML
    private TableView<Members> tableView;

    @FXML
    private TableColumn<Members, Integer> idColumn;

    @FXML
    private TableColumn<Members, String> nameColumn;

    @FXML
    private TableColumn<Members, Integer> bookIssueColumn;

    @FXML
    private TableColumn<Members, String> phoneColumn;


    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        bookIssueColumn.setCellValueFactory(new PropertyValueFactory<>("numBookIssue"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        ObservableList<Members> memberList = FXCollections.observableArrayList();
        memberList.add(new Members(1, "Alice", "0123456789", 2));
        memberList.add(new Members(2, "Bob", "0987654321", 1));
        tableView.setItems(memberList);
    }
}
