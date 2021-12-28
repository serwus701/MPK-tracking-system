package com.mycompany.app;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.esri.arcgisruntime.mapping.Viewpoint;

import java.util.ArrayList;
import java.util.LinkedList;

public class App extends Application {

    private final Label areaDetectionLabel;
    private final PaintingThread userThread;
    private final StackPane stackPane;
    private final LinkedList<String> vehicleToShowList;
    private MapView mapView;
    private boolean doAreaCheck;
    private double areaCheckPointX;
    private double areaCheckPointY;
    private int sleepTime;
    private double area;

    public App() {
        userThread = new PaintingThread(this);
        stackPane = new StackPane();
        vehicleToShowList = new LinkedList<>();
        doAreaCheck = false;
        areaCheckPointX = 17.036694;
        areaCheckPointY = 51.11114;
        sleepTime = 5000;
        area = 1;
        areaDetectionLabel = new Label("Your vehicle is in the area");
    }

    public static void main(String[] args) {

        Application.launch(args);
    }

    private void addBussesButtons() {
        VBox busVbox1 = new VBox();
        VBox busVbox2 = new VBox();
        VBox busVbox3 = new VBox();

        HBox busBox = new HBox();
        busBox.getChildren().addAll(busVbox1, busVbox2, busVbox3);

        busBox.setStyle("-fx-background-color: #c11111");
        busBox.setSpacing(2);
        busVbox1.setSpacing(2);
        busVbox2.setSpacing(2);
        busVbox3.setSpacing(2);

        VBox tramVbox = new VBox();
        StackPane.setAlignment(busBox, Pos.TOP_LEFT);
        busBox.setMaxSize(stackPane.getMaxWidth() / 8, stackPane.getMaxHeight() / 4);
        StackPane.setAlignment(tramVbox, Pos.TOP_RIGHT);
        tramVbox.setMaxSize(stackPane.getMaxWidth() / 8, stackPane.getMaxHeight() / 4);
        tramVbox.setSpacing(2);
        tramVbox.setStyle("-fx-background-color: #1149c1");
        stackPane.getChildren().add(busBox);
        stackPane.getChildren().add(tramVbox);

        ArrayList<MyButton> buses = ButtonsManagement.fillBusesButtons();
        ArrayList<MyButton> trams = ButtonsManagement.fillTramsButtons();

        for (int i = 0; i < 27; i++) {
            busVbox1.getChildren().add(buses.get(i).getMyButton());
        }
        for (int i = 27; i < 54; i++) {
            busVbox2.getChildren().add(buses.get(i).getMyButton());
        }
        for (int i = 54; i < 73; i++) {
            busVbox3.getChildren().add(buses.get(i).getMyButton());
        }

        for (MyButton myButton : buses
        ) {
            myButton.getMyButton().setOnAction(event -> {
                myButton.press(vehicleToShowList);
                userThread.refresh();
            });
        }

        for (MyButton myButton : trams
        ) {
            tramVbox.getChildren().add(myButton.getMyButton());
            myButton.getMyButton().setOnAction(event -> {
                myButton.press(vehicleToShowList);
                userThread.refresh();
            });
        }
    }

    private void addIntervalButtons() {
        VBox refreshBox = new VBox();
        HBox intervalBox = new HBox();
        Label refreshInstructions = new Label("Choose refresh rate");
        refreshBox.setStyle("-fx-background-color: #e11de8");
        refreshBox.getChildren().add(refreshInstructions);
        refreshBox.getChildren().add(intervalBox);
        stackPane.getChildren().add(refreshBox);
        StackPane.setAlignment(refreshBox, Pos.BOTTOM_CENTER);
        refreshBox.setMaxSize(20, stackPane.getMaxHeight() / 10);
        Button set5 = new Button("5s");
        Button set10 = new Button("10s");
        Button set15 = new Button("15s");
        set5.setMinWidth(40);
        set10.setMinWidth(40);
        set15.setMinWidth(40);

        set5.setOnAction(event -> {
            sleepTime = 5000;
            userThread.refresh();
        });
        set10.setOnAction(event -> {
            sleepTime = 10000;
            userThread.refresh();
        });
        set15.setOnAction(event -> {
            sleepTime = 15000;
            userThread.refresh();
        });
        intervalBox.getChildren().addAll(set5, set10, set15);
    }

