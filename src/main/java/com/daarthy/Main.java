package com.daarthy;

import com.daarthy.mini.constructors.Cat;
import com.daarthy.mini.constructors.MiniData;

public class Main {
    public static void main(String[] args) {

        Cat cat = new Cat("My cat");

        if(cat.getClass().isAnnotationPresent(MiniData.class)) {
            System.out.println("Annotated!");
        }
    }
}