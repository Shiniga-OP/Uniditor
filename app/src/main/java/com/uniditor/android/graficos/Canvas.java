package com.uniditor.android.graficos;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.uniditor.nucleo.graficos.Cor;
import com.uniditor.nucleo.graficos.Renderizador;

import java.util.ArrayDeque;
import com.uniditor.nucleo.sintaxe.Token;
import java.io.File;
import com.uniditor.nucleo.fontes.Fonte;

public class Canvas implements Renderizador {
    public android.graphics.Canvas canvas;

    public final Paint pincelTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint pincelForma = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint pincelFundo = new Paint();

    public final ArrayDeque<Rect> pilhaRecorte = new ArrayDeque<>();

    public int TAM_TAB = 4;

	public Canvas(int corForma, int corFundo) {
		pause = true;
		pincelForma.setColor(corForma);
        pincelFundo.setColor(corFundo);
		defTextoTam(25f);
	}

	public void defAPI(Object canvas) {
		if(canvas instanceof android.graphics.Canvas) {
			this.canvas = (android.graphics.Canvas)canvas;
			pause = false;
		}
	}

	@Override
	public void defTextoTam(float tam) {
		pincelTexto.setTextSize(tam);
	}

	@Override
	public void ajustar(int h, int v) {
		this.altura = v;
		this.largura = h;
	}

    @Override
    public void liberar() {
        canvas = null;
        pilhaRecorte.clear();
    }

    @Override
    public void iniciarQuadro() {
        pilhaRecorte.clear();
        canvas.save();
    }

    @Override
    public void fimQuadro() {
        canvas.restore();
    }

    @Override
    public void limpar(int cor) {
        pincelFundo.setColor(cor);
        canvas.drawRect(0, 0, largura, altura, pincelFundo);
    }

    @Override
	public void defFonte(Fonte fonte) {
		Typeface tf = Typeface.createFromFile(fonte.arquivo());
		pincelTexto.setTypeface(tf);
		pincelTexto.setTextSize(fonte.tamanho());
	}

    @Override
    public void defCorTexto(int cor) {
        pincelTexto.setColor(cor);
    }

    @Override
    public void renderTexto(String texto, float x, float y) {
        if(texto.indexOf('\t') < 0) {
            canvas.drawText(texto, x, y, pincelTexto);
            return;
        }
        float largTab = pincelTexto.measureText(" ") * TAM_TAB;
        float ox = x;
        int inicio = 0;
        for(int i = 0; i <= texto.length(); i++) {
            if(i == texto.length() || texto.charAt(i) == '\t') {
                if(i > inicio) {
                    canvas.drawText(texto, inicio, i, ox, y, pincelTexto);
                    ox += pincelTexto.measureText(texto, inicio, i);
                }
                if(i < texto.length()) ox += largTab;
                inicio = i + 1;
            }
        }
    }

    @Override
	public void renderTextoCor(String texto, Token[] cores, float x, float y) {
		if(cores == null || cores.length == 0) {
			renderTexto(texto, x, y);
			return;
		}
		int corOriginal = pincelTexto.getColor();
		int pos = 0;

		for(Token token : cores) {
			if(token.negrito && token.italico) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
			} else if(token.negrito) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			} else if(token.italico) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
			} else {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			}
			if(token.inicio < token.fim && token.fim <= texto.length()) {
				pincelTexto.setColor(token.cor);
				float ox = x + larguraTexto(texto.substring(0, token.inicio));
				renderTexto(texto.substring(token.inicio, token.fim), ox, y);
				pos = token.fim;
			}
		}
		if(pos < texto.length()) {
			pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			pincelTexto.setColor(corOriginal);
			float ox = x + larguraTexto(texto.substring(0, pos));
			renderTexto(texto.substring(pos), ox, y);
		}
		pincelTexto.setColor(corOriginal);
	}

    @Override
    public void renderRetangulo(float x, float y, float largura, float altura, int cor) {
        pincelForma.setStyle(Paint.Style.FILL);
        pincelForma.setColor(cor);
        canvas.drawRect(x, y, x + largura, y + altura, pincelForma);
    }

    @Override
    public void renderRetanguloContorno(float x, float y, float largura, float altura, int cor, float espessura) {
        pincelForma.setStyle(Paint.Style.STROKE);
        pincelForma.setStrokeWidth(espessura);
        pincelForma.setColor(cor);
        canvas.drawRect(x, y, x + largura, y + altura, pincelForma);
    }

    @Override
    public float larguraTexto(String texto) {
        if(texto.indexOf('\t') < 0) return pincelTexto.measureText(texto);
        float total = 0f;
        float largTab = pincelTexto.measureText(" ") * TAM_TAB;
        for(int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if(c == '\t') total += largTab;
            else total += pincelTexto.measureText(texto, i, i + 1);
        }
        return total;
    }

    @Override
    public float larguraCaractere(char c) {
        if (c == '\t') return pincelTexto.measureText(" ") * TAM_TAB;
        return pincelTexto.measureText(String.valueOf(c));
    }

    @Override
    public float alturaLinha() {
        Paint.FontMetrics fm = pincelTexto.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    @Override
    public float ascente() {
        return -pincelTexto.getFontMetrics().ascent;
    }

    @Override
    public float descente() {
        return pincelTexto.getFontMetrics().descent;
    }

    @Override
    public void addRecorte(float x, float y, float largura, float altura) {
        canvas.save();
        Rect r = new Rect((int)x, (int)y, (int)(x + largura), (int)(y + altura));
        pilhaRecorte.push(r);
        canvas.clipRect(r);
    }

    @Override
    public void subRecorte() {
        if(!pilhaRecorte.isEmpty()) {
            pilhaRecorte.pop();
            canvas.restore();
        }
    }

    @Override
    public void defPos(float dx, float dy) {
        canvas.translate(dx, dy);
    }

    @Override
    public int larguraTela() {
        return largura;
    }

    @Override
    public int alturaTela() {
        return altura;
    }
}
