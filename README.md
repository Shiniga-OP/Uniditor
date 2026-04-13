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
import com.uniditor.nucleo.sintaxe.cpp.TokenizadorCpp;

public class SuaActivity extends Activity {
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

```

## feito atualmente:
1. Editor de texto básico.
2. Quantidade de linhas ao lado.
3. Pressionar abre o menu de opções(Copiar, Colar, Recortar, Selecionar tudo) acima, e seleciona a palavra inteira.
4. Pressionar + Arrastar, seleciona tudo onde for arrastado, além de auto-rolamento.
4. Arrastar, rolamento suave para navegação entre as linhas com inércia.
5. Destaque de sintaxe básico pra C++.
6. Auto Identação.
7. Fechamento de chaves e aspas automatico.