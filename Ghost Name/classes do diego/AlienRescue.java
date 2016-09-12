package sek2016;

import lejos.hardware.Sound;
import lejos.utility.Delay;
import sek2016.Celula.STATUS;

/**
 * 
 * @author Equipe Sek 2016:<br>
 *         Diego Costa<br>
 *         Karinny Golçalves<br>
 *         Lucas *não sei o sobrenome*<br>
 *         Mariana *nao sei o sobrenome*<br>
 *         Rogério Pereira Batista - Eng. Elétrica<br>
 */
public class AlienRescue implements Runnable {
	/**
	 * Variavel global que indica se a Thread do programa está executando (ON)
	 * ou fechada (OFF)
	 */
	public static boolean alienRescueON;

	/**
	 * Thread que comanda a execução do PID
	 */
	public static Thread threadPID;

	// =========================Constantes de processo=========================

	private static final float PI = 3.141592f;
	private static final float CELL_SIZE = Celula.commonSize;// tamanho da
																// celula em
																// Metros
	private static final int COL_AMT = 9; // quantidade de colunas na matriz
	private static final int LIN_AMT = 9;// quantidade de linhas na matriz

	// =======================Variaveis do mapa==============================

	private final static Celula[][] CENTRAL_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] CAVE_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] OBSTACLE_MAP = new Celula[LIN_AMT][COL_AMT];

	private static Posicao inputCell = new Posicao(0, 4);
	private static Posicao caveEntrance;
	private static Posicao caveExit;
	private static Posicao obstacleEntrace;
	private static Posicao obstacleExit;


	// ====================Variaveis de processo===============================

	private static Posicao robotPosition = inputCell; // posição de entrada

	// ========================================================================
	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override

	public void run() {
		try { // o codigo deve ficar dentro desse try gigante
				// ======INICIO DO CODIGO======================================
			threadPID = new Thread(new PID());
			threadPID.setDaemon(true);
			threadPID.setName("threadPID");
			PID.pidRunning = true;
			threadPID.start();
			Navigation.setAcceleration(500, 500);

			victorySong();
			boolean captured = false;
			Navigation.openGarra();
			Navigation.forward(0.3f);
			Navigation.setVelocidade(360);
			boolean temp = true;// só temporária, até resolver uns bugs aí
			
			while(temp){
				cellExchanger();
				
				if (allowedReading() && !captured){
					if (Sensors.verificaObstaculo()){
						Navigation.stop();
						Navigation.closeGarra();
						/*
						 * codigo de checagem de cor
						 */
						
						alienRescueON = false;
						captured = true;

						
					}else{
						
					}

				}
				
			}
			
			/*
			while (!captured) {
				 
				if (allowedReading() == true && !captured) {
					Navigation.stop();
					Navigation.closeGarra();
				} 
			}
			*/
			// ======FINAL DO CODIGO=====================================
			alienRescueON = false;
		} catch (ThreadDeath e) {// quando o menu é chamado, essa thread é
									// desligada e lança essa exception
			e.getStackTrace();
		}
	}

	/**
	 * Reproduz o alegre som de sambar na cara das inimigas
	 */
	private static void victorySong() {
		Sound.setVolume(50);
		Sound.playTone(3000, 100);
		Sound.playTone(4000, 100);
		Sound.playTone(4500, 100);
		Sound.playTone(5000, 100);
		Delay.msDelay(80);
		Sound.playTone(3000, 200);
		Sound.playTone(5000, 500);
	}

	// ======================Logica de captura, mapeamento e retorno============

	private static void cellExchanger() {
		float tacho = Navigation.getTacho("B");
		float dist = (2 * PI * Navigation.RAIO) * tacho;
		if (dist >= CELL_SIZE) {
			newPosition();
			Navigation.resetTacho();
		}
	}

	private static void newPosition() {
		if (Navigation.orientation == Navigation.FRONT) {
			robotPosition.setLinha(robotPosition.x + 1);
		}

		else if (Navigation.orientation == Navigation.BACK) {
			robotPosition.setLinha(robotPosition.x - 1);
		}

		else if (Navigation.orientation == Navigation.LEFT) {
			robotPosition.setColuna(robotPosition.y + 1);
		}

		else if (Navigation.orientation == Navigation.RIGTH) {
			robotPosition.setColuna(robotPosition.y - 1);
		}

	}

	private static boolean allowedReading() {

		if (robotPosition.x == (LIN_AMT - 1) && (Navigation.orientation == Navigation.FRONT
				|| Navigation.orientation == Navigation.LEFT || Navigation.orientation == Navigation.RIGTH)) {

			return false;

		} else if (robotPosition.x == 0 && (Navigation.orientation == Navigation.BACK
				|| Navigation.orientation == Navigation.LEFT || Navigation.orientation == Navigation.RIGTH)) {

			return false;

		} else if (robotPosition.y == (COL_AMT - 1) && (Navigation.orientation == Navigation.LEFT
				|| Navigation.orientation == Navigation.BACK || Navigation.orientation == Navigation.FRONT)) {

			return false;

		} else if (robotPosition.y == 0 && (Navigation.orientation == Navigation.RIGTH
				|| Navigation.orientation == Navigation.BACK || Navigation.orientation == Navigation.FRONT)) {

			return false;

		}

		else {

			return true;

		}
	}
	
	private static void frontRobotCell(){
		
	}

	public static void iniciaPerifericoFrenteDireita(int cave) {
		/*
		 * Chama o launcher dos mapas
		 */
		mapLauncher();
		
		switch (cave){
		
		case EV3MainMenuClass.CAV_DIR:

			caveEntrance = new Posicao(4, 0);
			obstacleEntrace = new Posicao(8, 4);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			break;
			
		case EV3MainMenuClass.CAV_CIMA:
			caveEntrance = new Posicao(8, 4);
			obstacleEntrace = new Posicao(4, 0);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			break;
		}
		
	}
	
	public static void iniciaPerifericoFrenteEsquerda(int cave){
		/*
		 * chama o launcher dos mapas
		 */
		mapLauncher();
		
		switch (cave){
		
		case EV3MainMenuClass.CAV_CIMA:
			caveEntrance = new Posicao(8, 4);
			obstacleEntrace = new Posicao(4, 8);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			
			break;
		
		case EV3MainMenuClass.CAV_ESQ:
			caveEntrance = new Posicao(4, 8);
			obstacleEntrace = new Posicao(8, 4);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			
			break;
		}
		
	}
	
	public static void iniciaPerifericoLateral(int cave){
		/*
		 * chama o launcher dos mapas
		 */
		mapLauncher();
		
		switch (cave){
		
		case EV3MainMenuClass.CAV_ESQ:
			caveEntrance = new Posicao(4, 8);
			obstacleEntrace = new Posicao(4, 0);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			
			break;
			
		case EV3MainMenuClass.CAV_DIR:
			caveEntrance = new Posicao(4, 0);
			obstacleEntrace = new Posicao(4, 8);
			
			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			
			break;
		}
	}
	
	private static void mapLauncher(){
		/*
		 * Inicia o modulo central e perifericos com todas as celulas como não checadas
		 */
		for(int i = 0; i < LIN_AMT; i++){
			for(int j = 0; j < COL_AMT; j++){
				CENTRAL_MAP[i][j] = new Celula(new Posicao(i, j), STATUS.unchecked);
				CAVE_MAP[i][j] = new Celula(new Posicao(i, j), STATUS.unchecked);
				OBSTACLE_MAP[i][j] = new Celula(new Posicao(i, j), STATUS.unchecked);

			}
		}
	}

}
