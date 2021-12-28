package com.mycompany.app;

import javafx.scene.control.Button;

import java.util.LinkedList;

public class MyButton {
    String name;
    Button myButton;
    boolean isOn;

    public MyButton(String myName) {
        name = myName;
        myButton = new Button(myName);
        isOn = false;
        myButton.setMinWidth(40);
        myButton.setStyle("-fx-background-color: White");
    }

    void press(LinkedList<String> vehicleToShowList) {
        ButtonsManagement myButtons = new ButtonsManagement();

        if (isOn) {
            myButtons.deleteRequest(vehicleToShowList, name);
            myButton.setStyle("-fx-background-color: White");
            isOn = false;
        } else {
            myButtons.addRequest(vehicleToShowList, name);
            myButton.setStyle("-fx-background-color: MediumSeaGreen");
            isOn = true;
        }
    }

    Button getMyButton() {
        return myButton;
    }
}
