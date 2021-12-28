package com.mycompany.app;

import javafx.scene.control.Button;

import java.util.LinkedList;

public class MyButton {
    private final String name;
    private final Button myButton;
    private boolean isOn;

    public MyButton(String myName) {
        name = myName;
        myButton = new Button(myName);
        isOn = false;
        myButton.setMinWidth(40);
        myButton.setStyle("-fx-background-color: White");
    }

    public void press(LinkedList<String> vehicleToShowList) {

        if (isOn) {
            ButtonsManagement.deleteRequest(vehicleToShowList, name);
            myButton.setStyle("-fx-background-color: White");
            isOn = false;
        } else {
            ButtonsManagement.addRequest(vehicleToShowList, name);
            myButton.setStyle("-fx-background-color: MediumSeaGreen");
            isOn = true;
        }
    }

    public Button getMyButton() {
        return myButton;
    }
}
