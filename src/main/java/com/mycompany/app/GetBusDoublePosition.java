package com.mycompany.app;

import java.util.ArrayList;

public class GetBusDoublePosition {

    private final ArrayList<Double> busXPositions;
    private final ArrayList<Double> busYPositions;
    private final ArrayList<String> busName;

    private static String[] splitMyInput(String input) {
        input = input.replace("[", "");
        input = input.replace("]", "");
        input = input.replace("{", "");
        input = input.replace("},", "!");
        input = input.replace("\"y\"", "\"q\"");

        return input.split("!");

    }

    private void setPos(String input, String busNr, char xOrY) {

        String[] myBusList = splitMyInput(input);
        String strTempBusNr;

        for (String singleInput : myBusList) {
            strTempBusNr = "";
            for (int i = 8; i < 11; i++) {
                if (!(singleInput.charAt(i) == '\"'))
                    strTempBusNr += singleInput.charAt(i);
                else
                    break;
            }

            if (busNr.equals(strTempBusNr)) {
                StringBuilder strPos = new StringBuilder();

                for (int i = 0; i < singleInput.length(); i++) {
                    if (singleInput.charAt(i) == xOrY) {
                        int j = i + 3;
                        while (!(singleInput.charAt(j) == ',')) {
                            strPos.append(singleInput.charAt(j));
                            j++;
                        }
                        double Pos = Double.parseDouble(strPos.toString());
                        if (xOrY == 'x') {
                            busName.add(busNr);
                            busXPositions.add(Pos);
                        }
                        if (xOrY == 'q')
                            busYPositions.add(Pos);
                        break;
                    }
                }
            }
        }
    }

    public GetBusDoublePosition() {
        busXPositions = new ArrayList<>();
        busYPositions = new ArrayList<>();
        busName = new ArrayList<>();
    }

    public void setPositions(String input, String busNr) {
        setPos(input, busNr, 'x');
        setPos(input, busNr, 'q');
    }

    public void clear() {
        busXPositions.clear();
        busYPositions.clear();
        busName.clear();
    }

    public ArrayList<Double> getBusXPositions() {
        return busXPositions;
    }

    public ArrayList<Double> getBusYPositions() {
        return busYPositions;
    }

    public ArrayList<String> getBusNames() {
        return busName;
    }
}
