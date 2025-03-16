package com.example.studytracker;

public class Materia {
    private int id;
    private String nome;

    public Materia(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
