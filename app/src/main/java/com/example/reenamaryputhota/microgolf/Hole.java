package com.example.reenamaryputhota.microgolf;

/**
 * Created by reenamaryputhota on 4/14/18.
 */

public class Hole {

    int image_id;
    int number;

    public Hole(int image_id,int num) {
        this.image_id= image_id;
        this.number = num;
    }

    public int getImageId() {
        return image_id;
    }

    public int getNumber() {
        return number;
    }

    public void setImageId(int image_id) {
       this.image_id = image_id;
    }

    public void setNumber(int number) {
        this.number = number;
    }




}



