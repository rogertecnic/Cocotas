package plano_B;

import lejos.hardware.lcd.LCD;
import sek2016.Navigation;

/**
 * classe que rege todas as constantes do plano b
 * nao quis usar enum pois o enum faz é dificultar a atribuicao
 * de um valor numerico a uma constante
 * duvidas me pergunte, eu tenho um exemplo disso prontinho e vos mostro
 * @author Rogério
 *
 */
public interface Const {
	public static final float ACELERATION = Navigation.aceleration;
	public static final float VELO_INI = Navigation.VELO_INI;
	public static final float RAIO = Navigation.RAIO;


	/**
	 * Alinhamento do robo de acordo com o modulo que ele esta resgatando
	 */
	public static final int NORTE = 0;
	/**
	 * Alinhamento do robo de acordo com o modulo que ele esta resgatando
	 */
	public static final int SUL = 1;
	/**
	 * Alinhamento do robo de acordo com o modulo que ele esta resgatando
	 */
	public static final int LESTE = 2;
	/**
	 * Alinhamento do robo de acordo com o modulo que ele esta resgatando
	 */
	public static final int OESTE = 3;


	/**
	 *  @ARENA_A 1; configuracao da arena<br>
	 *  --r <br>
	 *	P C P <br>
	 *	--r <br>
	 */
	public static final int ARENA_A = 1;
	/**
	 *  @ARENA_B 2; configuracao da arena<br>
	 *  --P <br>
	 *	r C P <br>
	 *	--r <br>
	 */
	public static final int ARENA_B = 2;
	/**
	 *  @ARENA_C 3; configuracao da arena<br>
	 *  --r <br>
	 *	P C r <br>
	 *	--r <br>
	 */
	public static final int ARENA_C = 3;



	/**
	 *  @CAV_DIR 1; config da caverna
	 */
	public static final int CAV_DIR = 1;
	/**
	 *  @CAV_ESQ 2; config da caverna
	 */
	public static final int CAV_ESQ = 2;
	/**
	 *  @CAV_CIMA 3; config da caverna
	 */
	public static final int CAV_CIMA = 3;



	/**
	 * @CENTRAL 0; Indica qual modulo o robo esta/vai procurar
	 */
	public static final int CENTRAL = 0;
	/**
	 * @CCAVE 1; Indica qual modulo o robo esta/vai procurar
	 */
	public static final int CAVE = 1;
	/**
	 * @PAREDE 2; Indica qual modulo o robo esta/vai procurar
	 */
	public static final int OBSTACULO = 2;



	/**
	 * @BRANCO 3; indica a cor do boneco que ele deve/esta ou nao a resgatar
	 */
	public static final int BRANCO = 3;
	/**
	 * @VERMELHO 4; indica a cor do boneco que ele deve/esta ou nao a resgatar
	 */
	public static final int VERMELHO = 4;
	/**
	 * @PRETO 5; indica a cor do boneco que ele deve/esta ou nao a resgatar
	 */
	public static final int PRETO = 5;
	/**
	 * Retorna que o sensor de chao leu o chao branco
	 */
	public static final int FLOOR_BRANCO = 18;


	/**
	 * @VELO_PROCURA eh a velocidade que o robo usara para procurar o boneco
	 * essa velocidade eh 3/4 da velocidade de curva estabelecida na classe
	 * Navigation
	 */
	public static final float VELO_PROCURA = Navigation.VELO_CURVA*0.7f;
	/**
	 * @ANG_PROCURA angulo de abertura para o robo virar de um lado para o outro procurando
	 * valor padrao 45 graus.
	 */
	public static final int ANG_PROCURA = 60;
	/**
	 * @DIST_FRENTE_PROCURA distancia que o robo vai ir par afrente
	 * apos ter visto um boneco, o robo vai ate o boneco para pega-lo
	 */
	public static final float DIST_FRENTE_CAPTURA = 0.20f;
	/**
	 * @T_PARAR_APOS_VER_BONECO tempo que o robo demorara a chamar o metodo
	 * stop depois que ver um boneco, ajuda o robo a alinhar reto com o boneco
	 * valor padrao 200;
	 */
	public static final int T_PARAR_APOS_VER_BONECO = 10;


	/**
	 * @LADO_MODULO_CENTRAL comprimento da lateral do modulo central
	 * o modulo central é um quadrado, esta no edital, valor medio 1.85f
	 */
	public static final float LADO_MODULO_CENTRAL = 1.82f;
	/**
	 * @LADO_MODULO_RESGATE comprimento da lateral do modulo central
	 * o modulo central é um quadrado, esta no edital, valor medio 1.85f
	 */
	public static final float LADO_MODULO_RESGATE = 0.5f;
	/**
	 * @RAIO_CAVE raio da caverna
	 */
	public static final float RAIO_CAVE = 0.22f;
	/**
	 * @PROFUNDIDADE_BUNDA_ROBO distancia da traseira do robo ate o centro do robo
	 * serve para fazer o ajuste fino da saida do robo do modulo de resgate
	 */
	public static final float PROFUNDIDADE_BUNDA_ROBO = 0.115f;
	/**
	 * @DIST_EIXO_GARRA distancia da ponta da garra fechada ate o 
	 * eixo do robo
	 */
	public static final float DIST_EIXO_GARRA_FECHADA = 0.139f;
}
