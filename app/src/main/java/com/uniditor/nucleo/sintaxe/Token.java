package com.uniditor.nucleo.sintaxe;

import com.uniditor.nucleo.graficos.Cor;

public class Token {
    public final Cor cor;
    public final String descricao; // futuro: exibido no autocomplete
	public final boolean negrito, italico;

    public Token(Cor cor, String descricao, boolean negrito, boolean italico) {
        this.cor = cor;
        this.descricao = descricao;
		this.negrito = negrito;
		this.italico = italico;
    }
	
	public Token(Cor cor, boolean negrito, boolean italico) {
        this(cor, null, negrito, italico);
    }

    public Token(Cor cor) {
        this(cor, null, false, false);
    }
}

