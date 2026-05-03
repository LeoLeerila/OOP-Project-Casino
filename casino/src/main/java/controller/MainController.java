package controller;
import casino.Casino;
import game.GameAbstract;
import game.Blackjack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {
    //Huom: kuvakaappauksen perusteella olevat muuttujat
    //Käyttäjän napit
    @FXML
    private Button StartBtn;
    @FXML
    private Button PauseBtn;
    @FXML
    private Button ResetBtn;
    @FXML
    private Slider SimuSpeed;
    @FXML
    private Button AddPlayerBtn;

    //Containers
    @FXML
    private VBox GameTableContainer;
    @FXML
    private VBox GameTableDetailContainer;
    @FXML
    private VBox StatisticsContainer;
    @FXML
    private VBox EventlogContainer;

    @FXML
    public void initialize() {
        //Initialize casino and other necessary variables
        Casino casino = new Casino();
        GameAbstract blackjack = new Blackjack(4, 10, 1.5, 0.3, 10);
        //Set up event handlers for buttons
        /*
        StartBtn.setOnAction(e -> startSimulation());
        PauseBtn.setOnAction(e -> pauseSimulation());
        ResetBtn.setOnAction(e -> resetSimulation());
        AddPlayerBtn.setOnAction(e -> casino.addPlayer());
        */
    }

    public void displayGameTable(){
        //Get game tables (loop, or just manually add all GameAbstract stuff) ->
            //New Vbox/hbox -> Style new container
            //Add some game table info to vbox/hbox
            //GameTableContainer.getChildren().add(newContainer);
    }
    //trigger based
    public void displayGameTableDetails(){
        //GameTableDetailContainer.getChildren().clear();
        //Get clicked game table (ex. clickedBlackjack) -> find match (loop?) ->
            //New Vbox/hbox -> Style new container
            //Add all game table info to vbox/hbox
            //Add content manipulation buttons
            //   (Ex.) Button winChanceBtn = new Button();
            //      -> winChanceBtn.setOnAction(e -> clickedBlackjack.setWinChance(valueFromAField));
            //   (...And remaining buttons)
            //GameTableContainer.getChildren().add(newContainer);
    }

    //call in a simulation loop to maintain constant data updating for user.
    public void displayStatistics(){
        //StatisticsContainer.getChildren().clear();
        //String somevar = casino.getTotalRevenue() + "€";
        //Label totalRevenueLabel = new Label(somevar);
        //StatisticsContainer.getChildren().add(totalRevenueLabel);
        //(...repeat a shite lot for other relevant stats)
    }

    public void EventlogUpdater(){
        //No idea how to do this, but basically just add new events to the EventlogContainer as they happen in the casino.
    }
}
