
package com.example.smartpantrymanager;

public class Recipe {
    private int id;
    private String name;
    private String ingredients;
    private String instructions;

    public Recipe(int id, String name, String ingredients,
                  String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}
