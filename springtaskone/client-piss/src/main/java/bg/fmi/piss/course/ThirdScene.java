package bg.fmi.piss.course;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bg.fmi.piss.course.models.Drive;
import bg.fmi.piss.course.models.RideRequest;
import bg.fmi.piss.course.models.User;
import ch.qos.logback.core.net.server.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
public class ThirdScene {

    @FXML
    private TableView<RideRequestTable> rides;
    @FXML
    private TableColumn<RideRequestTable, String> passengerCell;
    @FXML
    private TableColumn<RideRequestTable, String> startPointCell;
    @FXML
    private TableColumn<RideRequestTable, String> endPointCell;
    @FXML
    private TableColumn<RideRequestTable, String> passengersCell;
    @FXML
    private TextField currentPassenger;
    @FXML
    private TextField currentStartPoint;
    @FXML
    private TextField currentEndPoint;
    @FXML
    private TextField currentPassengers;

    private WebClient webClient;
    private User currentUser;
    private RideRequest currentRideRequestProposed;

    public ThirdScene(WebClient webClient, User currentUser) {
        this.webClient = webClient;
        this.currentUser = currentUser;
    }

    public void proposeRide() {
        RideRequestTable selected = rides.getSelectionModel().getSelectedItem();

        if(selected == null) {
            return;
        }

        TextInputDialog alert = new TextInputDialog();
        alert.getEditor().clear();
        alert.setTitle("Confirm ride request");
        alert.setHeaderText("Propose a price to passenger:" + selected.getPassenger());
        Optional<String> result = alert.showAndWait();

        if(result.isPresent()) {
            Drive drive = new Drive(selected.getRequest(), Double.parseDouble(alert.getResult()));

            ClientResponse response = webClient.post()
                    .uri("/drive")
                    .body(BodyInserters.fromObject(drive))
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .exchange()
                    .block();

            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Successfully");
            alert1.setHeaderText("Successfully proposed a ride with passenger" + selected.getPassenger());
            alert1.showAndWait();
        }
    }

    public void changeDriver(ActionEvent event) throws IOException {
        Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();
        stage.close();

        FXMLLoader loaderEngine = new FXMLLoader(getClass().getResource("/secondScene.fxml"));
        loaderEngine.setController(new ThirdScene(webClient, currentUser));
        stage.setTitle("Passenger scene");
        stage.setScene(new Scene(loaderEngine.load()));
        stage.sizeToScene();

        stage.show();
    }

    public void refreshListDriver() {
        List<RideRequest> rideRequests = webClient.get()
                .uri("/ride-requests")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RideRequest>>() {
                })
                .block();

        ObservableList<RideRequestTable> data = FXCollections.observableArrayList();
        for (RideRequest rideRequest : rideRequests) {
            data.add(new RideRequestTable(rideRequest.getId(),
                    rideRequest.getPassenger().getEmail(),
                    rideRequest.getStartPoint(),
                    rideRequest.getEndPoint(),
                    rideRequest.getPassengers().toString(),
                    rideRequest));
        }

        passengerCell.setCellValueFactory(new PropertyValueFactory<>("passenger"));
        startPointCell.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        endPointCell.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        passengersCell.setCellValueFactory(new PropertyValueFactory<>("passengers"));

        rides.setItems(data);
        rides.refresh();

    }

    public void refreshProposed() {

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RideRequestTable {
        private String id;
        private String passenger;
        private String startPoint;
        private String endPoint;
        private String passengers;
        private RideRequest request;
    }
}
