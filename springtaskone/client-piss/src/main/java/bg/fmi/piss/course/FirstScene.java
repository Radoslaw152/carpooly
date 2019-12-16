package bg.fmi.piss.course;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import bg.fmi.piss.course.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FirstScene {

    @FXML
    private TextField emailRegister;
    @FXML
    private TextField passwordRegister;
    @FXML
    private TextField nameRegister;
    @FXML
    private TextField surnameRegister;

    @FXML
    private TextField emailLogin;
    @FXML
    private TextField passwordLogin;

    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;

    private WebClient webClient;

    public FirstScene() {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/api")
                .defaultHeader(HttpHeaders.ACCEPT, "text/html," +
                        "application/xhtml+xml," +
                        "application/xml;" +
                        "q=0.9," +
                        "image/webp," +
                        "image/apng,*/*;q=0.8")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
    }

    public void logIn(ActionEvent event) throws IOException {
        ClientResponse response = webClient.post()
                .uri("/login")
                .headers(httpHeaders -> httpHeaders.setBasicAuth(emailLogin.getText(),
                        passwordLogin.getText()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .block();

        if (response.rawStatusCode() < 200 || response.rawStatusCode() > 299) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect login. Please, try again.");
            alert.showAndWait();
        } else {
            webClient = webClient.mutate().defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth(emailLogin.getText(),
                    passwordLogin.getText())).build();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Successfully logged in.");
            alert.showAndWait();

            Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();
            stage.close();

            User user = response.bodyToMono(User.class).block();
            String nextScene = "/secondScene.fxml";
            Object scene = new SecondScene(webClient, user);
//            if (user.getRole().equals("driver")) {
//                nextScene = "/thirdScene.fxml";
//                scene = new ThirdScene(webClient, user);
//            }

            FXMLLoader loaderEngine = new FXMLLoader(getClass().getResource(nextScene));
            loaderEngine.setController(scene);
            stage.setTitle("Passenger scene");
            stage.setScene(new Scene(loaderEngine.load()));
            stage.sizeToScene();

            stage.show();
        }
    }

    public void register() {
        User user = User.builder()
                .email(emailRegister.getText())
                .name(nameRegister.getText())
                .surname(surnameRegister.getText())
                .password(passwordRegister.getText())
                .build();

        ClientResponse response = webClient.post()
                .uri("/register")
                .body(BodyInserters.fromObject(user))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .block();

        if (response.rawStatusCode() < 200 || response.rawStatusCode() > 299) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This user exist. Please, try again.");
            alert.showAndWait();

        } else {
            user = response.bodyToMono(User.class).block();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Successfully registered user with email: " + user.getEmail());
            alert.showAndWait();
        }
    }
}
