package sek2016;

public class Posicao {

	public int x;// Linha da matriz
	public int y;// Coluna da matriz

	public Posicao(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLinha(int linha){
		this.x = linha;
	}
	
	public void setColuna(int coluna){
		this.y = coluna;
	}
}
