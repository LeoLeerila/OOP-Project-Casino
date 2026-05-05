package view;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class simuview extends Application implements ISimulatorUI {
    private double simulationTime = 1000.0;
    private long delay = 500;
    private IVisualisation visualisation = new IVisualisation() {
        @Override
        public void clearDisplay() {}

        @Override
        public void newCustomer() {}
    };

    @FXML
    private Label EndingTime;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/casino_view.fxml"));
        MainController controller = new MainController();
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();
        controller.setUI(this);
        stage.setScene(new Scene(root));
        stage.show();
    }
    @Override
    public double getTime(){
        return simulationTime;
    }
    @Override
    public long getDelay(){
        return delay;
    }
    @Override
    public IVisualisation getVisualisation(){
        return visualisation;
    }
    @Override
    public void setEndingTime(double time){
        if (EndingTime != null){
            EndingTime.setText("Simu ended at: " + time);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
