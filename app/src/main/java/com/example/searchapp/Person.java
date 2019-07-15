package com.example.searchapp;


//Definición de clase Person, la cual será utilizada para representar a una persona en la aplicación
public class Person {

    //Declaración de los atributos de la clase

    private String name;
    private String lastName;
    private String birthDate;
    private String email;

    //Constructor de la clase, el cual será utilizado para la creación de los objetos de tipo Person

    public Person(String name, String lastName, String birthDate, String email) {
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
    }

    //Definición de métodos getters, para acceder a los atributos de un objeto de tipo Person

    public String getName() {
        return this.name;
    }
    public String getLastName() {
        return this.lastName;
    }
    public String getBirthDate() {
        return this.birthDate;
    }
    public String getEmail() {
        return this.email;
    }
}