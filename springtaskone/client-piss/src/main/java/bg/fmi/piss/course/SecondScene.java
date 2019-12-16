package bg.fmi.piss.course;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import bg.fmi.piss.course.models.Drive;
import bg.fmi.piss.course.models.RideRequest;
import bg.fmi.piss.course.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SecondScene implements Initializable {

    private WebClient webClient;
    private User currentUser;

    @FXML
    private TextField startPoint;
    @FXML
    private TextField endPoint;
    @FXML
    private Button requestRideButton;
    @FXML
    private Button refreshButton;
    @FXML
    private ChoiceBox<Integer> passengers;

    @FXML
    private TextField currentStartPoint;
    @FXML
    private TextField currentEndPoint;
    @FXML
    private TextField currentPassengers;
    @FXML
    private TextField currentDriver;
    @FXML
    private TableView<DriveTable> rides;
    @FXML
    private TableColumn<DriveTable, String> driverCell;
    @FXML
    private TableColumn<DriveTable, String> startPointCell;
    @FXML
    private TableColumn<DriveTable, String> endPointCell;
    @FXML
    private TableColumn<DriveTable, String> priceCell;

    private RideRequest currentRide;
    private Drive currentDrive;


    public SecondScene(WebClient webClient, User currentUser) {
        this.webClient = webClient;
        this.currentUser = currentUser;
    }

    public void refreshList() {
        List<Drive> drives = webClient.get()
                .uri("/drive")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .block()
                .bodyToMono(new ParameterizedTypeReference<List<Drive>>() {
                })
                .block();

        ObservableList<DriveTable> data = FXCollections.observableArrayList();
        for (Drive drive : drives) {
            data.add(new DriveTable(drive.getId(),
                    drive.getDriver().getEmail(),
                    drive.getRide().getStartPoint(),
                    drive.getRide().getEndPoint(),
                    drive.getPrice().toString() + " BGN"));
        }

        driverCell.setCellValueFactory(new PropertyValueFactory<>("driver"));
        startPointCell.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        endPointCell.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        priceCell.setCellValueFactory(new PropertyValueFactory<>("price"));

        rides.setItems(data);
        rides.refresh();
    }

    public void requestRide() {
        if (currentRide != null) {
            webClient.delete()
                    .uri("/ride-requests/" + currentRide.getId())
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .exchange()
                    .block();
        }

        RideRequest rideRequest = RideRequest.builder()
                .startPoint(startPoint.getText())
                .endPoint(endPoint.getText())
                .passengers(passengers.getValue())
                .build();

        ClientResponse response = webClient.post()
                .uri("/ride-requests")
                .body(BodyInserters.fromObject(rideRequest))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .block();
        if (!response.statusCode().is2xxSuccessful()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(response.statusCode().getReasonPhrase());
            alert.showAndWait();
        } else {
            RideRequest result = response.bodyToMono(RideRequest.class).block();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Successfully requested a ride.");
            alert.showAndWait();

            currentStartPoint.clear();
            currentStartPoint.insertText(0, result.getStartPoint());

            currentEndPoint.clear();
            currentEndPoint.insertText(0, result.getEndPoint());

            currentPassengers.clear();
            currentPassengers.insertText(0, result.getPassengers().toString());
            currentRide = result;
        }

    }

    public void changeDriver(ActionEvent event) throws IOException {
        if (currentUser.getRole().equals("passenger")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error");
            alert.setHeaderText("You are not a driver");
            alert.showAndWait();
            return;
        }
        Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();
        stage.close();

        FXMLLoader loaderEngine = new FXMLLoader(getClass().getResource("/thirdScene.fxml"));
        loaderEngine.setController(new ThirdScene(webClient, currentUser));
        stage.setTitle("Passenger scene");
        stage.setScene(new Scene(loaderEngine.load()));
        stage.sizeToScene();

        stage.show();
    }

    public void acceptRide() {
        DriveTable selected = rides.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(new ButtonType("Yes"));
        alert.getButtonTypes().add(new ButtonType("No"));
        alert.setTitle("Confirm drive");
        alert.setHeaderText(
                "Are you sure you want to accept ride with driver: " + selected.getDriver());
        alert.showAndWait();

        if (alert.getResult().getText().equals("Yes")) {
            webClient.post()
                    .uri("/drive/" + selected.getId() + "/" + currentRide.getId())
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .exchange();

            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Successfully");
            alert.setHeaderText("Successfully accepted a ride with driver" + selected.getDriver());
            alert.showAndWait();
            currentDriver.clear();
            currentDriver.insertText(0, selected.getDriver());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passengers.getItems().addAll(1, 2, 3, 4, 5, 6, 7);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DriveTable {
        private String id;
        private String driver;
        private String startPoint;
        private String endPoint;
        private String price;
    }
}
