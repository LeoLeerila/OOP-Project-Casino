package controller;
import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.Casino;
import simu.model.MyEngine;
import simu.model.NPC;
import simu.model.game.GameAbstract;
import simu.model.game.Blackjack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import view.ISimulatorUI;

public class MainController implements IControllerVtoM, IControllerMtoV {
    //Huom: kuvakaappauksen perusteella olevat muuttujat
    //Käyttäjän napit
    private Casino casino = new Casino();

    //get money/conversion from elsewhere this data is temp
    private int minNPCMoney = 100;
    private int maxNPCMoney = 10000;
    private int ChipConvesion = 15;

    private IEngine engine;
    private ISimulatorUI ui;
    private Thread simulationThread;
    private boolean isSimulationPaused = false;

    public MainController(){}
    public void setUI(ISimulatorUI ui){
        this.ui = ui;
    }

    @Override
    public void startSimulation() {
        if (simulationThread != null && simulationThread.isAlive()) {
            resetSimulation();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        engine = new MyEngine(this); // new Engine thread is created for every simulation
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());
        ui.getVisualisation().clearDisplay();
        EventlogContainer.clear();
        simulationThread = (Thread) engine;
        simulationThread.start();
    }
    public void createPlayer(){
        NPC player = casino.addPlayer(new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, casino.getGames()));
        logEvent(String.valueOf(player));
    }
    @FXML
    public Label EndingTime;
    @Override
    public void decreaseSpeed() { // hidastetaan moottorisäiettä
        engine.setDelay((long)(engine.getDelay()*1.10));
    }

    @Override
    public void increaseSpeed() { // nopeutetaan moottorisäiettä
        engine.setDelay((long)(engine.getDelay()*0.9));
    }
    @Override
    public void logEvent(String message) {
        Platform.runLater(() -> EventlogContainer.appendText(message + "\n"));
    }
    @Override
    public void showEndTime(double time) {
        Platform.runLater(()->ui.setEndingTime(time));
    }

    @Override
    public void visualiseCustomer() {
        Platform.runLater(() -> ui.getVisualisation().newCustomer());
    }
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
    private TextArea EventlogContainer;

    @FXML
    public void initialize() {
        //Initialize simu.model.casino and other necessary variables

        GameAbstract blackjack = new Blackjack(4, 10, 1.5, 0.3, 10);
        casino.addGame(blackjack);
        displayStatistics();
        StartBtn.setOnAction(e -> startSimulation());
        PauseBtn.setOnAction(e -> pauseSimulation());
        ResetBtn.setOnAction(e -> resetSimulation());
        //Set up event handlers for buttons

        AddPlayerBtn.setOnAction(e -> casino.addPlayer(new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, casino.getGames())));

    }

    public void pauseSimulation(){
        isSimulationPaused = !isSimulationPaused;
        if(engine != null) {
            engine.setPaused(isSimulationPaused);
        }
    }
    public void resetSimulation(){
        if(engine != null) {
            engine.setReset(true);
            ((Thread) engine).interrupt();
            engine = new MyEngine(this);
            ui.getVisualisation().clearDisplay();
            isSimulationPaused = false;
            simulationThread = null;
        }
    }

    public void displayGameTable(){
        //Get simu.model.game tables (loop, or just manually add all GameAbstract stuff) ->
            //New Vbox/hbox -> Style new container
            //Add some simu.model.game table info to vbox/hbox
            //GameTableContainer.getChildren().add(newContainer);
    }
    //trigger based
    public void displayGameTableDetails(){
        //GameTableDetailContainer.getChildren().clear();
        //Get clicked simu.model.game table (ex. clickedBlackjack) -> find match (loop?) ->
            //New Vbox/hbox -> Style new container
            //Add all simu.model.game table info to vbox/hbox
            //Add content manipulation buttons
            //   (Ex.) Button winChanceBtn = new Button();
            //      -> winChanceBtn.setOnAction(e -> clickedBlackjack.setWinChance(valueFromAField));
            //   (...And remaining buttons)
            //GameTableContainer.getChildren().add(newContainer);
    }

    //call in a simulation loop to maintain constant data updating for user.
    public void displayStatistics(){
        Label fillerText = new Label("Statistics and relevant data: ");
        Label totalRevenue = new Label("Total Revenue: " + casino.getTotalRevenue() + "€");
        Label totalLoaned = new Label("Total loaned: " + casino.getTotalLoaned() + "€");
        Label totalPlayers = new Label("Total players: " + casino.getTotalPlayers());
        StatisticsContainer.getChildren().clear();
        StatisticsContainer.getChildren().addAll(fillerText, totalRevenue, totalLoaned, totalPlayers);
        //String somevar = simu.model.casino.getTotalRevenue() + "€";
        //Label totalRevenueLabel = new Label(somevar);
        //StatisticsContainer.getChildren().add(totalRevenueLabel);
        //(...repeat a shite lot for other relevant stats)
    }
}
