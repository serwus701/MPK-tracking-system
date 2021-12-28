package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ButtonsManagement {

    public static void deleteRequest(List<String> busNumbers, String numberToDelete) {
        for (String eachNumber : busNumbers) {
            if (Objects.equals(numberToDelete, eachNumber)) {
                busNumbers.remove(eachNumber);
                return;
            }
        }
    }

    public static void addRequest(List<String> busNumbers, String numberToAdd) {
        for (String eachNumber : busNumbers) {
            if (Objects.equals(numberToAdd, eachNumber)) {
                return;
            }
        }
        busNumbers.add(numberToAdd);
    }

    public static ArrayList<MyButton> fillBusesButtons() {

        ArrayList<MyButton> buses = new ArrayList<>();

        buses.add(new MyButton("a"));
        buses.add(new MyButton("c"));
        buses.add(new MyButton("d"));
        buses.add(new MyButton("k"));
        buses.add(new MyButton("n"));

        for (int i = 100; i < 117; i++) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        for (int i = 118; i < 123; i++) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        for (int i = 124; i < 135; i++) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        buses.add(new MyButton("136"));
        buses.add(new MyButton("140"));
        for (int i = 142; i < 152; i++) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        buses.add(new MyButton("206"));
        for (int i = 240; i < 252; i++) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        for (int i = 253; i < 260; i = i + 2) {
            buses.add(new MyButton(String.valueOf(i)));
        }
        buses.add(new MyButton("315"));
        buses.add(new MyButton("319"));
        buses.add(new MyButton("602"));
        buses.add(new MyButton("607"));
        buses.add(new MyButton("703"));
        buses.add(new MyButton("731"));

        return buses;
    }

    public static ArrayList<MyButton> fillTramsButtons() {

        ArrayList<MyButton> trams = new ArrayList<>();

        for (int i = 1; i < 12; i++) {
            trams.add(new MyButton(String.valueOf(i)));
        }
        for (int i = 15; i < 18; i++) {
            trams.add(new MyButton(String.valueOf(i)));
        }
        trams.add(new MyButton("20"));
        trams.add(new MyButton("23"));
        trams.add(new MyButton("31"));
        trams.add(new MyButton("33"));
        trams.add(new MyButton("70"));
        trams.add(new MyButton("74"));

        return trams;
    }
}
