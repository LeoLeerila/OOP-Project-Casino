package controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.framework.SimuUpdateEvent;
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
import simu.model.game.Poker;
import view.ISimulatorUI;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

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

    //THREAD SAFE QUEUES FOR UPDATES
    private final ConcurrentLinkedQueue<SimuUpdateEvent> updateQueue = new ConcurrentLinkedQueue<>();
    //https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html
    //should help with processing updates
    private AnimationTimer updateTimer;


    public MainController() {
    }

    public void setUI(ISimulatorUI ui) {
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

    public void createPlayer() {
        NPC player = casino.addPlayer(new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, casino.getGames()));
        queueUpdate(new SimuUpdateEvent(
                SimuUpdateEvent.Type.PLAYER_ADDED, player
        ));
    }

    public void timeAdvance() {
        casino.startGames();
        casino.updateGameTimes(-1 * casino.getLowestGameTime());
        casino.handleFreePlayers();
        casino.calculateRevenueChange();
        casino.calculateLoanedChange();
        logEvent("Revee: " + casino.getTotalRevenue());

        queueUpdate(new SimuUpdateEvent(
                SimuUpdateEvent.Type.STATISTICS_UPDATE, null
        ));
    }

    @FXML
    public Label EndingTime;

    @Override
    public void decreaseSpeed() { // hidastetaan moottorisäiettä
        engine.setDelay((long) (engine.getDelay() * 1.10));
    }

    @Override
    public void increaseSpeed() { // nopeutetaan moottorisäiettä
        engine.setDelay((long) (engine.getDelay() * 0.9));
    }

    @Override
    public void logEvent(String message) {
        Platform.runLater(() -> EventlogContainer.appendText(message + "\n"));
    }

    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> ui.setEndingTime(time));
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
    private Accordion GameTableAccordion;

    @FXML
    public void initialize() {
        //Initialize simu.model.casino and other necessary variables
        GameAbstract poker = new Poker(5, 40, 20, 0.5, 20);
        GameAbstract blackjack = new Blackjack(4, 10, 1.5, 0.3, 10);
        casino.addGame(blackjack);
        casino.addGame(poker);
        displayStatistics();
        displayGameTable();
        StartBtn.setOnAction(e -> startSimulation());
        PauseBtn.setOnAction(e -> pauseSimulation());
        ResetBtn.setOnAction(e -> resetSimulation());
        AddPlayerBtn.setOnAction(e -> casino.addPlayer(new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, casino.getGames())));
        GameTableAccordion.expandedPaneProperty().addListener((obs, oldPane, newPane) -> {
            GameTableDetailContainer.getChildren().clear();

            if (newPane == null) {
                return;
            }
            GameAbstract selectedGame = (GameAbstract) newPane.getUserData();
            displayGameTableDetails(selectedGame);
        });

        SimuSpeed.valueProperty().addListener((observable, old, newVal) -> {
            adjustSpeed(newVal.doubleValue());
        });
        SimuSpeed.setMin(0.0);
        SimuSpeed.setMax(10.0);
        //default
        SimuSpeed.setValue(5.0);
        startUIUpdate();
    }

    private void adjustSpeed(double sliderVal) {
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
                while ((event = updateQueue.poll()) != null) {
                    switch (event.getType()) {
                        case PLAYER_ADDED:
                            logEvent("Player added with " + ((NPC) event.getData()).getMoney() + "€ and a preference for " + ((NPC) event.getData()).getGamePreference());
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
                            updateGameTable();
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
    }

    public void pauseSimulation() {
        isSimulationPaused = !isSimulationPaused;
        if (engine != null) {
            engine.setPaused(isSimulationPaused);
        }
    }

    public void resetSimulation() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        if (engine != null) {
            engine.setReset(true);
            ((Thread) engine).interrupt();
            Clock.getInstance().setTime(0);
            engine = new MyEngine(this);
            ui.getVisualisation().clearDisplay();
            isSimulationPaused = false;
            simulationThread = null;
        }
        updateQueue.clear();
        if (updateTimer != null) {
            updateTimer.start();
        }
    }

    public void displayGameTable() {
        Accordion accordion = (Accordion) GameTableContainer.getChildren().get(0);
        accordion.getPanes().clear();

        for (GameAbstract game : casino.getGames()) {
            TitledPane pane = new TitledPane();
            pane.setText(game.getType());
            pane.setAnimated(false);
            pane.setUserData(game);
            VBox content = new VBox();
            content.setSpacing(5);
            content.setPadding(new Insets(5));
            content.getChildren().addAll(
                    new Label("Max: " + game.getMaxPlayers()),
                    new Label("Min bet: " + game.getMinBet()),
                    new Label("Win chance: " + game.getWinChance()),
                    new Label("Active: " + game.getCurrentPlayerNum())
            );
            pane.setContent(content);
            accordion.getPanes().add(pane);
        }
    }

    //literally just for that fucking switch case
    public void updateGameTable() {
        Accordion accordion = (Accordion) GameTableContainer.getChildren().get(0);
        for (TitledPane pane : accordion.getPanes()) {
            String gameType = pane.getText();
            for (GameAbstract game : casino.getGames()) {
                if (game.getType().equals(gameType)) {
                    VBox content = (VBox) pane.getContent();
                    ((Label) content.getChildren().get(0)).setText("Max: " + game.getMaxPlayers());
                    ((Label) content.getChildren().get(1)).setText("Min bet: " + game.getMinBet());
                    ((Label) content.getChildren().get(2)).setText("Win chance: " + game.getWinChance());
                    ((Label) content.getChildren().get(3)).setText("Active: " + game.getCurrentPlayerNum());
                }
            }
        }
    }

    //exists in case we add a way for user to add their own gametable (therefore it needs it's own display method)
    public void displayGameTable(String type, int maxplayer, int minbet, int cashout, double winchance, int gametime) {
    }

    private HBox createEditRow(String labelText, String currentValue, Consumer<String> onApply) {
        HBox row = new HBox(8);

        Label label = new Label(labelText);
        TextField field = new TextField(currentValue);
        Button button = new Button("Apply");

        button.setOnAction(e -> onApply.accept(field.getText()));

        row.getChildren().addAll(label, field, button);
        return row;
    }

    //trigger based
    public void displayGameTableDetails(GameAbstract game) {
        GameTableDetailContainer.getChildren().clear();

        Label title = new Label("Selected table: " + game.getType());
        Label status = new Label("Status: " + (game.isGameInProgress() ? "in progress" : "open"));
        Label players = new Label("Players: " + game.getCurrentPlayerNum() + " / " + game.getMaxPlayers());
        Label queue = new Label("Queue: " + game.getPlayerQueueSize());
        Label totalPlayers = new Label("Total players: " + game.getTotalPlayers());
        Label minBet = new Label("Minimum bet: " + game.getMinBet() + "€");
        Label cashOut = new Label("Cash-out multiplier: " + game.getCashOut());
        Label winChance = new Label("Win chance: " + game.getWinChance());
        Label gameTime = new Label("Game time: " + game.getGameTime());
        Label currentTime = new Label("Current game time: " + game.getCurrentGameTime());

        HBox minBetRow = createEditRow(
                "Minimum bet:",
                String.valueOf(game.getMinBet()),
                text -> {
                    int value = Integer.parseInt(text);
                    game.setMinBet(value);
                    displayGameTableDetails(game);
                    updateGameTable();
                }
        );

        HBox cashOutRow = createEditRow(
                "Cash-out multiplier:",
                String.valueOf(game.getCashOut()),
                text -> {
                    int value = Integer.parseInt(text);
                    game.setCashOut(value);
                    displayGameTableDetails(game);
                    updateGameTable();
                }
        );

        HBox winChanceRow = createEditRow(
                "Win chance:",
                String.valueOf(game.getWinChance()),
                text -> {
                    double value = Double.parseDouble(text);
                    game.setWinChance(value);
                    displayGameTableDetails(game);
                    updateGameTable();
                }
        );

        GameTableDetailContainer.getChildren().addAll(
                title,
                status,
                players,
                queue,
                totalPlayers,
                minBet,
                cashOut,
                winChance,
                gameTime,
                currentTime,
                minBetRow,
                cashOutRow,
                winChanceRow

        );
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

    public void displayStatistics() {
        Label fillerText = new Label("Statistics and relevant data: ");
        Label totalRevenue = new Label("Total Revenue: " + casino.getTotalRevenue() + "€");
        Label totalLoaned = new Label("Total loaned: " + casino.getTotalLoaned() + "€");
        Label totalPlayers = new Label("Total players: " + casino.getTotalPlayers());
        StatisticsContainer.getChildren().clear();
        StatisticsContainer.getChildren().addAll(fillerText, totalRevenue, totalLoaned, totalPlayers);
    }
}
