# Uniditor
É um editor de código que pode abrigar diferentes extensões.
Com sua estrutura separada em modúlos, é possivel utilizar formas distintas de renderização.

## Como usar?
Para usar o editor padrão exclusivo do Android por enquanto. Esta é a configuração:
```Java
import android.app.Activity;
import android.os.Bundle;
import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.android.EditorAndroidCanvas;
import com.uniditor.android.graficos.Canvas;

public class SuaActivity extends Activity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        VisaoEditor visaoEditor = new new VisaoEditor(
            new Canvas() // canvas como renderizador
        );
		EditorAndroidCanvas editor = new EditorAndroidCanvas(this, visaoEditor); // componente necessario no Android
        setContentView(editor);
    }
}
```

## feito atualmente:
1. Editor de texto básico.
2. Quantidade de linhas ao lado.
3. (Android)Arrastar/Pressionar abre o menu de opções(Copiar, Colar, Recortar). Além de selecionar pressionando ou arrastando o dedo.