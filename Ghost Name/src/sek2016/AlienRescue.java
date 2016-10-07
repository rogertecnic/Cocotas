package sek2016;

import java.util.List;

import javax.jws.soap.SOAPBinding;
import javax.print.attribute.standard.MediaSize.NA;

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

	enum Module {
		Central, Cave, Obstacle, OutOfModule;
	}
	// =================INSTANCIAS DAS THREADS=================================

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

	// =======================VARIAVEIS DE MAPA===========================
	private final static Celula[][] CENTRAL_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] CAVE_MAP = new Celula[LIN_AMT][COL_AMT];
	private final static Celula[][] OBSTACLE_MAP = new Celula[LIN_AMT][COL_AMT];

	public static Posicao inputCell = new Posicao(0, 4);
	private static Posicao caveEntrance;
	private static Posicao caveExit;
	private static Posicao obstacleEntrace;
	private static Posicao obstacleExit;

	private static Astar aStar;

	// ======================== Variáveis de posicionamento=================

	/**
	 * Indica qual módulo o robo se encontra
	 */
	private static Module modulo = Module.OutOfModule;

	/**
	 * Armazena a orientação do robo quando ele troca de modulo
	 */
	private static int orientacaoArmazenada;

	/**
	 * Distancia da travessia entre um módulo e outro ou de entrada no modulo
	 * central.
	 */
	private static final float DIST_TRAVESSIA = 0.25f;

	// =================================Flags de uso geral=================

	/**
	 * Variavel global que indica se a Thread do programa está executando (ON)
	 * ou fechada (OFF)
	 */
	public static boolean alienRescueON;

	/**
	 * Flag que informa que houve uma mudança de célula
	 */
	public static boolean cellExchanged = false;

	/**
	 * Flag que informa que a leitura pra aquela célula já foi feita
	 */
	public static boolean cellAlreadyRead = false;

	// ==================== Caminho do robo============================
	/**
	 * Lista ligada que contem o caminho até algo
	 */
	private static List<Celula> path;

	// =====================Pontos de controle dos 3 mapas=================
	// --------------------Mapa central-------------------------

	Posicao point1CentralMap = new Posicao(4, 4);
	Posicao point2CentralMap = new Posicao(4, 7);
	Posicao point3CentralMap = new Posicao(7, 4);
	Posicao point4CentralMap = new Posicao(2, 1);

	// --------------------Mapa da caverna----------------------

	Posicao point1Cavemap = new Posicao(4, 7);
	Posicao point2Cavemap = new Posicao(7, 4);
	Posicao point3Cavemap = new Posicao(4, 1);

	// -----------------Mapa do obstáculo----------------------
	// ----------------Seção 1----------------------

	Posicao point1ObstacleMapS1 = new Posicao(1, 6);
	Posicao point2ObstacleMapS1 = new Posicao(1, 2);

	// --------Seção 2-----------------------------

	Posicao point3ObstacleMapS2 = new Posicao(3, 1);
	Posicao point4ObstacleMapS2 = new Posicao(3, 2);
	Posicao point5ObstacleMapS2 = new Posicao(3, 6);
	Posicao point6ObstacleMapS2 = new Posicao(3, 7);

	// -----------Seção 3------------------------
	Posicao point7ObstacleMapS3 = new Posicao(6, 7);
	Posicao point8ObstacleMapS3 = new Posicao(7, 5);
	Posicao point9ObstacleMapS3 = new Posicao(6, 5);
	Posicao point10ObstacleMapS3 = new Posicao(7, 2);

	// ========================Métodos de Implementação====================
	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		Navigation.garraFechada = false;
		try { // o codigo deve ficar dentro desse try
				// ======INICIO DO CODIGO===================================
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

			// ======FINAL DO CODIGO=========================================

			alienRescueON = false;

			/*
			 * Quando o menu é chamado, essa thread é desligada e essa exceção é
			 * lançada
			 */
		} catch (ThreadDeath e) {

			e.getStackTrace();

		} catch (Exception e) {

			e.printStackTrace();
			Sound.buzz();

		}
	}

	/**
	 * Método Motherfucker que siplemesmente faz o resgate
	 */
	public static void toRescue() {
		/*
		 * Leitura do chão e definição do que resgatar
		 */

		enterModule(Module.Central);

	}
	
	/**
	 * 
	 * @return O melhor lugar para se fazer aquela busca marota
	 */
	private static Module bestPlaceToSearch() {
		int centralAmount = 0;
		int caveAmount = 0;
		int obstacleAmount = -4;

		for (int i = 0; i < COL_AMT; i++) {
			for (int j = 0; j < LIN_AMT; j++) {

				if (CENTRAL_MAP[i][j].getStatus() == Status.unchecked && MainMenuClass.bonecoNoCentro) {
					
					centralAmount++;
					
				}
				
				if (CAVE_MAP[i][j].getStatus() == Status.unchecked ){
					
					caveAmount++;
					
				}
				
				if (OBSTACLE_MAP[i][j].getStatus() == Status.unchecked){
					
					obstacleAmount++;
					
				}

			}
		}
		
		
		if (centralAmount >= obstacleAmount || centralAmount >=caveAmount){
			
			return Module.Central;
			
		}else if (caveAmount >= obstacleAmount){
			
			return Module.Cave;
			
		}else{
			return Module.Obstacle;
		}
		
	}
	
	private static void centralMapStrategy(){
		
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
	private static void checkFrontRobotCell(Celula[][] mapaAtual) {

		if (Navigation.orientation == Navigation.FRONT) {

			if (Sensors.checkIfCellOcuppied()) {
				mapaAtual[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.occupied);
				/*
				 * Técnica de captura
				 */

			} else {
				mapaAtual[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.BACK) {

			if (Sensors.checkIfCellOcuppied()) {
				mapaAtual[Navigation.robotPosition.x - 1][Navigation.robotPosition.y].setStatus(Status.occupied);
				/*
				 * Técnica de captura
				 */

			} else {
				mapaAtual[Navigation.robotPosition.x + 1][Navigation.robotPosition.y].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.LEFT) {
			if (Sensors.checkIfCellOcuppied()) {
				mapaAtual[Navigation.robotPosition.x][Navigation.robotPosition.y + 1].setStatus(Status.occupied);
				/*
				 * Técnica de captura
				 */

			} else {
				mapaAtual[Navigation.robotPosition.x][Navigation.robotPosition.y + 1].setStatus(Status.empty);
			}

		}

		else if (Navigation.orientation == Navigation.RIGTH) {
			if (Sensors.checkIfCellOcuppied()) {
				mapaAtual[Navigation.robotPosition.x][Navigation.robotPosition.y - 1].setStatus(Status.occupied);

				/*
				 * Técnica de captura
				 */

			} else {
				mapaAtual[Navigation.robotPosition.x][Navigation.robotPosition.y - 1].setStatus(Status.empty);
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
	 * Realiza a troca de modulo, bem como a atualização de direção e
	 * posicionamento nas celulas
	 * 
	 * @param moduloAlvo
	 *            Modulo ao qual se deseja alcançar
	 */
	public static void enterModule(Module moduloAlvo) {
		/*
		 * Quando se deseja entrar no módulo central, vindo da area de resgate
		 * chamada OutOfModule
		 */
		if (modulo == Module.OutOfModule && moduloAlvo == Module.Central) {

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);

			Navigation.setTachometer(true);

			Navigation.orientation = Navigation.FRONT;

		}
		/*
		 * Quando se quer entrar no modulo da caverna
		 */
		else if (modulo == Module.Central && moduloAlvo == Module.Cave) {

			if (Navigation.orientation == Navigation.FRONT) {

				orientacaoArmazenada = Navigation.BACK;

			} else if (Navigation.orientation == Navigation.BACK) {

				orientacaoArmazenada = Navigation.FRONT;

			} else if (Navigation.orientation == Navigation.LEFT) {

				orientacaoArmazenada = Navigation.RIGTH;

			} else if (Navigation.orientation == Navigation.RIGTH) {

				orientacaoArmazenada = Navigation.LEFT;

			}

			Navigation.setTachometer(false);

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);

			Navigation.orientation = Navigation.FRONT;

			Navigation.robotPosition = caveExit;

			Navigation.setTachometer(true);

		}
		/*
		 * Quando se quer entrar no modulo do obstáculo
		 */
		else if (modulo == Module.Central && moduloAlvo == Module.Obstacle) {
			if (Navigation.orientation == Navigation.FRONT) {

				orientacaoArmazenada = Navigation.BACK;

			} else if (Navigation.orientation == Navigation.BACK) {

				orientacaoArmazenada = Navigation.FRONT;

			} else if (Navigation.orientation == Navigation.LEFT) {

				orientacaoArmazenada = Navigation.RIGTH;

			} else if (Navigation.orientation == Navigation.RIGTH) {

				orientacaoArmazenada = Navigation.LEFT;

			}

			Navigation.setTachometer(false);

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);

			Navigation.orientation = Navigation.FRONT;

			Navigation.robotPosition = obstacleExit;

			Navigation.setTachometer(true);

		}
		/*
		 * Quando se quer entrar no modulo central vindo do modulo cave
		 */
		else if (modulo == Module.Cave && moduloAlvo == Module.Central) {

			Navigation.setTachometer(false);

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);

			Navigation.orientation = orientacaoArmazenada;

			Navigation.robotPosition = caveEntrance;

			Navigation.setTachometer(true);

		}
		/*
		 * Quando se quer entrar no mudulo central vindo do modulo Obstacle
		 */
		else if (modulo == Module.Obstacle && moduloAlvo == Module.Central) {

			Navigation.setTachometer(false);

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);

			Navigation.orientation = orientacaoArmazenada;

			Navigation.robotPosition = obstacleEntrace;

			Navigation.setTachometer(true);

		}
		/*
		 * Quando se deseja entrar na area de resgate chamado OutOfModule
		 */
		else if (modulo == Module.Central && moduloAlvo == Module.OutOfModule) {

			Navigation.setTachometer(false);

			Navigation.andar(DIST_TRAVESSIA);

			setModule(moduloAlvo);
		}
	}

	/**
	 * Retorna o modulo atual em que o robo está
	 * 
	 * @return Modulo, do tipo <b>Module</b>
	 */
	public static Module getModule() {

		return modulo;

	}

	/**
	 * Permite que o modulo em que o robo está seja alterado
	 * 
	 * @param moduloAlvo
	 *            Do tipo <b>Module</b>
	 */
	public static void setModule(Module moduloAlvo) {

		modulo = moduloAlvo;

	}

	private static Celula[][] currentMap() {
		switch (getModule()) {

		case Central:
			return CENTRAL_MAP;

		case Cave:
			return CAVE_MAP;

		case Obstacle:
			return OBSTACLE_MAP;

		default:
			break;
		}
		return null;
	}

	/**
	 * Literalmente encontra o melhor caminho para passar e seta esse caminho em
	 * <b>path</b>
	 * 
	 * @param posicaoAlvo
	 *            posição ao qual se deseja chegar
	 * @throws Exception
	 *             Exceção gerada pelo uso do A*
	 */
	private static void setPath(Posicao posicaoAlvo) throws Exception {
		aStar = new Astar(CENTRAL_MAP);
		path = aStar.search(Navigation.robotPosition, posicaoAlvo);
	}

	/**
	 * Retorna a lista de celulas que formam o melhor caminho para uma posição
	 * alvo, que foi definidas dentro de path
	 * 
	 * @return O caminho que está contido dentro do <b>path</b>
	 */
	private static List<Celula> getPath() {

		return path;

	}

	/**
	 * Esse método implementa o ir do robô de sua posição para uma posição alvo
	 * 
	 * @param caminho
	 *            É uma lista de celulas de por onde o robo deve passar para
	 *            chegar na posição alvo
	 */
	private static void goTo(List<Celula> caminho) {

		if (caminho.isEmpty()) {
			MainMenuClass.printDebug("Caminho Vazio");
			Sound.buzz();
		} else {

			for (int i = 0; i < caminho.size(); i++) {
				/*
				 * A célula está a esquerda da posição do robounico modo de
				 * chegar até ela é só quando a orientação for LEFT
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
							if (allowedReading() && !cellAlreadyRead) {

								checkFrontRobotCell(currentMap());
								cellAlreadyRead = true;

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
				 * A célula está a direita da posição do robo, unico modo de
				 * chegar até ela é só quando a orientação for RIGHT
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
							if (allowedReading() && !cellAlreadyRead) {

								checkFrontRobotCell(currentMap());
								cellAlreadyRead = true;

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
				 * A célula está a frente da posição do robo, unico modo de
				 * chegar até ela é só quando a orientação for FRONT
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
							if (allowedReading() && !cellAlreadyRead) {

								checkFrontRobotCell(currentMap());
								cellAlreadyRead = true;

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
				 * A célula está atrás da posição do robo, unico modo de chegar
				 * até ela é só quando a orientação for BACK
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
							if (allowedReading() && !cellAlreadyRead) {

								checkFrontRobotCell(currentMap());
								cellAlreadyRead = true;

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

}