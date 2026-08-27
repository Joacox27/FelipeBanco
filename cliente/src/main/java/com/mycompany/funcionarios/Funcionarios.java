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
public class Funcionarios {
    private ArrayList<Funcionario> lista; 
    public Funcionarios(){
        this.lista = new ArrayList<>();
    }
    public void nuevoFuncionario(Funcionario f){
        this.lista.add(f);
    }
    public ArrayList<Funcionario> mostrarFuncionarios(){
        return this.lista;
    }
    
}
