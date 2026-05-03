package ui;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import engine.RestaurantEngine;
import fileio.FileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import manager.InventoryManager;
import model.ApplianceType;
import model.Ingredient;
import model.Order;

public class DashboardUI extends Application {

    private final RestaurantEngine engine = new RestaurantEngine();

    // Orders section
    //keep the screen synchronized with the order queue in the engine
    private final ObservableList<String> orderItems = FXCollections.observableArrayList();
    private final ListView<String>       orderList  = new ListView<>(orderItems);

    // Inventory section
    //table-like grid for ingredient quantities and cash
    private final GridPane inventoryGrid = new GridPane();
    private final Label    cashLabel     = new Label();

    // Appliance status section
    private final Label grillStatus   = applianceStatusLabel("Grill");
    private final Label airpotStatus  = applianceStatusLabel("AirPot");
    private final Label ovenStatus    = applianceStatusLabel("Oven");
    private final Label drinkStatus   = applianceStatusLabel("Drink Dispenser");
    private final Label sauceStatus   = applianceStatusLabel("Sauce Dispenser");

    //dropdown selection control
    private final ComboBox<Ingredient> restockDropdown = new ComboBox<>();

    // scrollable log area for messages
    private final TextArea logArea = new TextArea();

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");


    @Override
    public void start(Stage stage) {
        engine.setLogCallback(this::appendLog);
        engine.setRefreshCallback(this::refreshUI);

        VBox ordersPanel  = buildOrdersPanel();
        VBox inventoryBox = buildInventoryPanel();
        VBox restockBox   = buildRestockPanel();
        VBox logBox       = buildLogPanel();

        VBox rightPanel = new VBox(10, inventoryBox, restockBox);
        HBox topRow = new HBox(10, ordersPanel, rightPanel);
        HBox.setHgrow(ordersPanel, Priority.SOMETIMES);
        HBox.setHgrow(rightPanel,  Priority.ALWAYS);
        topRow.setPadding(new Insets(10));

        VBox root = new VBox(10, topRow, logBox);
        VBox.setVgrow(logBox, Priority.ALWAYS);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Scene scene = new Scene(root, 1150, 780);
        stage.setTitle("The Silicon Spatula - Restaurant Tycoon  |  Team 7");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setOnCloseRequest(e -> { engine.stopSimulation(); Platform.exit(); });
        stage.show();

        refreshUI();
        engine.startSimulation();
        appendLog("Silicon Spatula is open! New orders arrive every 2-5 seconds.");
        appendLog("TIP: Different appliances can run simultaneously (e.g. Grill + Drink Dispenser).");
    }

    private VBox buildOrdersPanel() {
        Label title = panelTitle("Orders Queue");

        orderList.setPrefHeight(280);
        orderList.setStyle(
            "-fx-background-color: #16213e; -fx-control-inner-background: #16213e;" +
            "-fx-text-fill: #e0e0e0; -fx-border-color: #0f3460; -fx-border-width: 1;");

        // Appliance status block
        Label statusTitle = new Label("Appliance Status:");
        statusTitle.setTextFill(Color.web("#aaaaaa"));
        statusTitle.setFont(Font.font("Monospaced", 11));
        HBox statusRow1 = new HBox(12, grillStatus, airpotStatus);
        HBox statusRow2 = new HBox(12, ovenStatus,  drinkStatus);
        HBox statusRow3 = new HBox(12, sauceStatus);
        VBox statusBox  = new VBox(3, statusTitle, statusRow1, statusRow2, statusRow3);
        statusBox.setPadding(new Insets(4, 0, 4, 0));

        Button cookBtn = styledButton("Cook Next Order", "#e94560", "#c73652");
        cookBtn.setMaxWidth(Double.MAX_VALUE);
        cookBtn.setOnAction(e -> engine.handleCookNextOrder());

        Button stopBtn = styledButton("Stop", "#f5a623", "#d4841a");
        stopBtn.setMaxWidth(Double.MAX_VALUE);
        stopBtn.setOnAction(e -> handleStop());

        VBox box = new VBox(8, title, orderList, statusBox, cookBtn, stopBtn);
        box.setPadding(new Insets(12));
        box.setStyle(panelStyle());
        box.setPrefWidth(340);
        return box;
    }

