package com.mycompany.app;

import com.esri.arcgisruntime.internal.util.DelayService;

import java.util.ArrayList;

public class GetBusDoublePosition {

    ArrayList <Double> busXPositions = new ArrayList<>();
    ArrayList <Double> busYPositions = new ArrayList<>();
    ArrayList <String> busName = new ArrayList<>();

   void setPos(String input, String busNr, char xOrY){

        String[] myBusList = splitMyInput(input);
        String strTempBusNr = "";

        for(String singleInput: myBusList){
            strTempBusNr = "";
            for(int i=8; i<11; i++){
                if(!(singleInput.charAt(i) =='\"'))
                    strTempBusNr += singleInput.charAt(i);
                else
                    break;
            }

            if(busNr.equals(strTempBusNr)){
                String strPos = "";

                for(int i=0;i<singleInput.length();i++){
                    if(singleInput.charAt(i)==xOrY){
                        int j=i+3;
                        while (!(singleInput.charAt(j) ==',')){
                                strPos+=singleInput.charAt(j);
                            j++;
                        }
                        double Pos = Double.parseDouble(strPos);
                        if(xOrY=='x'){
                            busName.add(busNr);
                            busXPositions.add(Pos);
                        }
                        if(xOrY=='q')
                            busYPositions.add(Pos);
                        break;
                    }
                }
            }
        }
    }

    static String[] splitMyInput(String input){
        input = input.replace("[","");
        input = input.replace("]","");
        input = input.replace("{","");
        input = input.replace("},","!");
        input = input.replace("\"y\"","\"q\"");

        String[] myBusList = input.split("!");

        return myBusList;

    }

    ArrayList<Double> getBusXPositions(){
       return busXPositions;
    }
    ArrayList<Double> getBusYPositions(){
        return busYPositions;
    }
    ArrayList<String> getBusnames(){ return busName; }


}
