package com.editor;

import android.app.Activity;
import android.os.Bundle;

import com.uniditor.android.ConfigAndroid;
import com.uniditor.android.graficos.Canvas;
import com.uniditor.android.EditorAndroidCanvas;

import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.nucleo.sintaxe.TokenizadorJava;
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
			new TokenizadorJava() // regras de sintaxe
		);
		
		visaoEditor.render.defFonte(
			Util.arquivo.copiarAssets("firacode-regular.ttf")
		);
		EditorAndroidCanvas editor = new EditorAndroidCanvas(this, visaoEditor);
        setContentView(editor);
    }
}
