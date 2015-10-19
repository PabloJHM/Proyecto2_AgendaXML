package com.example.dam.proyecto1;

/**
 * Created by 2dam on 19/10/2015.
 */
public class ComparadorNombre implements java.util.Comparator<Contacto>{
    @Override
    public int compare(Contacto c1, Contacto c2) {
        return c2.getNombre().compareToIgnoreCase(c1.getNombre());
    }
}
