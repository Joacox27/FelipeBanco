/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.funcionarios;

import java.util.ArrayList;

/**
 *
 * @author gabriel
 */
public class Fachada {
    private static Fachada singleton;
    private Funcionarios funcionarios = new Funcionarios();
    public static Fachada getInstancia(){
        if(singleton==null){
            singleton=new Fachada();
        }
        return singleton;
    }
    public ArrayList<Funcionario> mostrarFuncionarios(){
        return this.funcionarios.mostrarFuncionarios();
    }
    private void nuevoFuncionario(Funcionario f){
        this.funcionarios.nuevoFuncionario(f);
    }
    
    public void ingresarDatos(String nombre, int edad, String rol){
        Funcionario f= new Funcionario(nombre, edad, rol);
        nuevoFuncionario(f);
    }
    
}
