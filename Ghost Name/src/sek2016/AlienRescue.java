package sek2016;

import java.util.List;
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
	/**
	 * quantidade de colunas na matriz
	 */
	private static final int COL_AMT = 9;
	/**
	 * quantidade de linhas na matriz
	 */
	private static final int LIN_AMT = 9;

	private static final float distAndar = Celula.commonSize + 0.05f;

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

	/**
	 * Variável que informa que houve uma mudança de célula
	 */
	public static boolean cellExchanged = false;

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

			goTo(caveEntrance);
			goTo(obstacleEntrace);

			// ======FINAL DO
			// CODIGO=================================================
			alienRescueON = false;
		} catch (ThreadDeath e) {// quando o menu é chamado, essa thread é
			// desligada e lança essa exception
			e.getStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
			Sound.buzz();

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
		 * Inicia o modulo central e perifericos com todas as celulas como não
		 * checadas com excessão das celulas centrais do modulo caverna.
		 */
		for (int i = 0; i < LIN_AMT; i++) {
			for (int j = 0; j < COL_AMT; j++) {

				if (i == 0 || i == (LIN_AMT - 1) || j == 0 || j == (COL_AMT - 1)) {
					CENTRAL_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
					CENTRAL_MAP[i][j].g = 1;

					OBSTACLE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
					OBSTACLE_MAP[i][j].g = 1;

					if ((i >= 2 && i <= 6) && (j >= 2 && j <= 6)) {

						CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.occupied);
						CAVE_MAP[i][j].g = 1;

					} else {

						CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
						CAVE_MAP[i][j].g = 1;

					}
				} else {
					CENTRAL_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);
					OBSTACLE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);

					if ((i >= 2 && i <= 6) && (j >= 2 && j <= 6)) {

						CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.occupied);

					} else {

						CAVE_MAP[i][j] = new Celula(new Posicao(i, j), Status.unchecked);

					}
				}

			}
		}
	}

	/**
	 * Esse método implementa o ir do robô de sua posição para uma posição alvo
	 * 
	 * @param posicaoAlvo
	 * @throws Exception
	 */
	private static void goTo(Posicao posicaoAlvo) throws Exception {

		aStar = new Astar(CAVE_MAP);
		List<Celula> caminho = aStar.search(Navigation.robotPosition, posicaoAlvo);

		for (int i = 0; i < caminho.size(); i++) {
			/*
			 * A célula está a esquerda da posição do robounico modo de chegar
			 * até ela é só quando a orientação for LEFT
			 */
			if (caminho.get(i).getPosicao().x == Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y > Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.LEFT) {

					if (Navigation.orientation == Navigation.FRONT) {

						Navigation.stop();
						Navigation.turn(90);

					} else if (Navigation.orientation == Navigation.BACK) {

						Navigation.stop();
						Navigation.turn(-90);

					} else {

						Navigation.stop();
						Navigation.turn(90);
					}

				}

				Navigation.forward();

				while (true) {

					if (cellExchanged == false) {
						if (allowedReading()) {

							// checkFrontRobotCell();

						}
					} else {

						if ((i + 1) < caminho.size()) {
							Sound.beep();

							cellExchanged = false;
							break;

						} else {

							Navigation.stop();

							cellExchanged = false;
							break;

						}
					}
				}

			}
			/*
			 * A célula está a direita da posição do robo, unico modo de chegar
			 * até ela é só quando a orientação for RIGHT
			 */
			else if (caminho.get(i).getPosicao().x == Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y < Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.RIGTH) {

					if (Navigation.orientation == Navigation.FRONT) {

						Navigation.stop();
						Navigation.turn(-90);

					} else if (Navigation.orientation == Navigation.BACK) {

						Navigation.stop();
						Navigation.turn(90);

					} else {

						Navigation.stop();
						Navigation.turn(90);

					}

				}

				Navigation.forward();

				while (true) {

					if (cellExchanged == false) {
						if (allowedReading()) {

							// checkFrontRobotCell();

						}
					} else {

						if ((i + 1) < caminho.size()) {
							Sound.beep();

							cellExchanged = false;
							break;

						} else {
							Navigation.stop();

							cellExchanged = false;
							break;

						}
					}
				}

			}
			/*
			 * A célula está a frente da posição do robo, unico modo de chegar
			 * até ela é só quando a orientação for FRONT
			 */
			else if (caminho.get(i).getPosicao().x > Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y == Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.FRONT) {

					if (Navigation.orientation == Navigation.LEFT) {

						Navigation.stop();
						Navigation.turn(-90);

					} else if (Navigation.orientation == Navigation.RIGTH) {

						Navigation.stop();
						Navigation.turn(90);

					} else {

						Navigation.stop();
						Navigation.turn(-90);

					}

				}

				Navigation.forward();

				while (true) {

					if (cellExchanged == false) {
						if (allowedReading()) {

							// checkFrontRobotCell();

						}
					} else {

						if ((i + 1) < caminho.size()) {
							Sound.beep();

							cellExchanged = false;
							break;

						} else {
							Navigation.stop();

							cellExchanged = false;
							break;

						}
					}
				}

			}
			/*
			 * A célula está atrás da posição do robo, unico modo de chegar até
			 * ela é só quando a orientação for BACK
			 */
			else if (caminho.get(i).getPosicao().x < Navigation.robotPosition.x
					&& caminho.get(i).getPosicao().y == Navigation.robotPosition.y) {

				while (Navigation.orientation != Navigation.BACK) {

					if (Navigation.orientation == Navigation.LEFT) {

						Navigation.stop();
						Navigation.turn(90);

					} else if (Navigation.orientation == Navigation.RIGTH) {

						Navigation.stop();
						Navigation.turn(-90);

					} else {

						Navigation.stop();
						Navigation.turn(90);

					}

				}

				Navigation.forward();

				while (true) {

					if (cellExchanged == false) {
						if (allowedReading()) {

							// checkFrontRobotCell();

						}
					} else {

						if ((i + 1) < caminho.size()) {
							Sound.beep();
							cellExchanged = false;
							break;

						} else {

							Navigation.stop();

							cellExchanged = false;
							break;

						}
					}
				}

			}
		}
	}

}