    private void addAreaButtons() {
        VBox areaStuff = new VBox();

        Slider areaSlider = new Slider(0, 2, 1);
        areaSlider.setMaxWidth(360);

        Button areaCheckDescription = new Button("Press it to enable area check. Afterwards use right mouse button");

        EventHandler<? super MouseEvent> mapMouseHandler = (EventHandler<MouseEvent>) event -> {
            area = ((Slider) event.getSource()).getValue();
            if (areaCheckDescription.getStyle().equals("-fx-background-color: #30d24b"))
                userThread.refresh();
        };
        EventHandler<? super KeyEvent> mapKeyHandler = (EventHandler<KeyEvent>) event -> {
            area = ((Slider) event.getSource()).getValue();
            if (areaCheckDescription.getStyle().equals("-fx-background-color: #30d24b"))
                userThread.refresh();
        };

        areaSlider.setOnMouseReleased(mapMouseHandler);
        areaSlider.setOnKeyReleased(mapKeyHandler);


        stackPane.getChildren().add(areaStuff);
        StackPane.setAlignment(areaStuff, Pos.TOP_CENTER);
        areaStuff.setMaxWidth(400);
        areaStuff.setMaxHeight(40);

        areaCheckDescription.setStyle("-fx-background-color: #d29f30");

        areaCheckDescription.setOnAction(event -> {
            doAreaCheck = !doAreaCheck;
            if (areaCheckDescription.getStyle().equals("-fx-background-color: #d29f30"))
                areaCheckDescription.setStyle("-fx-background-color: #30d24b");
            else
                areaCheckDescription.setStyle("-fx-background-color: #d29f30");
            userThread.refresh();
        });

        Label sliderDescription = new Label("0 m                                          1000 m                                     2000 m");
        sliderDescription.setStyle("-fx-background-color: #6cf13b");

        areaStuff.getChildren().add(areaCheckDescription);
        areaStuff.getChildren().add(areaSlider);
        areaStuff.getChildren().add(sliderDescription);
        areaStuff.getChildren().add(areaDetectionLabel);


        EventHandler<? super MouseEvent> mapMouseButtonHandler = (EventHandler<MouseEvent>) event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                Point2D graphicPoint = new Point2D(event.getX(), event.getY());
                Point mapPoint = mapView.screenToLocation(graphicPoint);

                String coordinatesString = CoordinateFormatter.toLatitudeLongitude(mapPoint, CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES, 6);
                String[] splitCoordinates = coordinatesString.split(" ");
                areaCheckPointY = Double.parseDouble(splitCoordinates[0].replace("N", ""));
                areaCheckPointX = Double.parseDouble((splitCoordinates[1].replace("E", "")));

                if (areaCheckDescription.getStyle().equals("-fx-background-color: #30d24b"))
                    userThread.refresh();
            }
        };
        mapView.setOnMouseClicked(mapMouseButtonHandler);

        areaDetectionLabel.setStyle("-fx-background-color: #ef0808");
        areaDetectionLabel.setVisible(false);
    }

    @Override
    public void start(Stage stage) {

        Thread thread = new Thread(userThread, "T1");

        stage.setTitle("Mpk Wroclaw");
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        stage.setOnCloseRequest(t -> {
            userThread.refresh();
            userThread.stop();
        });

        Scene scene = new Scene(stackPane);
        stage.setScene(scene);

        String yourApiKey = "AAPK252690f93bdf4a97873eabb3a8ab7265IOKa3PonrswUJwppgQfTIKQ-6z96Q7jzyw5iuVZk2sSosqKtmmZb2UGfarSrBkrT";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

        mapView = new MapView();
        stackPane.getChildren().add(mapView);
        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION);
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(51.11114, 17.036694, 90000));

        thread.start();

        addBussesButtons();

        addIntervalButtons();

        addAreaButtons();
    }

    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }

    public void addMapView(GraphicsOverlay myGraphics) {
        mapView.getGraphicsOverlays().add(myGraphics);
    }

    public void setAreaLabelVisibility(boolean setVisible) {
        areaDetectionLabel.setVisible(setVisible);
        ;
    }

    public LinkedList<String> getVehicleToShowList() {
        return vehicleToShowList;
    }

    public boolean getDoAreaCheck() {
        return doAreaCheck;
    }

    public double getAreaCheckPointX() {
        return areaCheckPointX;
    }

    public double getAreaCheckPointY() {
        return areaCheckPointY;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public double getArea() {
        return area;
    }
}
