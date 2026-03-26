package com.uniditor.nucleo.sintaxe;

import com.uniditor.nucleo.graficos.Cor;

public class Destaque {
    public final Tokenizador tokenizador;

    public Destaque(Tokenizador tokenizador) {
        this.tokenizador = tokenizador;
    }

    public Token[] colorir(String linha) {
        return tokenizador.tokenizar(linha);
    }

    public void reiniciar() {
        tokenizador.reiniciar();
    }
}

