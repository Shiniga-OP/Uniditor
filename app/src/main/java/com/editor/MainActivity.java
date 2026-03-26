package com.editor;

import android.app.Activity;
import android.os.Bundle;
import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.android.EditorAndroidCanvas;
import com.uniditor.android.graficos.Canvas;
import com.uniditor.nucleo.sintaxe.cpp.TokenizadorCpp;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
		
		VisaoEditor visaoEditor = new VisaoEditor(
			new Canvas(), // renderizador
			new TokenizadorCpp() // regras de sintaxe
		);
		
		EditorAndroidCanvas editor = new EditorAndroidCanvas(this, visaoEditor);
        setContentView(editor);
    }
}

