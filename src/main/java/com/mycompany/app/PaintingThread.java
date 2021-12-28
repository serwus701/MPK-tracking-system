package com.mycompany.app;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PaintingThread implements Runnable {

    private final App mainThread;
    private final GraphicsOverlay myGraphics;
    private final ArrayList<Point> myBusesPointsArray;
    private ArrayList<String> busName;
    private final GetBusDoublePosition arrayDonorSystem;
    private volatile boolean exit = false;
    private boolean keepWaiting = true;

    private boolean isTram(String busNr) {
        if ((busNr.equals("1") || busNr.equals("2") || busNr.equals("3") || busNr.equals("4") || busNr.equals("5") || busNr.equals("6") || busNr.equals("7") || busNr.equals("8") || busNr.equals("9") || busNr.equals("10"))) {
            return true;
        }
        return busNr.equals("11") || busNr.equals("15") || busNr.equals("16") || busNr.equals("17") || busNr.equals("20") || busNr.equals("23") || busNr.equals("31") || busNr.equals("33") || busNr.equals("70") || busNr.equals("74");
    }

    private double distanceBetweenPositions(double latitude1, double longitude1, double latitude2, double longitude2) {
        latitude1 = latitude1 / 1.65;
        latitude2 = latitude2 / 1.65;
        double p = 0.017453292519943295;
        double a = 0.5 - Math.cos((latitude2 - latitude1) * p) / 2 + Math.cos(latitude1 * p) * Math.cos(latitude2 * p) * (1 - Math.cos((longitude2 - longitude1) * p)) / 2;

        return 12742 * Math.asin(Math.sqrt(a));
    }

    private void drawBuses(int i) {
        int color;
        if (!isTram(busName.get(i)))
            color = 0xFFFF0000;
        else
            color = 0xFF0000FF;

        SimpleMarkerSymbol marker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, color, 12);

        Graphic colourGraphic = new Graphic(myBusesPointsArray.get(i), marker);
        colourGraphic.setZIndex(0);

        myGraphics.getGraphics().add(colourGraphic);
        TextSymbol markerNr = new TextSymbol();
        markerNr.setText(busName.get(i));
        if (isTram(busName.get(i)))
            markerNr.setColor(0xFFFFFFFF);
        markerNr.setSize(10);
        Graphic textGraphic = new Graphic(myBusesPointsArray.get(i), markerNr);
        textGraphic.setZIndex(1);
        myGraphics.getGraphics().add(textGraphic);
    }

    private void drawAreaCircle() {
        PointCollection points = new PointCollection(SpatialReferences.getWgs84());

        for (int j = 0; j < 31; j++) {
            double x_onCircle = mainThread.getAreaCheckPointX() + (mainThread.getArea() * 0.01 * 1.533 * Math.cos(j * ((2 * Math.PI) / 30)));
            double y_onCircle = mainThread.getAreaCheckPointY() + (mainThread.getArea() * 0.01 * 1.533 * 0.62 * Math.sin(j * ((2 * Math.PI) / 30)));
            points.add(new Point(x_onCircle, y_onCircle));
        }

        Polygon surveillanceArea = new Polygon(points);

        SimpleLineSymbol lineAroundArea = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
        SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x10F0F000, lineAroundArea);

        myGraphics.getGraphics().add(new Graphic(surveillanceArea, polygonSymbol));

        SimpleMarkerSymbol areaMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 7);
        Point areaMarkerPoint = new Point(mainThread.getAreaCheckPointX(), mainThread.getAreaCheckPointY(), SpatialReferences.getWgs84());
        myGraphics.getGraphics().add(new Graphic(areaMarkerPoint, areaMarker));
    }

    private void waiting() {
        keepWaiting = true;
        for (int i = 0; i < 50; i++)
            if (keepWaiting) {
                try {
                    Thread.sleep(mainThread.getSleepTime() / 50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    public PaintingThread(App tempMyApp) {
        mainThread = tempMyApp;
        myGraphics = new GraphicsOverlay();
        myBusesPointsArray = new ArrayList<>();
        arrayDonorSystem = new GetBusDoublePosition();
        busName = new ArrayList<>();
    }

    public void stop() {
        exit = true;
    }

    public void refresh() {
        keepWaiting = false;
    }

    @Override

    public void run() {

        mainThread.addMapView(myGraphics);

        while (!exit) {

            arrayDonorSystem.clear();

            String longStringBusInput = null;
            try {
                longStringBusInput = GetMpkData.getMpkData(mainThread.getVehicleToShowList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ((!(Objects.equals(longStringBusInput, "[]"))) && (!Objects.equals(longStringBusInput, ""))) {

                for (String vehicleNumber : mainThread.getVehicleToShowList()) {
                    arrayDonorSystem.setPositions(longStringBusInput, vehicleNumber);

                    myBusesPointsArray.clear();

                    for (int i = 0; i < arrayDonorSystem.getBusXPositions().size(); i++) {
                        myBusesPointsArray.add(new Point(arrayDonorSystem.getBusYPositions().get(i), arrayDonorSystem.getBusXPositions().get(i), SpatialReferences.getWgs84()));
                    }
                    busName = arrayDonorSystem.getBusNames();

                    myGraphics.getGraphics().clear();
                    boolean isBusInArea = false;
                    for (int i = 0; i < myBusesPointsArray.size(); i++) {

                        drawBuses(i);

                        if (mainThread.getDoAreaCheck()) {
                            drawAreaCircle();

                            if (distanceBetweenPositions(myBusesPointsArray.get(i).getX(), myBusesPointsArray.get(i).getY(), mainThread.getAreaCheckPointX(), mainThread.getAreaCheckPointY()) <= mainThread.getArea()) {
                                isBusInArea = true;

                            }
                        }
                    }
                    mainThread.setAreaLabelVisibility(isBusInArea);
                }
            } else
                myGraphics.getGraphics().clear();

            waiting();
        }
    }
}
