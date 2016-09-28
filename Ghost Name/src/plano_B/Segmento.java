package plano_B;
/**
 * 
 * orientacao angular, cada modulo tera sua orientacao a partir da entrada
 * a parede da entrada sera paralela ao eixo x
 * ao mudar de modulo a orientacao muda, ou seja, a lista de segmentos muda
 *
 */
public class Segmento {
	//================Variaveis=============
	/**
	 * Angulo em graus em que o segmento que o robo andou faz com o eixo x
	 */
	public  int ang;
	public  float dist;
	public Segmento(int ang, float dist){
		this.ang = ang;
		this.dist = dist;
	}
}
