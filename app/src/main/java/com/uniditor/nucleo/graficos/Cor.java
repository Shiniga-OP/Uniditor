package com.uniditor.nucleo.graficos;

public class Cor {
	public int inicio, fim, valor;
	
	public static final int AZUL = 0xFF61AFEF, VERDE = 0xFF98C379, ROXO = 0xFFC678DD;
	
	public Cor(int inicio, int fim, int valor) {
		this.inicio = inicio;
		this.fim = fim;
		this.valor = valor;
	}
}
