package com.example.databaseapp;

public class pet {

    private String name;
    private String breed;
    private int weight;
    private int gender;
   // private int price;

   //  public pet(String name , String breed , int weight , int gender , int price)
   public pet(String name , String breed , int weight , int gender ){
        this.name = name;
        this.breed = breed;
        this.weight = weight;
        this.gender = gender;
      //  this.price = price;

    }


    public String getName(){
       return name;
    }

    public int getGender(){
        return gender;
    }

    public String getBreed(){
       return breed;
    }

    public int getWeight(){
       return weight;
    }

   // public int getPrice() {return price;}

    public void setName(String name) {
       this.name = name;
    }


    public void setBreed(String breed) {
        this.breed = breed;
    }


    public void setWeight(int weight) {
        this.weight = weight;
    }


    public void setGender(int gender) {
        this.gender = gender;
    }

   // public void setPrice(int price) { this.price = price; }
}
