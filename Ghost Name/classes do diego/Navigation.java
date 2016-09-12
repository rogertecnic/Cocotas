package sek2016;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

/**
 * Controle de movimento do robo
 */
public class Navigation {
	// ---------------------DECLARACAO DOS MOTORES---------------------------
	static EV3LargeRegulatedMotor rodaE;
	static EV3LargeRegulatedMotor rodaD;
	static EV3MediumRegulatedMotor motorG;

	// ---------------------CONSTANTES DE DESCRICAO--------------------------
	private final static float VELO_INI = 8.0f;
	private final static float DISTANCIA_ENTRE_RODAS = 0.1378f;// metros, ja
																// conferido
	public final static float RAIO = 0.0280f; // metros, ja conferido (se
												// alterar tem que alterar o de
												// cima)
	// --------------------------------Orientação------------------------------
	/*
	 * Constantes de orientação do robo
	 */
	final static int FRONT = 2;
	final static int LEFT = 1;
	final static int RIGTH = 3;
	final static int BACK = 0;

	/*
	 * Orientação real do robo
	 */
	static int orientation = FRONT;

	// ---------------------CONSTANTES DE PROCESSO---------------------------
	static float[] WdWe = new float[2]; // Velocidade angular da roda Direita e
										// Esquerda

	// ---------------------VARIAVEIS DE PROCESSO----------------------------
	private static boolean garraFechada = false;// para não abrir se ja estiver
												// aberta ou vice versa

	// --------------------METODOS------------------------------------------
	/**
	 * Serve de suporte para o executeTurn e para orientação, garantindo que os
	 * giros sejam de multiplos de 90 graus
	 * 
	 * @param degree
	 *            são aos graus que o robo deve girar
	 */
	public static void turn(float degree) {
		if (degree % (90) == 0 || degree % (-90) == 0) {
			executeTurn(degree);

			if (degree < 0) {
				int giro = (int) (degree / -90);

				for (int x = 0; x < giro; x++) {

					alterOrientation(-1);

				}

			} else if (degree > 0) {
				int giro = (int) (degree / 90);

				for (int x = 0; x < giro; x++) {

					alterOrientation(1);

				}

			}
		} else {
			Sound.beepSequence();
		}
	}

	/**
	 * Gira o robo no proprio eixo, (não usa o giroscopio, o método segura o
	 * programa dentro dele
	 * 
	 * @param graus
	 *            inteiro positivo (horário)<br>
	 *            inteiro negativo (antihorário)
	 */
	private static void executeTurn(float graus) {
		PID.pidRunning = false; // pausa o pid para não zoar as velocidades
		float theta = ((3.14159265f * graus / 180) * DISTANCIA_ENTRE_RODAS * 180f) / (2f * RAIO * 3.14159265f);
		rodaE.rotate((int) theta, true);
		rodaD.rotate(-(int) theta); // trava o programa aqui e aguarda o giro
									// terminar
		stop();
		Delay.msDelay(10);
		Sensors.resetAngle();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning = true; // continua o pid

	}

	/**
	 * Virar robo a esquerda 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	public static void turnLeft() {
		PID.pidRunning = false; // pausa o pid para não zoar as velocidades
		float angle = Sensors.getAngle();
		while (angle <= 85) {
			rodaE.setSpeed(100);
			rodaD.setSpeed(100);
			rodaE.backward();
			rodaD.forward();
			angle = Sensors.getAngle();
		}
		stop();
		Delay.msDelay(10);
		Sensors.resetAngle();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning = true; // continua o pid

		alterOrientation(-1);
	}

	/**
	 * Virar robo a direita 90 graus usando o giroscopio<br>
	 * trava o programa dentro do metodo ate o giro terminar
	 */
	public static void turnRight() {
		PID.pidRunning = false; // pausa o pid para não zoar as velocidades
		float angle = Sensors.getAngle();
		while (angle >= -85) {
			rodaE.setSpeed(100);
			rodaD.setSpeed(100);
			rodaE.forward();
			rodaD.backward();
			angle = Sensors.getAngle();
		}
		stop();
		Delay.msDelay(10);
		Sensors.resetAngle();
		PID.zeraPID(); // zera o pid para a nova posição
		PID.pidRunning = true; // continua o pid

		alterOrientation(1);
	}

