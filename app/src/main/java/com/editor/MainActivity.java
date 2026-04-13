package com.editor;

import android.app.Activity;
import android.os.Bundle;
import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.android.EditorAndroidCanvas;
import com.uniditor.android.graficos.Canvas;
import com.uniditor.nucleo.sintaxe.cpp.TokenizadorCpp;
import com.uniditor.nucleo.fontes.Fonte;
import java.io.File;
import java.io.IOException;
import com.arquivos.ArquivosUtil;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
		
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
					try {
						return ArquivosUtil.copiarArquivoAssets(
							getAssets().open("firacode-regular.ttf"),
							getCacheDir(), "firecode.ttf"
						);
					} catch(IOException e) {
						msg("[ERRO]: "+e);
						return null;
					}
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
	
	public void msg(String txt) {
		Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
	}
}
