package com.uniditor.nucleo.graficos;

public interface Renderizador {
	public int altura, largura;
	public boolean pause;
    // === ciclo de vida ===
    void iniciar();
    void liberar();
    void iniciarQuadro(); // chamado no inicio de cada qudro
    void fimQuadro(); // chamado no fim
	void defAPI(Object renderizacao);

    // === fundo ===
    void limpar(int cor);

    // === texto ===
    void defFonte(String familia, float tam);
    void defCorTexto(int cor);
    void renderTexto(String texto, float x, float y);
    void renderTextoCor(String texto, Cor[] cores, float x, float y);

    // === formas ===
    void renderRetangulo(float x, float y, float largura, float altura, int cor);
    void renderRetanguloContorno(float x, float y, float largura, float altura, int cor, float espessura);

    // === metricas(necessario pra VisaoEditor calcular tela) ===
    float larguraTexto(String texto);
    float larguraCaractere(char c);
    float alturaLinha();
    float ascente();
    float descente();

    // === recorte/rolamento ===
    void addRecorte(float x, float y, float largura, float altura);
    void subRecorte();
    void defPos(float dx, float dy); // translação global(rolamento)

    // === dimensões da superficie ===
	void ajustar(int altura, int largura);
    int larguraTela();
    int alturaTela();
}