	/**
	 * robo anda pra frente em uma determinada distancia<br>
	 * velocidade definida pelo PID, metodo segura o programa dentro dele ate o
	 * robo chegar
	 * 
	 * @param dist
	 *            ditancia em metros
	 */
	public static void forward(float dist) { // s=theta*r
		float theta;
		theta = dist / RAIO * 180 / 3.14159265f;
		float position = rodaD.getPosition(); // posicao em graus
		// rodaD.rotate(theta); //se usar o rodaX.rotate() o robo da uma
		// viradinha quando chega
		// rodaE.rotate(theta);
		Navigation.forward();
		while (rodaD.getPosition() <= (position + theta)) {
		}
		Navigation.stop();
	}

	/**
	 * Frente, velocidade definida pelo PID
	 */
	public static void forward() {
		rodaE.forward();
		rodaD.forward();
	}

	/**
	 * Traz. velocidade definida pelo PID
	 */
	public static void backward() {
		rodaE.backward();
		rodaD.backward();
	}

	/**
	 * seta aceleracao de cada roda em graus/s^2 DA RODA
	 * 
	 * @param motorD
	 *            motor direito
	 * @param motorE
	 *            motor esquerdo
	 */
	public static void setAcceleration(int motorD, int motorE) {
		Navigation.rodaD.setAcceleration(motorD);
		Navigation.rodaE.setAcceleration(motorE);
	}

	/**
	 * seta velocidade de cada roda de acordo com o PID em graus/s DA RODA
	 */
	public static void setVelocidade() {
		WdWe = convertePara_WdWe();
		Navigation.rodaD.setSpeed(WdWe[0]);
		Navigation.rodaE.setSpeed(WdWe[1]);

	}

	/**
	 * seta velocidade em graus/s DA RODA
	 * 
	 * @param velo
	 */
	public static void setVelocidade(int velo) {
		setAcceleration(700, 700);
		Navigation.rodaD.setSpeed(velo);
		Navigation.rodaE.setSpeed(velo);

	}

	/**
	 * fechar garra
	 */
	public static void closeGarra() {
		if (garraFechada == false) {
			garraFechada = true;
			motorG.rotate(60);
		} else {
			LCD.clear();
			LCD.drawString("Garra fechada", 0, 0);
		}
	}

	/**
	 * Abrir garra
	 */
	public static void openGarra() {
		if (garraFechada == true) {
			garraFechada = false;
			motorG.rotate(-60);
		} else {
			LCD.clear();
			LCD.drawString("Garra aberta", 0, 0);
		}
	}

	/**
	 * Parar o robo
	 */
	public static void stop() {
		rodaD.setAcceleration(1500);
		rodaE.setAcceleration(1500);
		rodaD.stop(true);
		rodaE.stop(false);
	}

	private static float[] convertePara_WdWe() {
		float Wd = (2 * VELO_INI - DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);
		float We = (2 * VELO_INI + DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);

		return new float[] { Wd, We };

	}

	/**
	 * instancia os motores
	 * 
	 * @param initMotors:
	 *            true ou false
	 */
	public static void init(boolean initMotors) {
		if (initMotors == true) {
			rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
			rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
			motorG = new EV3MediumRegulatedMotor(MotorPort.C);

		}
	}

	public static void close() {
		rodaD.close();
		rodaE.close();
	}

	/**
	 * Metodo que retorna a tacometria dos motores de acordo com os parametros
	 * especificados, caso o parametro seja desconhecido, retorna null.
	 * 
	 * @param L
	 *            (Left) para retornar a tacometria do motor esquerdo <br>
	 *            <b>R</b> (Right) para retornar a tacometria do motor direito
	 *            <br>
	 *            <b>B</b> (Both) para retornar a média da tacometria dos dois
	 *            motores
	 * @return Retorna valores float
	 */
	public static float getTacho(String x) {
		if (x.toUpperCase() == "L") {
			return rodaE.getTachoCount();

		} else if (x.toUpperCase() == "R") {

			return rodaD.getTachoCount();

		} else if (x.toUpperCase() == "B") {
			float temp1 = rodaE.getTachoCount();
			float temp2 = rodaD.getTachoCount();

			return ((temp1 + temp2) / 2);

		} else {

			return (Float) null;
		}
	}
	
	public static void resetTacho(){
		rodaE.resetTachoCount();
		rodaD.resetTachoCount();
	}

	private static void alterOrientation(int ortt) {
		if (ortt > 1) {
			ortt = 1;
		} else if (ortt < -1) {
			ortt = -1;
		}

		switch (ortt) {
		case 1:
			orientation++;

			if (orientation > 3) {
				orientation = 0;
			}
			break;

		case -1:
			orientation--;

			if (orientation < 0) {
				orientation = 3;
			}
			break;
		}
	}
}
