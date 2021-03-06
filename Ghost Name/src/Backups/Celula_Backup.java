package Backups;

import sek2016.Posicao;

public class Celula_Backup {

	public static float commonSize = 0.2f; // tamanho padr�o das c�lulas, cm

	private float size; // tamanho da c�lula
	private boolean occupied = true;// est� ocupado?
	private Posicao posicao;// posi��o da c�lula no mapa
	public void setPosicao(Posicao posicao) {
		this.posicao = posicao;
	}

	private boolean checked;// est� checado?
	private int typeOfObject;
	private boolean temParede;

	public int f;
	public int g;
	public int h;
	public Celula_Backup parent;

	Celula_Backup(Posicao posicao) {
		this(commonSize, true, posicao, true);
	}

	Celula_Backup(int size, Posicao posicao, boolean checked) {
		this.size = size;
		this.occupied = true;
		this.posicao = posicao;
		this.checked = checked;
	}

	public Celula_Backup(float size, boolean occupied, Posicao posicao, boolean checked) {
		this.size = size;
		this.occupied = occupied;
		this.checked = checked;
		this.f = 0;
		this.g = 0;
		this.h = 0;
		this.parent = null;
		this.posicao = posicao;
	}

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

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
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

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return this.checked;
	}
}