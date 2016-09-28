package sek2016;

import java.util.List;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;
import sek2016.Celula.Status;

/**
 * 
 * @author Equipe Sek 2016:<br>
 *         Diego Costa - Eng. da Computacao<br>
 *         Karinny Gol�alves<br>
 *         Lucas Sim�es - Eng. da Computacao<br>
 *         Mariana Moreno - Eng. Mec�nica<br>
 *         Rog�rio Pereira Batista - Eng. El�trica<br>
 */
public class AlienRescue implements Runnable {
	/**
	 * Variavel global que indica se a Thread do programa est� executando (ON)
	 * ou fechada (OFF)
	 */
	public static boolean alienRescueON;

	/**
	 * Thread que comanda a execu��o do PID
	 */
	public static Thread threadPID;
	/**
	 * Thread que permite que a tacometria seja usada
	 */
	private static Thread threadTacometria;

	// =========================CONSTANTES DE PROCESSO=========================
	/**
	 * quantidade de colunas na matriz
	 */
	private static final int COL_AMT = 9; 
	/**
	 * quantidade de linhas na matriz
	 */
	private static final int LIN_AMT = 9;
	// =======================VARIAVEIS DO MAPA==============================
	private final static Celula[][] CENTRAL_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] CAVE_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] OBSTACLE_MAP = new Celula[LIN_AMT][COL_AMT];

	public static Posicao inputCell = new Posicao(0, 4);
	private static Posicao caveEntrance;
	private static Posicao caveExit;
	private static Posicao obstacleEntrace;
	private static Posicao obstacleExit;

	private static Astar aStar;
	static Posicao teste = new Posicao(5, 5);

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
			 * Thread da PID � iniciada aqui.
			 */
			threadPID = new Thread(new PID());
			threadPID.setDaemon(true);
			threadPID.setName("threadPID");
			PID.pidRunning = true;
			threadPID.start();

			/*
			 * Thread da Tacometria � iniciada aqui.
			 */
			threadTacometria = new Thread(new Navigation());
			threadTacometria.setDaemon(true);
			threadTacometria.setName("Thread Tacometria");
			threadTacometria.start();
			Navigation.forward();
			Delay.msDelay(2000);
			Navigation.stop();
			Navigation.forward();
			Delay.msDelay(2000);
			Navigation.stop();
			Navigation.forward();
			Delay.msDelay(2000);
			Navigation.stop();
			/*while(Button.ENTER.isUp()){
				
			}*/
			//goTo(teste);
			// Plano_B.partiu();
			// victorySong();
			// Navigation.openGarra();
			// float dist = 0.3f;
			// int ang = 45;
			// Navigation.andar(dist);

			// Button.ENTER.waitForPressAndRelease();

			// ======FINAL DO
			// CODIGO=================================================
			alienRescueON = false;
		} catch (ThreadDeath e) {// quando o menu � chamado, essa thread �
									// desligada e lan�a essa exception
			e.getStackTrace();
		} catch (Exception e) {

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
	 * Faz a checagem da celula que est� em frente ao robo, usando o sensor de
	 * presen�a e distancia<br>
	 * Caso tenha algo a captura � realizada, caso contr�rio, a c�lula � marcada
	 * como vazia<br>
	 * S� deve ser acionado depois que a leitura for permitida (allowedReading),
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

		case MainMenuClass.CAV_DIR:

			caveEntrance = new Posicao(4, 0);
			obstacleEntrace = new Posicao(8, 4);

			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);
			break;

		case MainMenuClass.CAV_CIMA:
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

		case MainMenuClass.CAV_CIMA:
			caveEntrance = new Posicao(8, 4);
			obstacleEntrace = new Posicao(4, 8);

			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);

			break;

		case MainMenuClass.CAV_ESQ:
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

		case MainMenuClass.CAV_ESQ:
			caveEntrance = new Posicao(4, 8);
			obstacleEntrace = new Posicao(4, 0);

			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);

			break;

		case MainMenuClass.CAV_DIR:
			caveEntrance = new Posicao(4, 0);
			obstacleEntrace = new Posicao(4, 8);

			caveExit = new Posicao(0, 4);
			obstacleExit = new Posicao(0, 4);

			break;
		}
	}

	private static void mapLauncher() {
		/*
		 * Inicia o modulo central e perifericos com todas as celulas como n�o
		 * checadas com excess�o das celulas centrais do modulo caverna.
		 */
		for (int i = 0; i < LIN_AMT; i++) {
			for (int j = 0; j < COL_AMT; j++) {

				CENTRAL_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
				OBSTACLE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);

				if ((i >= 3 && i <= 5) && (j >= 3 && j <= 5)) {

					CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.occupied);

				} else {

					CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);

				}
			}
		}
	}

	/**
	 * Esse m�todo implementa o ir do rob� de sua posi��o para uma posi��o alvo
	 * 
	 * @param posicaoAlvo
	 * @throws Exception
	 */
	private static void goTo(Posicao posicaoAlvo) throws Exception {

		aStar = new Astar(CENTRAL_MAP);
		List<Celula> caminho = aStar.search(Navigation.robotPosition, posicaoAlvo);

		for (int i = 0; i < caminho.size(); i++) {
			/*
			 * A c�lula est� a esquerda da posi��o do robounico modo de chegar
			 * at� ela � s� quando a orienta��o for RIGHT
			 */
			if (caminho.get(i).getPosicao().x == Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y > Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.RIGTH) {

					Navigation.turn(-90);

				}

				Navigation.forward();

				while (caminho.get(i).getPosicao() != Navigation.robotPosition) {
					if (allowedReading()) {

						checkFrontRobotCell();

					}
				}

				caminho.remove(i);
				continue;

			}
			/*
			 * A c�lula est� a esquerda da posi��o do robo, unico modo de chegar
			 * at� ela � s� quando a orienta��o for LEFT
			 */
			else if (caminho.get(i).getPosicao().x == Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y > Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.LEFT) {

					Navigation.turn(90);

				}

				Navigation.forward();

				while (caminho.get(i).getPosicao() != Navigation.robotPosition) {
					if (allowedReading()) {

						checkFrontRobotCell();

					}
				}

				caminho.remove(i);
				continue;
			}
			/*
			 * A c�lula est� a frente da posi��o do robo, unico modo de chegar
			 * at� ela � s� quando a orienta��o for FRONT
			 */
			else if (caminho.get(i).getPosicao().x > Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y == Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.FRONT) {

					if (Navigation.orientation == Navigation.LEFT) {

						Navigation.turn(90);

					} else if (Navigation.orientation == Navigation.RIGTH) {

						Navigation.turn(-90);

					} else {

						Navigation.turn(-90);

					}

				}

				Navigation.forward();

				while (caminho.get(i).getPosicao() != Navigation.robotPosition) {
					if (allowedReading()) {

						checkFrontRobotCell();

					}
				}

				caminho.remove(i);
				continue;

			}
			/*
			 * A c�lula est� atr�s da posi��o do robo, unico modo de chegar at�
			 * ela � s� quando a orienta��o for BACK
			 */
			else if (caminho.get(i).getPosicao().x < Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y == Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.BACK) {

					if (Navigation.orientation == Navigation.LEFT) {

						Navigation.turn(90);

					} else if (Navigation.orientation == Navigation.RIGTH) {

						Navigation.turn(-90);

					} else {

						Navigation.turn(90);

					}

				}

				Navigation.forward();

				while (caminho.get(i).getPosicao() != Navigation.robotPosition) {
					if (allowedReading()) {

						checkFrontRobotCell();

					}
				}

				caminho.remove(i);
				continue;

			}
		}
	}

}