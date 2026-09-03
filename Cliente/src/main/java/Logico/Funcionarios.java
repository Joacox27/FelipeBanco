package Logico;

import java.util.ArrayList;

public class Funcionarios {

    private ArrayList<Funcionario> lista;

    public Funcionarios() {
        this.lista = new ArrayList<>();
    }

    public void nuevoFuncionario(Funcionario f) {
        this.lista.add(f);
    }

    public ArrayList<Funcionario> mostrarFuncionarios() {
        return this.lista;
    }
}