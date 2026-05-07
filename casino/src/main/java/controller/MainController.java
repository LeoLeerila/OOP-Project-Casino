package controller;
import javafx.application.Platform;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.framework.SimuUpdateEvent;
import simu.model.Casino;
import simu.model.MyEngine;
import simu.model.NPC;
import simu.model.game.GameAbstract;
import simu.model.game.Blackjack;
import simu.model.Save;
import simu.framework.Clock;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import simu.model.game.Poker;
import view.ISimulatorUI;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.animation.AnimationTimer;

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
    private Thread casinoThread;

    //THREAD SAFE QUEUES FOR UPDATES
    private final ConcurrentLinkedQueue<SimuUpdateEvent> updateQueue = new ConcurrentLinkedQueue<>();
    //https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html
    //should help with processing updates
    private AnimationTimer updateTimer;


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
        casinoThread = new Thread(casino);
        casinoThread.start();

    }
    public void createPlayer(){ //this mäybe needs to be thread safe
        NPC player = casino.addPlayer(new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, casino.getGames()));
        queueUpdate(new SimuUpdateEvent(
                SimuUpdateEvent.Type.PLAYER_ADDED, player
        ));
    }
    public void timeAdvance(){
        if(casino.getIsPaused()){//move it here because why not
            logEvent("Simulator moving forward");
            casino.resumeCopy();
        }
        CopyOnWriteArrayList<NPC> PlayersThatLeft = new CopyOnWriteArrayList<NPC>(casino.getPlayersThatLeft());
        if(!PlayersThatLeft.isEmpty()){
            for (NPC player : PlayersThatLeft){
                queueUpdate(new SimuUpdateEvent(
                        SimuUpdateEvent.Type.PLAYER_REMOVED, player
                ));
            }
            casino.clearPlayersThatLeft();
            queueUpdate(new SimuUpdateEvent(
                    SimuUpdateEvent.Type.STATISTICS_UPDATE, null
            ));
            //Save statistics
            Save.saveSimulation(Clock.getInstance().getTime(), casino.getTotalRevenue(), casino.getTotalLoaned(), casino.getCurrentPlayers().size(), casino.getCurrentPlayers(), casino.getGames().size(), casino.getGames());
        }

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
        GameAbstract poker = new Poker(5, 40, 1.6, 0.5, 20);
        GameAbstract blackjack = new Blackjack(4, 10, 2, 0.3, 10);
        casino.addGame(blackjack);
        casino.addGame(poker);
        displayStatistics();
        StartBtn.setOnAction(e -> startSimulation());
        PauseBtn.setOnAction(e -> pauseSimulation());
        ResetBtn.setOnAction(e -> resetSimulation());
        AddPlayerBtn.setOnAction(e -> createPlayer());

        SimuSpeed.valueProperty().addListener((observable, old, newVal) -> {
            adjustSpeed(newVal.doubleValue());
        });
        SimuSpeed.setMin(0.0);
        SimuSpeed.setMax(10.0);
        //default
        SimuSpeed.setValue(5.0);
        startUIUpdate();
    }

    private void adjustSpeed(double sliderVal){
        if (engine != null) {
            double delayMS = 50.0 * Math.pow(2.0, (10.0 - sliderVal) / 2.0);
            long newDelay = Math.round(delayMS);
            engine.setDelay(newDelay);
        }
    }

    private void startUIUpdate() {
        updateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                SimuUpdateEvent event;
                while((event = updateQueue.poll()) != null) {
                    switch (event.getType()){
                        case PLAYER_ADDED:
                            logEvent("Player added with " + ((NPC) event.getData()).getMoney() + "€ and a preference for "+ ((NPC) event.getData()).getGamePreference());
                            break;
                        case PLAYER_REMOVED:
                            logEvent("Player removed with " + ((NPC) event.getData()).getMoney() + "€");
                            break;
                        case GAME_STARTED:
                            logEvent("Game started: " + ((GameAbstract) event.getData()).getTotalPlayers() + " players.");
                            break;
                        case GAME_ENDED:
                            logEvent("Game ended in: " + ((GameAbstract) event.getData()).getGameTime() + " time units.");
                            break;
                        case STATISTICS_UPDATE:
                            displayStatistics();
                            break;
                        case TIME_ADVANCED:
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        updateTimer.start();
    }


    public void queueUpdate(SimuUpdateEvent event) {
        updateQueue.offer(event);
        //initialize statistics save file
        Save.initialSaveSimulation();

    }

    public void pauseSimulation(){
        isSimulationPaused = !isSimulationPaused;
        if(engine != null) {
            engine.setPaused(isSimulationPaused);
        }
    }
    public void resetSimulation(){
        if (updateTimer != null){
            updateTimer.stop();
        }
        if(engine != null) {
            engine.setReset(true);
            ((Thread) engine).interrupt();
            Clock.getInstance().setTime(0);
            engine = new MyEngine(this);
            ui.getVisualisation().clearDisplay();
            isSimulationPaused = false;
            simulationThread = null;
        }
        updateQueue.clear();
        if(updateTimer !=null){
            updateTimer.start();
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

    public void displayStatistics(){
        Label fillerText = new Label("Statistics and relevant data: ");
        Label totalRevenue = new Label("Total Revenue: " + casino.getTotalRevenue() + "€");
        Label totalLoaned = new Label("Total loaned: " + casino.getTotalLoaned() + "€");
        Label totalPlayers = new Label("Total players: " + casino.getTotalPlayers());
        Label currentPlayers = new Label("Current players: " + casino.getCurrentPlayers().size());
        StatisticsContainer.getChildren().clear();
        StatisticsContainer.getChildren().addAll(fillerText, totalRevenue, totalLoaned, totalPlayers, currentPlayers);
    }
}
