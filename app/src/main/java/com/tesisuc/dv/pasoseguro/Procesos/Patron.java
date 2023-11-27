package com.tesisuc.dv.pasoseguro.Procesos;

public class Patron {

    private String titulo;
    private float x[];
    private float y[];
    private float z[];

    public Patron(String titulo, float x[], float y[], float z[]){
        this.titulo = titulo;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public float[] getX() {
        return x;
    }

    public void setX(float[] x) {
        this.x = x;
    }

    public float[] getY() {
        return y;
    }

    public void setY(float[] y) {
        this.y = y;
    }

    public float[] getZ() {
        return z;
    }

    public void setZ(float[] z) {
        this.z = z;
    }
}
