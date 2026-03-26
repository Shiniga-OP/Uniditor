package com.uniditor.android.graficos;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.uniditor.nucleo.graficos.Cor;
import com.uniditor.nucleo.graficos.Renderizador;

import java.util.ArrayDeque;
import com.uniditor.nucleo.sintaxe.Token;

public class Canvas implements Renderizador {
    public android.graphics.Canvas canvas;

    public final Paint pincelTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint pincelForma = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final Paint pincelFundo = new Paint();

    public final ArrayDeque<Rect> pilhaRecorte = new ArrayDeque<>();
	
	public Canvas() {
		pause = true;
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
    public void iniciar() {
        pincelTexto.setTypeface(Typeface.MONOSPACE);
        pincelTexto.setColor(0xFFE0E0E0);
        pincelForma.setColor(0xFFFFFFFF);
        pincelFundo.setColor(0xFF1E1E1E);
		defTextoTam(25f);
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
    public void defFonte(String familia, float tam) {
        Typeface tf = familia != null
			? Typeface.create(familia, Typeface.NORMAL)
			: Typeface.MONOSPACE;
        pincelTexto.setTypeface(tf);
        pincelTexto.setTextSize(tam);
    }

    @Override
    public void defCorTexto(int cor) {
        pincelTexto.setColor(cor);
    }

    @Override
    public void renderTexto(String texto, float x, float y) {
        canvas.drawText(texto, x, y, pincelTexto);
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
			Cor c = token.cor;
			
			// 1. desenha o trecho sem estilo(antes do token)
			if(pos < c.inicio) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
				pincelTexto.setColor(corOriginal);
				float ox = x + pincelTexto.measureText(texto, 0, pos);
				canvas.drawText(texto, pos, c.inicio, ox, y, pincelTexto);
			}
			// 2. aplica estilo e desenha o token
			if(token.negrito && token.italico) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
			} else if(token.negrito) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			} else if(token.italico) {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
			} else {
				pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			}
			if(c.inicio < c.fim && c.fim <= texto.length()) {
				pincelTexto.setColor(c.valor);
				float ox = x + pincelTexto.measureText(texto, 0, c.inicio);
				canvas.drawText(texto, c.inicio, c.fim, ox, y, pincelTexto);
				pos = c.fim;
			}
		}
		// 3. desenha o trecho restante sem estilo
		if(pos < texto.length()) {
			pincelTexto.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
			pincelTexto.setColor(corOriginal);
			float ox = x + pincelTexto.measureText(texto, 0, pos);
			canvas.drawText(texto, pos, texto.length(), ox, y, pincelTexto);
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
        return pincelTexto.measureText(texto);
    }

    @Override
    public float larguraCaractere(char c) {
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

