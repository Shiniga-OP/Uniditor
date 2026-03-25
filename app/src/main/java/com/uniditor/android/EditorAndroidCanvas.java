package com.uniditor.android;

import android.view.View;
import android.graphics.Canvas;
import com.uniditor.nucleo.Editor;
import android.view.MotionEvent;
import android.content.Context;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.uniditor.nucleo.entradas.Teclado;

public class EditorAndroidCanvas extends View {
	public Editor editor;
	public View ISSO;
	
	public EditorAndroidCanvas(Context ctx, Editor editor) {
		super(ctx);
		this.editor = editor;
		
		this.ISSO = this;
		this.editor.entrada.teclado = new Teclado() {
			@Override
			public void abrirTeclado() {
				InputMethodManager imm = (InputMethodManager)getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
				if(imm != null) imm.showSoftInput(ISSO, InputMethodManager.SHOW_IMPLICIT);
			}
		};
		setFocusable(true);
        setFocusableInTouchMode(true);
	}
	
	@Override
	protected void onDraw(android.graphics.Canvas canvas) {
		editor.render.defAPI(canvas);
		editor.render.ajustar(getWidth(), getHeight());
		editor.att();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		requestFocus();
		editor.aoTocar(e.getX(), e.getY());
		invalidate();
		return true;
	}
	
	@Override
    public InputConnection onCreateInputConnection(EditorInfo info) {
        info.inputType = android.text.InputType.TYPE_CLASS_TEXT
			| android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
			| android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        info.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION;

        return new BaseInputConnection(this, false) {
            @Override
            public boolean commitText(CharSequence texto, int novoCursor) {
                editor.entrada.add(texto.toString());
                editor.garantirCursorVisivel();
                invalidate();
                return true;
            }

            @Override
            public boolean deleteSurroundingText(int antes, int depois) {
                for(int i = 0; i < antes;  i++) editor.entrada.rmAntes();
                for(int i = 0; i < depois; i++) editor.entrada.rmDepois();
                editor.garantirCursorVisivel();
                invalidate();
                return true;
            }

            @Override
            public boolean sendKeyEvent(android.view.KeyEvent evento) {
                // teclas de seta via teclado fisico/bluetooth
                if(evento.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    switch(evento.getKeyCode()) {
                        case android.view.KeyEvent.KEYCODE_DPAD_UP:
                            editor.cursor.mover(-1, 0, editor.buffer); break;
                        case android.view.KeyEvent.KEYCODE_DPAD_DOWN:
                            editor.cursor.mover(1, 0, editor.buffer);  break;
                        case android.view.KeyEvent.KEYCODE_DPAD_LEFT:
                            editor.cursor.mover(0, -1, editor.buffer); break;
                        case android.view.KeyEvent.KEYCODE_DPAD_RIGHT:
                            editor.cursor.mover(0, 1, editor.buffer);  break;
                        case android.view.KeyEvent.KEYCODE_DEL:
                            editor.entrada.rmAntes(); break;
                        case android.view.KeyEvent.KEYCODE_FORWARD_DEL:
                            editor.entrada.rmDepois(); break;
                        default:
                            return super.sendKeyEvent(evento);
                    }
                    editor.garantirCursorVisivel();
                    invalidate();
                    return true;
                }
                return super.sendKeyEvent(evento);
            }
        };
    }
	
	@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        editor.render.liberar();
    }
}
