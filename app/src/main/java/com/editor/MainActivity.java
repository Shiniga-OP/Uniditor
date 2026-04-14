package com.editor;

import android.app.Activity;
import android.os.Bundle;

import com.uniditor.android.ConfigAndroid;
import com.uniditor.android.graficos.Canvas;
import com.uniditor.android.EditorAndroidCanvas;

import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.nucleo.sintaxe.cpp.TokenizadorCpp;
import com.uniditor.nucleo.fontes.Fonte;
import com.uniditor.nucleo.Util;

import java.io.File;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
		new ConfigAndroid(this);
		
		VisaoEditor visaoEditor = new VisaoEditor(
			new Canvas(
				0xFFFFFFFF, // cor das formas
				0xFF1E1E1E // cor do fundo
			), // renderizador
			new TokenizadorCpp() // regras de sintaxe
		);
		
		visaoEditor.render.defFonte(new Fonte() {
				@Override
				public File arquivo() {
					return Util.arquivo.copiarArquivoAssets("firacode-regular.ttf");
				}

				@Override
				public String familia() {
					return "monospace";
				}

				@Override
				public float tamanho() {
					return 24f;
				}
		});
		EditorAndroidCanvas editor = new EditorAndroidCanvas(this, visaoEditor);
        setContentView(editor);
    }
}
