package com.uniditor.nucleo.editores;

import com.uniditor.nucleo.entradas.CursorSimples;
import com.uniditor.nucleo.entradas.EntradaTextoSimples;
import com.uniditor.nucleo.Buffer;
import com.uniditor.nucleo.BufferSimples;
import java.util.Timer;
import java.util.TimerTask;
import com.uniditor.nucleo.graficos.Renderizador;
import com.uniditor.nucleo.Editor;
import com.uniditor.nucleo.entradas.EntradaTexto;

public class VisaoEditor extends Editor {
    public final float ESPACO_ESQ  = 12f;
    public final float ESPACO_TOPO = 12f;
    public float rolamentoY = 0f;

    public boolean cursorVisivel = true;
    public Timer relogio;
	public TimerTask piscaCursor;

    public final int COR_FUNDO = 0xFF1E1E1E;
    public final int COR_TEXTO = 0xFFE0E0E0;
    public final int COR_CURSOR = 0xFFFFFFFF;
    public final int COR_NUMERO_LINHA = 0xFF606060;
    public final int COR_sarjeta_FUNDO = 0xFF252525;

    public VisaoEditor(Renderizador render) {
        this("", render);
    }

    public VisaoEditor(String textoInicial, Renderizador render) {
        buffer = new BufferSimples(textoInicial);
        cursor = new CursorSimples();
        entrada = new EntradaTextoSimples(buffer, cursor);
		this.render = render;
        this.render.iniciar();
		
		if(relogio == null) relogio = new Timer();

		piscaCursor = new TimerTask() {
			@Override
			public void run() {
				cursorVisivel = !cursorVisivel;
			}
		};
        relogio.schedule(piscaCursor, 500, 500);
    }

	@Override
    public void aoTocar(float x, float y) {
		abrirTeclado();
		moverCursorToque(x, y);
		cursorVisivel = true;
    }

	@Override
    public void abrirTeclado() {
		entrada.teclado.abrirTeclado();
    }

    public void moverCursorToque(float tX, float tY) {
        float altLinha = render.alturaLinha();
        float sarjetaLarg = larguraSarjeta();

        float yRelativo = tY - ESPACO_TOPO + rolamentoY;
        int linha = (int)(yRelativo / altLinha);
        linha = Math.max(0, Math.min(linha, buffer.totalLinhas() - 1));

        float xRelativo = tX - sarjetaLarg - ESPACO_ESQ;
        String conteudo = buffer.linha(linha);
        int coluna = 0;
        float acumulado = 0f;
        for(int i = 0; i < conteudo.length(); i++) {
            float largChar = render.larguraCaractere(conteudo.charAt(i));
            if(acumulado + largChar / 2f > xRelativo) break;
            acumulado += largChar;
            coluna = i + 1;
        }
        cursor.def(linha, coluna);
        garantirCursorVisivel();
    }
	@Override
    public void garantirCursorVisivel() {
        float altLinha = render.alturaLinha();
        float yCursor = ESPACO_TOPO + cursor.linha() * altLinha;
        float tela = render.largura;

        if(yCursor - rolamentoY < 0) {
            rolamentoY = yCursor;
        } else if(yCursor - rolamentoY + altLinha > tela) {
            rolamentoY = yCursor + altLinha - tela;
        }
    }

    public float larguraSarjeta() {
        int digitos = String.valueOf(buffer.totalLinhas()).length();
        return render.larguraTexto("0") * (digitos + 1) + ESPACO_ESQ;
    }

    public void att() {
		if(render.pause) return;
        render.iniciarQuadro();
        render.limpar(COR_FUNDO);

        float altLinha = render.alturaLinha();
        float sarjetaLarg = larguraSarjeta();
        float baseline = render.ascente();

        // área de texto com recorte
        render.addRecorte(sarjetaLarg, 0, render.largura - sarjetaLarg, render.altura);
        render.defPos(0, -rolamentoY);

        // linhas visiveis
        int primeiraLinha = Math.max(0, (int)(rolamentoY / altLinha));
        int ultimaLinha = Math.min(buffer.totalLinhas() - 1,
		(int)((rolamentoY + render.largura) / altLinha) + 1);

        render.defCorTexto(COR_TEXTO);
        for(int i = primeiraLinha; i <= ultimaLinha; i++) {
            float y = ESPACO_TOPO + i * altLinha + baseline;
            render.renderTexto(buffer.linha(i), sarjetaLarg + ESPACO_ESQ, y);
        }
        // cursor
        if(cursorVisivel) {
            float xCursor = sarjetaLarg + ESPACO_ESQ
				+ render.larguraTexto(buffer.linha(cursor.linha()).substring(0, cursor.coluna()));
            float yCursor = ESPACO_TOPO + cursor.linha() * altLinha;
            render.renderRetangulo(xCursor, yCursor, 2f, altLinha, COR_CURSOR);
        }
        render.subRecorte();

        render.renderRetangulo(0, 0, sarjetaLarg, render.altura, COR_sarjeta_FUNDO);
        render.defCorTexto(COR_NUMERO_LINHA);
        for(int i = primeiraLinha; i <= ultimaLinha; i++) {
            float y = ESPACO_TOPO + i * altLinha + baseline - rolamentoY;
            String num = String.valueOf(i + 1);
            float xNum = sarjetaLarg - render.larguraTexto(num) - ESPACO_ESQ;
            render.renderTexto(num, xNum, y);
        }
        render.fimQuadro();
    }

    public void defTexto(String texto) {
        // substitui o conteudo inteiro
        buffer.defTexto(texto);
        cursor.def(0, 0);
        rolamentoY = 0f;
    }
}

