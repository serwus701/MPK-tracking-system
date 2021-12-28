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

    App mainThread;

    private volatile boolean exit = false;
    boolean keepWaiting = true;

    public void stop() {
        exit = true;
    }

    public void refresh() {
        keepWaiting = false;
    }

    public PaintingThread(App tempMyApp) {
        mainThread = tempMyApp;
    }

    @Override

    public void run() {

        GraphicsOverlay myGraphics = new GraphicsOverlay();
        mainThread.mapView.getGraphicsOverlays().add(myGraphics);

        while (!exit) {
            ArrayList<Point> myBusesPointsArray = new ArrayList<>();
            GetBusDoublePosition arrayDonorSystem = new GetBusDoublePosition();

            String longStringBusInput = null;
            try {
                longStringBusInput = GetMpkData.getMpkData(mainThread.vehicleToShowList);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if ((!(Objects.equals(longStringBusInput, "[]"))) && (!Objects.equals(longStringBusInput, ""))) {

                for (String vehicleNumber : mainThread.vehicleToShowList) {
                    arrayDonorSystem.setPos(longStringBusInput, vehicleNumber, 'x');
                    arrayDonorSystem.setPos(longStringBusInput, vehicleNumber, 'q');

                    int busSize = arrayDonorSystem.getBusXPositions().size();
                    myBusesPointsArray.clear();

                    for (int i = 0; i < busSize; i++) {
                        myBusesPointsArray.add(new Point(arrayDonorSystem.getBusYPositions().get(i), arrayDonorSystem.getBusXPositions().get(i), SpatialReferences.getWgs84()));
                    }
                    mainThread.busName = arrayDonorSystem.getBusNames();

                    myGraphics.getGraphics().clear();

                    boolean isBusInArea = false;
                    myGraphics.getGraphics().clear();
                    for (int i = 0; i < myBusesPointsArray.size(); i++) {

                        int color;
                        if (!mainThread.isTram(mainThread.busName.get(i)))
                            color = 0xFFFF0000;
                        else
                            color = 0xFF0000FF;

                        SimpleMarkerSymbol marker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, color, 12);

                        Graphic colourGraphic = new Graphic(myBusesPointsArray.get(i), marker);
                        colourGraphic.setZIndex(0);

                        myGraphics.getGraphics().add(colourGraphic);
                        TextSymbol markerNr = new TextSymbol();
                        markerNr.setText(mainThread.busName.get(i));
                        if (mainThread.isTram(mainThread.busName.get(i)))
                            markerNr.setColor(0xFFFFFFFF);
                        markerNr.setSize(10);
                        Graphic textGraphic = new Graphic(myBusesPointsArray.get(i), markerNr);
                        textGraphic.setZIndex(1);
                        myGraphics.getGraphics().add(textGraphic);

                        if (mainThread.doAreaCheck) {

                            PointCollection points = new PointCollection(SpatialReferences.getWgs84());

                            for (int j = 0; j < 31; j++) {
                                double x_onCircle = mainThread.areaCheckPointX + (mainThread.area * 0.01 * 1.533 * Math.cos(j * ((2 * Math.PI) / 30)));
                                double y_onCircle = mainThread.areaCheckPointY + (mainThread.area * 0.01 * 1.533 * 0.62 * Math.sin(j * ((2 * Math.PI) / 30)));
                                points.add(new Point(x_onCircle, y_onCircle));
                            }

                            Polygon surveillanceArea = new Polygon(points);

                            SimpleLineSymbol lineAroundArea = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFFFF0000, 2);
                            SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x10F0F000, lineAroundArea);

                            myGraphics.getGraphics().add(new Graphic(surveillanceArea, polygonSymbol));

                            SimpleMarkerSymbol areaMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFF0000FF, 7);
                            Point areaMarkerPoint = new Point(mainThread.areaCheckPointX, mainThread.areaCheckPointY, SpatialReferences.getWgs84());
                            myGraphics.getGraphics().add(new Graphic(areaMarkerPoint, areaMarker));

                            if (mainThread.distanceBetweenPositions(myBusesPointsArray.get(i).getX(), myBusesPointsArray.get(i).getY(), mainThread.areaCheckPointX, mainThread.areaCheckPointY) <= mainThread.area) {
                                isBusInArea = true;

                            }
                        }
                    }
                    mainThread.areaInformation.setVisible(isBusInArea);
                }
            }
            keepWaiting = true;
            for (int i = 0; i < 50; i++)
                if (keepWaiting) {
                    try {
                        Thread.sleep(mainThread.sleepTime / 50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
