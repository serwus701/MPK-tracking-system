package com.mycompany.app;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.*;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import javafx.application.Application;
import javafx.event.ActionEvent;
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
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Application {

    static MapView mapView;
    ArrayList<MyButton> buses = new ArrayList<>();
    ArrayList<MyButton> trams = new ArrayList<>();
    ArrayList<String> busName = new ArrayList<>();
    LinkedList<String> vehicleToShowList = new LinkedList<>();
    boolean doAreaCheck = false;
    double areaCheckPointX = 17.036694;
    double areaCheckPointY = 51.11114;
    int sleepTime = 5000;
    double value;
    Label areaInformation = new Label("Your vehicle is in the area");

    public static void main(String[] args) {

        Application.launch(args);
    }

    boolean isTram(String busNr) {
        if ((busNr.equals("1") || busNr.equals("2") || busNr.equals("3") || busNr.equals("4") || busNr.equals("5") || busNr.equals("6") || busNr.equals("7") || busNr.equals("8") || busNr.equals("9") || busNr.equals("10"))) {
            return true;
        }
        if (busNr.equals("11") || busNr.equals("15") || busNr.equals("16") || busNr.equals("17") || busNr.equals("20") || busNr.equals("23") || busNr.equals("31") || busNr.equals("33") || busNr.equals("70") || busNr.equals("74")) {
            return true;
        }
        return false;
    }

    double distanceBetweenPositions(double x1, double y1, double x2, double y2) {

        x1 = Math.toRadians(x1);
        x2 = Math.toRadians(x2);
        y1 = Math.toRadians(y1);
        y2 = Math.toRadians(y2);

        double dlon = y2 - y1;
        double dlat = x2 - x1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(x1) * Math.cos(x2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 3956;

        return c * r;
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {

        MyRunnable userThread = new MyRunnable();
        Thread thread = new Thread(userThread, "T1");

        stage.setTitle("Mpk Wroclaw");
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                userThread.stop();
            }
        });


        StackPane stackPane = new StackPane();
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


        ButtonsManagement myButtons = new ButtonsManagement();
        myButtons.fillBuses();
        myButtons.fillTrams();

        buses = myButtons.getBuses();
        trams = myButtons.getTrams();

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
            myButton.getMyButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    myButton.press(myButton.getIsOn(), vehicleToShowList);
                    userThread.refresh();
                }
            });
        }

        for (MyButton myButton : trams
        ) {
            tramVbox.getChildren().add(myButton.getMyButton());
            myButton.getMyButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    myButton.press(myButton.getIsOn(), vehicleToShowList);
                    userThread.refresh();
                }
            });
        }

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

        set5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sleepTime = 5000;
            }
        });
        set10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sleepTime = 10000;
            }
        });
        set15.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sleepTime = 15000;
            }
        });
        intervalBox.getChildren().addAll(set5, set10, set15);

        VBox areaStuff = new VBox();

        Slider areaSlider = new Slider(0, 2000, 1000);

        EventHandler<? super MouseEvent> mapMouseHandler = (EventHandler<MouseEvent>) event -> {
            value = (double) (((Slider) event.getSource()).getValue());
            System.out.println(value);
        };
        EventHandler<? super KeyEvent> mapKeyHandler = (EventHandler<KeyEvent>) event -> {
            value = (double) (((Slider) event.getSource()).getValue());
            System.out.println(value);
        };

        areaSlider.setOnMouseReleased(mapMouseHandler);


        stackPane.getChildren().add(areaStuff);
        StackPane.setAlignment(areaStuff, Pos.TOP_CENTER);
        areaStuff.setMaxWidth(400);
        areaStuff.setMaxHeight(30);

        Button areaCheckDescription = new Button("Press it to enable area check. Afterwards use right mouse button");
        areaCheckDescription.setStyle("-fx-background-color: #d29f30");

        areaCheckDescription.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doAreaCheck = !doAreaCheck;
                if (areaCheckDescription.getStyle().equals("-fx-background-color: #d29f30"))
                    areaCheckDescription.setStyle("-fx-background-color: #30d24b");
                else
                    areaCheckDescription.setStyle("-fx-background-color: #d29f30");
            }
        });

        areaStuff.getChildren().add(areaCheckDescription);
        areaStuff.getChildren().add(areaSlider);
        areaStuff.getChildren().add(areaInformation);


        EventHandler<? super MouseEvent> mapMouseButtonHandler = (EventHandler<MouseEvent>) event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                Point2D graphicPoint = new Point2D(event.getX(), event.getY());
                Point mapPoint = mapView.screenToLocation(graphicPoint);

                String coordinatesString = CoordinateFormatter.toLatitudeLongitude(mapPoint, CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES, 6);
                String[] splitCoordinates = coordinatesString.split(" ");
                areaCheckPointY = Double.parseDouble(splitCoordinates[0].replace("N", ""));
                areaCheckPointX = Double.parseDouble((splitCoordinates[1].replace("E", "")));
            }
        };
        mapView.setOnMouseClicked(mapMouseButtonHandler);

        areaInformation.setStyle("-fx-background-color: #ef0808");
        areaInformation.setVisible(false);
    }

    public class MyRunnable implements Runnable {

        private volatile boolean exit = false;
        boolean keepWaiting = true;

        public void stop() {
            exit = true;
        }

        public void refresh(){
            keepWaiting = false;
        }

        @Override

        public void run() {

            while (!exit) {
                GraphicsOverlay myGraphics = new GraphicsOverlay();
                mapView.getGraphicsOverlays().add(myGraphics);
                ArrayList<Point> myBusesPointsArray = new ArrayList<>();

                GetBusDoublePosition arrayDonorSystem = new GetBusDoublePosition();

                String longStringBusInput = null;
                try {
                    longStringBusInput = GetMpkData.getMpkData(vehicleToShowList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if ((!(Objects.equals(longStringBusInput, "[]"))) && (!Objects.equals(longStringBusInput, ""))) {

                    for (String vehicleNumber : vehicleToShowList) {
                        arrayDonorSystem.setPos(longStringBusInput, vehicleNumber, 'x');
                        arrayDonorSystem.setPos(longStringBusInput, vehicleNumber, 'q');

                        int busSize = arrayDonorSystem.getBusXPositions().size();
                        myBusesPointsArray.clear();

                        for (int i = 0; i < busSize; i++) {
                            myBusesPointsArray.add(new Point(arrayDonorSystem.getBusYPositions().get(i), arrayDonorSystem.getBusXPositions().get(i), SpatialReferences.getWgs84()));
                        }
                        busName = arrayDonorSystem.getBusnames();

                        myGraphics.getGraphics().clear();

                        boolean isBusInArea = false;
                        for (int i = 0; i < myBusesPointsArray.size(); i++) {

                            int color;
                            if (!isTram(busName.get(i)))
                                color = 0xFFFF0000;
                            else
                                color = 0xFF0000FF;

                            SimpleMarkerSymbol marker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, color, 12);

                            myGraphics.getGraphics().add(new Graphic(myBusesPointsArray.get(i), marker));
                            TextSymbol markerNr = new TextSymbol();
                            markerNr.setText(busName.get(i));
                            if (isTram(busName.get(i)))
                                markerNr.setColor(0xFFFFFFFF);
                            markerNr.setSize(10);
                            myGraphics.getGraphics().add(new Graphic(myBusesPointsArray.get(i), markerNr));

                            if (doAreaCheck) {
                                SimpleMarkerSymbol areaMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 7);
                                Point areaMarkerPoint = new Point(areaCheckPointX, areaCheckPointY, SpatialReferences.getWgs84());
                                myGraphics.getGraphics().add(new Graphic(areaMarkerPoint, areaMarker));

                                if (distanceBetweenPositions(myBusesPointsArray.get(i).getX(), myBusesPointsArray.get(i).getY(), areaCheckPointX, areaCheckPointY) < value / 1000) {
                                    isBusInArea = true;
                                }
                            }
                        }
                        areaInformation.setVisible(isBusInArea);
                    }
                }
                keepWaiting = true;
                for(int i=0; i<20; i++)
                    if(keepWaiting) {
                        try {
                            Thread.sleep(sleepTime/20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                myGraphics.getGraphics().clear();
            }
        }
    }

    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }


}
