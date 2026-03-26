package com.uniditor.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.uniditor.nucleo.Editor;
import com.uniditor.nucleo.entradas.Teclado;
import android.os.Handler;

public class EditorAndroidCanvas extends View {
    public Editor editor;
    public View ISSO;

    public static final float LIMIAR_DRAG = 8f;

    public float toqueInicioX, toqueInicioY;
    public boolean arrastando = false;
    public boolean pressionado = false;

    public ActionMode modoAcao = null;

    public static final int ID_COPIAR = 1;
    public static final int ID_RECORTAR = 2;
    public static final int ID_COLAR = 3;
    public static final int ID_SELECIONAR_TUDO = 4;

    public GestureDetector gestureDetector;
	
	public float veloRolamento = 0f;
	public float ultimoY = 0f;
	public long ultimoTempo = 0L;
	public Handler geren = new Handler();
	public Runnable loopInercia = null;
	public Runnable loopAutoRolamento = null;
	public float autoRolamentoDelta = 0f;

    public EditorAndroidCanvas(Context ctx, final Editor editor) {
        super(ctx);
        this.editor = editor;

        this.ISSO = this;
        this.editor.entrada.teclado = new Teclado() {
            @Override
            public void abrirTeclado() {
                InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.showSoftInput(ISSO, InputMethodManager.SHOW_IMPLICIT);
            }
        };

        gestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public void onLongPress(MotionEvent e) {
					pressionado = true;
					int[] pos = editor.posToque(e.getX(), e.getY());
					selecionarPalavra(pos[0], pos[1]);
					abrirModoAcao();
					invalidate();
				}
			});

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void selecionarPalavra(int linha, int coluna) {
        String conteudo = editor.buffer.linha(linha);
        if(conteudo.isEmpty()) return;
        coluna = Math.min(coluna, conteudo.length());

        int ini = coluna;
        int fim = coluna;

        while(ini > 0 && eSeparador(conteudo.charAt(ini - 1))) ini--;
        if(ini == coluna && coluna < conteudo.length() && eSeparador(conteudo.charAt(coluna))) {
            fim = coluna + 1;
            editor.entrada.iniciarSelecao(linha, ini);
            editor.entrada.attSelecao(linha, fim);
            return;
        }
        while(ini > 0 && !eSeparador(conteudo.charAt(ini - 1))) ini--;
        while(fim < conteudo.length() && !eSeparador(conteudo.charAt(fim))) fim++;

        editor.entrada.iniciarSelecao(linha, ini);
        editor.entrada.attSelecao(linha, fim);
    }

    public static boolean eSeparador(char c) {
        return Character.isWhitespace(c)
            || c == '.' || c == ',' || c == ';' || c == ':'
            || c == '!' || c == '?' || c == '(' || c == ')'
            || c == '[' || c == ']' || c == '{' || c == '}'
            || c == '"' || c == '\'' || c == '/' || c == '\\'
            || c == '-' || c == '+' || c == '=' || c == '<'
            || c == '>' || c == '&' || c == '|' || c == '@';
    }

    public final ActionMode.Callback padraoAcao = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode modo, Menu menu) {
            menu.add(0, ID_COPIAR, 0, "Copiar");
            menu.add(0, ID_RECORTAR, 1, "Recortar");
            menu.add(0, ID_COLAR, 2, "Colar");
            menu.add(0, ID_SELECIONAR_TUDO, 3, "Selecionar tudo");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode modo, Menu menu) {
            // desabilita Copiar e Recortar se não há seleção
            boolean temSelecao = editor.entrada.selecao().ativa && !editor.entrada.copiar().isEmpty();
            menu.findItem(ID_COPIAR).setEnabled(temSelecao);
            menu.findItem(ID_RECORTAR).setEnabled(temSelecao);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode modo, MenuItem item) {
            ClipboardManager clip = (ClipboardManager)
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);

            switch(item.getItemId()) {
                case ID_COPIAR:
                    String textoCopiar = editor.entrada.copiar();
                    if(!textoCopiar.isEmpty() && clip != null)
                        clip.setPrimaryClip(ClipData.newPlainText("texto", textoCopiar));
                    modo.finish();
                    return true;
                case ID_RECORTAR:
                    String textoRecortar = editor.entrada.copiar();
                    if(!textoRecortar.isEmpty() && clip != null) {
                        clip.setPrimaryClip(ClipData.newPlainText("texto", textoRecortar));
                        editor.entrada.rmSelecao();
                    }
                    modo.finish();
                    return true;
                case ID_COLAR:
                    if(clip != null && clip.hasPrimaryClip()) {
                        ClipData.Item it = clip.getPrimaryClip().getItemAt(0);
                        if(it != null && it.getText() != null)
                            editor.entrada.add(it.getText().toString());
                    }
                    modo.finish();
                    return true;
                case ID_SELECIONAR_TUDO:
                    editor.entrada.selecionarTudo(editor.buffer);
                    modo.invalidate();
                    invalidate();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode modo) {
            modoAcao = null;
            editor.entrada.limparSelecao();
            invalidate();
        }
    };

    public void abrirModoAcao() {
        if(modoAcao == null) modoAcao = startActionMode(padraoAcao);
        else modoAcao.invalidate();
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
		gestureDetector.onTouchEvent(e);

		switch(e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				toqueInicioX = e.getX();
				toqueInicioY = e.getY();
				ultimoY = e.getY();
				ultimoTempo = e.getEventTime();
				
				arrastando = false;
				pressionado = false;
				pararInercia();
				pararAutoRolamento();
				if(modoAcao != null) modoAcao.finish();
				editor.entrada.limparSelecao();
				break;
			case MotionEvent.ACTION_MOVE:
				if(pressionado) {
					// segurar + arrastar -> expande seleção com auto-rolamento
					int[] posAtual = editor.posToque(e.getX(), e.getY());
					editor.entrada.attSelecao(posAtual[0], posAtual[1]);

					float zona = editor.render.alturaLinha() * 2f;
					float yTela = e.getY();
					if(yTela < zona) {
						iniciarAutoRolamento(-(zona - yTela) * 0.4f, posAtual);
					} else if(yTela > editor.render.altura - zona) {
						iniciarAutoRolamento((yTela - (editor.render.altura - zona)) * 0.4f, posAtual);
					} else {
						pararAutoRolamento();
					}
					invalidate();
					break;
				}
				float dyAbs = Math.abs(e.getY() - toqueInicioY);
				float dxAbs = Math.abs(e.getX() - toqueInicioX);
				if(!arrastando && (dxAbs > LIMIAR_DRAG || dyAbs > LIMIAR_DRAG)) {
					arrastando = true;
				}
				if(arrastando) {
					float delta = ultimoY - e.getY();
					long dt = e.getEventTime() - ultimoTempo;
					if(dt > 0) veloRolamento = veloRolamento * 0.6f + (delta / dt * 16f) * 0.4f;
					
					aplicarRolamento(delta);
					ultimoY = e.getY();
					ultimoTempo = e.getEventTime();
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				pararAutoRolamento();
				if(pressionado) {
					pressionado = false;
					break;
				}
				if(arrastando) {
					arrastando = false;
					iniciarInercia();
				} else {
					editor.aoTocar(e.getX(), e.getY());
				}
				break;
		}
		invalidate();
		return true;
	}
	
	public void aplicarRolamento(float delta) {
		editor.rolamentoY += delta;
		float altTotal = editor.render.alturaLinha() * editor.buffer.totalLinhas() + editor.ESPACO_TOPO;
		float maxRolamento = Math.max(0, altTotal - editor.render.altura);
		editor.rolamentoY = Math.max(0, Math.min(editor.rolamentoY, maxRolamento));
	}

	public void iniciarInercia() {
		pararInercia();
		loopInercia = new Runnable() {
			@Override
			public void run() {
				if(Math.abs(veloRolamento) < 0.5f) {
					veloRolamento = 0f;
					loopInercia = null;
					return;
				}
				aplicarRolamento(veloRolamento);
				veloRolamento *= 0.92f;
				invalidate();
				geren.postDelayed(this, 16);
			}
		};
		geren.post(loopInercia);
	}

	public void pararInercia() {
		if(loopInercia != null) {
			geren.removeCallbacks(loopInercia);
			loopInercia = null;
		}
	}

	public void iniciarAutoRolamento(final float velocidade, final int[] posReferencia) {
		if(loopAutoRolamento != null) {
			autoRolamentoDelta = velocidade;
			return;
		}
		autoRolamentoDelta = velocidade;
		loopAutoRolamento = new Runnable() {
			@Override
			public void run() {
				if(loopAutoRolamento == null) return;
				aplicarRolamento(autoRolamentoDelta);
				// atualiza a seleção para a posição visível mais próxima do dedo
				int[] pos = posReferencia;
				editor.entrada.attSelecao(pos[0], pos[1]);
				invalidate();
				geren.postDelayed(this, 16);
			}
		};
		geren.post(loopAutoRolamento);
	}

	public void pararAutoRolamento() {
		if(loopAutoRolamento != null) {
			geren.removeCallbacks(loopAutoRolamento);
			loopAutoRolamento = null;
		}
		autoRolamentoDelta = 0f;
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
            public boolean sendKeyEvent(KeyEvent evento) {
                if(evento.getAction() == KeyEvent.ACTION_DOWN) {
                    switch(evento.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            editor.cursor.mover(-1, 0, editor.buffer); break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            editor.cursor.mover(1,  0, editor.buffer); break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            editor.cursor.mover(0, -1, editor.buffer); break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            editor.cursor.mover(0,  1, editor.buffer); break;
                        case KeyEvent.KEYCODE_DEL:
                            editor.entrada.rmAntes(); break;
                        case KeyEvent.KEYCODE_FORWARD_DEL:
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
		pararInercia();
		pararAutoRolamento();
		if(modoAcao != null) modoAcao.finish();
		editor.render.liberar();
	}
}

