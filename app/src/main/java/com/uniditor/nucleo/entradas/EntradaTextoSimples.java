package com.uniditor.nucleo.entradas;

import com.uniditor.nucleo.Buffer;

public class EntradaTextoSimples implements EntradaTexto {
    public final Buffer buffer;
    public final Cursor cursor;

    public EntradaTextoSimples(Buffer buffer, Cursor cursor) {
        this.buffer = buffer;
        this.cursor = cursor;
    }

    @Override
    public void add(String texto) {
        buffer.add(cursor.linha(), cursor.coluna(), texto);

        // recalcula posição depois da inserção
        if(texto.contains("\n")) {
            String[] partes = texto.split("\n", -1);
            int novaLinha = cursor.linha() + partes.length - 1;
            int novaColuna = partes[partes.length - 1].length();
            // se havia texto depois do cursor na linha original, ele vai pra linha nova
            // a coluna da ultima parte do texto inserido é a posição correta
            cursor.def(novaLinha, novaColuna);
        } else {
            cursor.def(cursor.linha(), cursor.coluna() + texto.length());
        }
    }

    @Override
    public void rmAntes() {
        int linha = cursor.linha();
        int coluna = cursor.coluna();

        if(coluna > 0) {
            // deleção simples dentro da linha
            buffer.rm(linha, coluna - 1, 1);
            cursor.def(linha, coluna - 1);
        } else if (linha > 0) {
            // cursor no início da linha  junta com a linha anterior
            int colunaAnterior = buffer.linha(linha - 1).length();
            buffer.rm(linha - 1, colunaAnterior, 1); // deleta o \n
            cursor.def(linha - 1, colunaAnterior);
        }
        // se linha == 0 e coluna == 0, não faz nada
    }

    @Override
    public void rmDepois() {
        int linha = cursor.linha();
        int coluna = cursor.coluna();
        String linhaAtual = buffer.linha(linha);

        if(coluna < linhaAtual.length()) {
            // deleção simples dentro da linha
            buffer.rm(linha, coluna, 1);
        } else if(linha < buffer.totalLinhas() - 1) {
            // cursor no fim da linha junta com a próxima
            buffer.rm(linha, coluna, 1); // deleta o \n
        }
        // cursor não muda de posição no deletarDepois
        cursor.clampar(buffer);
    }
}