    private VBox buildInventoryPanel() {
        Label title = panelTitle("Inventory");
        cashLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        cashLabel.setTextFill(Color.web("#4caf50"));

        inventoryGrid.setHgap(14);
        inventoryGrid.setVgap(4);
        inventoryGrid.setPadding(new Insets(6, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(inventoryGrid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(210);
        scroll.setStyle("-fx-background: #16213e; -fx-background-color: #16213e;");

        VBox box = new VBox(8, title, cashLabel, scroll);
        box.setPadding(new Insets(12));
        box.setStyle(panelStyle());
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return box;
    }

    private VBox buildRestockPanel() {
        Label title = panelTitle("Restock Supply Chain");

        restockDropdown.getItems().addAll(Ingredient.values());
        restockDropdown.setValue(Ingredient.BUN);
        restockDropdown.setMaxWidth(Double.MAX_VALUE);
        restockDropdown.setStyle(
            "-fx-background-color: #16213e; -fx-text-fill: #e0e0e0;" +
            "-fx-border-color: #0f3460; -fx-border-width: 1;");

        // shows the cost and quantity added for the currently selected ingredient
        Label costLabel = new Label(formatRestockCost(Ingredient.BUN));
        costLabel.setTextFill(Color.web("#aaaaaa"));
        costLabel.setFont(Font.font("Monospaced", 11));
        restockDropdown.setOnAction(e -> {
            Ingredient selected = restockDropdown.getValue();
            if (selected != null) costLabel.setText(formatRestockCost(selected));
        });

        Button buyBtn = styledButton("Buy Ingredient", "#0f3460", "#1a4a8a");
        buyBtn.setMaxWidth(Double.MAX_VALUE);
        buyBtn.setOnAction(e -> engine.handleRestock(restockDropdown.getValue()));

        VBox box = new VBox(8, title, restockDropdown, costLabel, buyBtn);
        box.setPadding(new Insets(12));
        box.setStyle(panelStyle());
        return box;
    }

    private VBox buildLogPanel() {
        Label title = panelTitle("System Log");

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle(
            "-fx-control-inner-background: #0d0d1a; -fx-text-fill: #b0c4de;" +
            "-fx-font-family: 'Monospaced'; -fx-font-size: 12;" +
            "-fx-border-color: #0f3460; -fx-border-width: 1;");
        logArea.setPrefHeight(170);

        Button saveBtn = styledButton("Save Game", "#2d6a4f", "#1e4d38");
        Button loadBtn = styledButton("Load Game", "#2d6a4f", "#1e4d38");
        saveBtn.setOnAction(e -> handleSave());
        loadBtn.setOnAction(e -> handleLoad());

        HBox btnRow = new HBox(10, saveBtn, loadBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox box = new VBox(6, title, logArea, btnRow);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        box.setPadding(new Insets(10));
        box.setStyle(panelStyle());
        return box;
    }

    //methods called by button lambdas
    private void handleStop() {
        engine.stopSimulation();
    }

    private void handleSave() {
        try {
            FileManager.saveState(engine);
            appendLog("Game saved to silicon_spatula_save.txt");
        } catch (IOException ex) {
            appendLog("ERROR saving: " + ex.getMessage());
        }
    }

    private void handleLoad() {
        try {
            FileManager.loadState(engine);
            refreshUI();
            appendLog("Game loaded!  Queue: " + engine.getOrderQueue().size()
                + " orders  |  Cash: $" + String.format("%.2f", engine.getInventory().getCash()));
        } catch (IOException ex) {
            appendLog("ERROR loading: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            appendLog("ERROR: Save file corrupted - " + ex.getMessage());
        }
    }


    public void refreshUI() {
        Platform.runLater(() -> {
            // Cash
            cashLabel.setText(String.format("Cash:  $%.2f", engine.getInventory().getCash()));

            // Inventory grid
            inventoryGrid.getChildren().clear();
            Map<Ingredient, Integer> snap = engine.getInventory().getInventorySnapshot();
            int row = 0;
            for (Map.Entry<Ingredient, Integer> entry : snap.entrySet()) {
                Label nameLabel = new Label(entry.getKey().name().replace("_", " "));
                nameLabel.setTextFill(Color.web("#aaaaaa"));
                nameLabel.setFont(Font.font("Monospaced", 12));

                Label qtyLabel = new Label(String.valueOf(entry.getValue()));
                qtyLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
                qtyLabel.setTextFill(entry.getValue() <= 2
                    ? Color.web("#e94560")
                    : Color.web("#b0e0b0"));

                inventoryGrid.add(nameLabel, 0, row);
                inventoryGrid.add(qtyLabel,  1, row);
                row++;
            }

            // Order list
            orderItems.clear();
            for (Order order : engine.getOrderQueue()) {
                orderItems.add(order.toString());
            }
            if (orderItems.isEmpty()) {
                orderItems.add("  (no pending orders)");
            }

            // Appliance status
            Set<ApplianceType> busy = engine.getBusyAppliances();
            updateApplianceLabel(grillStatus, "Grill", busy.contains(ApplianceType.GRILL));
            updateApplianceLabel(airpotStatus, "AirPot", busy.contains(ApplianceType.AIRPOT));
            updateApplianceLabel(ovenStatus, "Oven", busy.contains(ApplianceType.OVEN));
            updateApplianceLabel(drinkStatus, "Drink Dispenser", busy.contains(ApplianceType.DRINK_DISPENSER));
            updateApplianceLabel(sauceStatus, "Sauce Dispenser", busy.contains(ApplianceType.SAUCE_DISPENSER));
        });
    }

    //helper methods
    public void appendLog(String message) {
        String ts = LocalTime.now().format(TIME_FMT);
        Platform.runLater(() -> {
            logArea.appendText("[" + ts + "] " + message + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void updateApplianceLabel(Label label, String name, boolean busy) {
        if (busy) {
            label.setText(name + ": BUSY");
            label.setTextFill(Color.web("#e94560"));
        } else {
            label.setText(name + ": FREE");
            label.setTextFill(Color.web("#4caf50"));
        }
    }

    private Label applianceStatusLabel(String name) {
        Label l = new Label(name + ": FREE");
        l.setFont(Font.font("Monospaced", 11));
        l.setTextFill(Color.web("#4caf50"));
        return l;
    }

    private String formatRestockCost(Ingredient ing) {
        return String.format("Cost: $%.2f  |  Adds +%d units of %s",
            ing.getRestockCost(), InventoryManager.RESTOCK_AMOUNT,
            ing.name().replace("_", " "));
    }

    private Label panelTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#e0e0e0"));
        return l;
    }

    private Button styledButton(String text, String bg, String hover) {
        Button btn = new Button(text);
        btn.setFont(Font.font("System", FontWeight.BOLD, 12));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:5;-fx-padding:8 14;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color:" + hover + ";-fx-background-radius:5;-fx-padding:8 14;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color:" + bg + ";-fx-background-radius:5;-fx-padding:8 14;"));
        return btn;
    }

    private String panelStyle() {
        return "-fx-background-color:#16213e;-fx-border-color:#0f3460;" +
               "-fx-border-width:1;-fx-border-radius:6;-fx-background-radius:6;";
    }

    public static void main(String[] args) {
        launch(args);
    }
}