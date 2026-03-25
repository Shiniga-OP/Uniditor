package com.uniditor.nucleo.entradas;

public interface EntradaTexto {
	public Teclado teclado;
	
    void add(String texto);
    void rmAntes();
    void rmDepois();
}
