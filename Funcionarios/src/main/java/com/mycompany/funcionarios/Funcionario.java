/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.funcionarios;

/**
 *
 * @author gabriel
 */
public class Funcionario {
    private String nombre;
    private int edad;
    private String rol;

    public Funcionario(String nombre, int edad, String rol) {
        this.nombre = nombre;
        this.edad = edad;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String toString(){
        return this.getNombre() + " " + this.getRol() + " " + this.getEdad();
    }
}
