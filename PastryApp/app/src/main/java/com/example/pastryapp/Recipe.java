package com.example.pastryapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    String Author, Name, Id, ImageUrl;
    ArrayList<String> Ingridients, Quantity, Instructions;
    ArrayList<Double> Timers;

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getId() {
        return Id;
    }

    public String getAuthor() {
        return Author;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<String> getIngridients() {
        return Ingridients;
    }

    public ArrayList<String> getQuantity() {
        return Quantity;
    }

    public ArrayList<String> getInstructions() {
        return Instructions;
    }

    public ArrayList<Double> getTimers() {
        return Timers;
    }
}
