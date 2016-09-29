package sek2016;


public class Celula {
	enum Status {
		unchecked, empty, occupied;
	}

	public static final float commonSize = 0.192f; // tamanho padr�o das c�lulas,
													// m

	private float size = commonSize; // tamanho da c�lula
	private Status status = Status.unchecked;// est� ocupado?
	private Posicao posicao;// posi��o da c�lula no mapa

	private int typeOfObject;
	private boolean temParede;

	public int f;
	public int g;
	public int h;
	public Celula parent;

	public void setPosicao(Posicao posicao) {
		this.posicao = posicao;
	}

	Celula(Posicao posicao) {
		this(Status.empty, posicao, true);
	}

	Celula(Posicao posicao, Status status) {
		this.status = status;
		this.posicao = posicao;
	}

	public Celula(Status occupied, Posicao posicao, boolean checked) {
		this.status = occupied;
		this.f = 0;
		this.g = 0;
		this.h = 0;
		this.parent = null;
		this.posicao = posicao;
	}

	/**
	 * apaga os valores de f,g,h e do parent
	 */
	public void apagar() {
		this.f = 0;
		this.g = 0;
		this.h = 0;
		this.parent = null;
	}

	public int getTypeOfObject() {
		return typeOfObject;
	}

	public void setTypeOfObject(int typeOfObject) {
		this.typeOfObject = typeOfObject;
	}

	public float getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean temParede() {
		return temParede;
	}

	public void setParede(boolean temParede) {
		this.temParede = temParede;
	}

	public Posicao getPosicao() {
		return this.posicao;
	}
	
	public Status getStatus(){
		return this.status;
	}
	
	public void setStatus(Status status){
		this.status = status;
	}
}