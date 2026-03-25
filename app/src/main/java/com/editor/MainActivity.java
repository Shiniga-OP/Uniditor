package com.editor;

import android.app.Activity;
import android.os.Bundle;
import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.android.EditorAndroidCanvas;
import com.uniditor.android.graficos.Canvas;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
		EditorAndroidCanvas editor = new EditorAndroidCanvas(this, new VisaoEditor(new Canvas()));
        setContentView(editor);
    }
}

