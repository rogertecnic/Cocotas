package sek2016;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import sek2016.Celula.Status;

/**
 * 
 * @author Equipe Sek 2016:<br>
 *         Diego Costa - Eng. da Computacao<br>
 *         Karinny Golçalves<br>
 *         Lucas Simões - Eng. da Computacao<br>
 *         Mariana Moreno - Eng. Mecânica<br>
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
	/**
	 * Thread que permite que a tacometria seja usada
	 */
	private static Thread threadTacometria;

	// =========================CONSTANTES DE PROCESSO=========================

	private static final int COL_AMT = 9; // quantidade de colunas na matriz
	private static final int LIN_AMT = 9;// quantidade de linhas na matriz

	// =======================VARIAVEIS DO MAPA==============================
	private final static Celula[][] CENTRAL_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] CAVE_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] OBSTACLE_MAP = new Celula[LIN_AMT][COL_AMT];

	public static Posicao inputCell = new Posicao(0, 4);
	private static Posicao caveEntrance;
	private static Posicao caveExit;
	private static Posicao obstacleEntrace;
	private static Posicao obstacleExit;

	// ========================================================================
	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		Navigation.garraFechada = false;
		try { // o codigo deve ficar dentro desse try gigante
				// ======INICIO DO
				// CODIGO=============================================================
			/*
			 * Thread da PID é iniciada aqui.
			 */
			threadPID = new Thread(new PID());
			threadPID.setDaemon(true);
			threadPID.setName("threadPID");
			PID.pidRunning = true;
			threadPID.start();

			/*
			 * Thread da Tacometria é iniciada aqui.
			 */
			threadTacometria = new Thread(new Navigation());
			threadTacometria.setDaemon(true);
			threadTacometria.setName("Thread Tacometria");
			threadTacometria.start();

			victorySong();
			// Navigation.openGarra();
			float dist = -0.3f;
			int ang = 45;
			//Navigation.andar(dist);
			
			//Sensors.calibraCorDoll();
			Button.ENTER.waitForPressAndRelease();
//			while (Button.ENTER.isUp()){
//				ang = Sensors.VerificaCorDoll();
//	}
			
			
			
			
			// ======FINAL DO CODIGO=====================================================
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

	private static boolean allowedReading() {

		if (Navigation.robotPosition.x == (LIN_AMT - 1) && (Navigation.orientation == Navigation.FRONT
				|| Navigation.orientation == Navigation.LEFT || Navigation.orientation == Navigation.RIGTH)) {

			return false;

		} else if (Navigation.robotPosition.x == 0 && (Navigation.orientation == Navigation.BACK
				|| Navigation.orientation == Navigation.LEFT || Navigation.orientation == Navigation.RIGTH)) {

			return false;

		} else if (Navigation.robotPosition.y == (COL_AMT - 1) && (Navigation.orientation == Navigation.LEFT
				|| Navigation.orientation == Navigation.BACK || Navigation.orientation == Navigation.FRONT)) {

			return false;

		} else if (Navigation.robotPosition.y == 0 && (Navigation.orientation == Navigation.RIGTH
				|| Navigation.orientation == Navigation.BACK || Navigation.orientation == Navigation.FRONT)) {

			return false;

		}

		else {

			return true;

		}
	}

	/**
	 * Faz a checagem da celula que está em frente ao robo, usando o sensor de
	 * presença e distancia<br>
	 * Caso tenha algo a captura é realizada, caso contrário, a célula é marcada
	 * como vazia<br>
	 * Sò deve ser acionado depois que a leitura for permitida (allowedReading),
	 * para evitar inconsistencias nas leituras
	 */
	private static void checkFrontRobotCell() {
		if (Navigation.orientation == Navigation.FRONT) {

			if (Sensors.verificaObstaculo()) {
				CENTRAL_MAP[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.occupied);
				Navigation.stop();
				Navigation.closeGarra();

			} else {
				CENTRAL_MAP[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.BACK) {

			if (Sensors.verificaObstaculo()) {
				CENTRAL_MAP[Navigation.robotPosition.x - 1][Navigation.robotPosition.y].setStatus(Status.occupied);
				Navigation.stop();
				Navigation.closeGarra();

			} else {
				CENTRAL_MAP[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.LEFT) {
			if (Sensors.verificaObstaculo()) {
				CENTRAL_MAP[Navigation.robotPosition.x][Navigation.robotPosition.y + 1].setStatus(Status.occupied);
				Navigation.stop();
				Navigation.closeGarra();
			} else {
				CENTRAL_MAP[Navigation.robotPosition.x][Navigation.robotPosition.y + 1].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.RIGTH) {
			if (Sensors.verificaObstaculo()) {
				CENTRAL_MAP[Navigation.robotPosition.x][Navigation.robotPosition.y - 1].setStatus(Status.occupied);
				Navigation.stop();
				Navigation.closeGarra();
			} else {
				CENTRAL_MAP[Navigation.robotPosition.x][Navigation.robotPosition.y - 1].setStatus(Status.empty);
			}

		}
	}

	public static void iniciaPerifericoFrenteDireita(int cave) {
		/*
		 * Chama o launcher dos mapas
		 */
		mapLauncher();

		switch (cave) {

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

	public static void iniciaPerifericoFrenteEsquerda(int cave) {
		/*
		 * chama o launcher dos mapas
		 */
		mapLauncher();

		switch (cave) {

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

	public static void iniciaPerifericoLateral(int cave) {
		/*
		 * chama o launcher dos mapas
		 */
		mapLauncher();

		switch (cave) {

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

	private static void mapLauncher() {
		/*
		 * Inicia o modulo central e perifericos com todas as celulas como não
		 * checadas
		 */
		for (int i = 0; i < LIN_AMT; i++) {
			for (int j = 0; j < COL_AMT; j++) {
				CENTRAL_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
				CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
				OBSTACLE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);

			}
		}
	}

}