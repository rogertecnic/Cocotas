package sek2016;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

/**
 * mover o robo
 */
public class Navigation {
	// --------------------Declaração de Motores---------------------------
	static EV3LargeRegulatedMotor rodaE;
	static EV3LargeRegulatedMotor rodaD;
	static EV3MediumRegulatedMotor motorG;



	// --------------Constantes de Descrição--------------------------
	private final static float VELO_INI = 8.0f;
	private final static float DISTANCIA_ENTRE_RODAS = 0.137f;
	private final static float RAIO = 0.0274f; //metros

	//-------------------------Constantes de Processo-----------------------
	static float[] WdWe = new float[2]; //Velocidade angular da roda Direita e Esquerda


	// ---------------------------Métodos----------------------------------

	/**
	 * Virar robo a esquerda 90 graus
	 */
	public static void turnLeft() {
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
		PID.zeraPID();
		Delay.msDelay(50);
	}

	/**
	 * Virar robo a direita 90 graus
	 */
	public static void turnRight() {
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
		PID.zeraPID();
		Delay.msDelay(50);
	}

	/**
	 * robo anda pra frente em uma determinada distancia
	 * @param dist ditancia em metros
	 */
	public static void forward(float dist){ //   s=theta*r
		float theta;
		theta=dist/RAIO*180/3.14159265f;
		System.out.println(theta);
		rodaE.rotate((int)theta, true);
		rodaD.rotate((int)theta);
	}
	/**
	 * Frente, velocidade definida pelo PID
	 */
	public static void forward() {
		rodaE.forward();
		rodaD.forward();
	}

	/**
	 * Traz.
	 */
	public static void backward() {
		rodaE.backward();
		rodaD.backward();

	}

	/**
	 * seta velocidade de cada roda
	 * @param motorD
	 * @param motorE
	 */
	public static void setAcceleration(int motorD, int motorE){
		Navigation.rodaD.setAcceleration(motorD);
		Navigation.rodaE.setAcceleration(motorE);
	}

	public static void setVelocidade (){
		setAcceleration(700, 700);
		WdWe = convertePara_WdWe();
		Navigation.rodaD.setSpeed(WdWe[0]);
		Navigation.rodaE.setSpeed(WdWe[1]);


	}
	
	private static boolean garraFechada=false;
	/**
	 * fechar garra
	 */
	public static void closeGarra(){
		if(garraFechada == false){
			garraFechada = true;
			motorG.rotate(60);
		}
		else{
			LCD.clear();
			LCD.drawString("Garra fechada", 0, 0);
		}
	}

	/**
	 * Abrir garra
	 */
	public static void openGarra(){
		if(garraFechada == true){
			garraFechada = false;
			motorG.rotate(-60);
		}
		else{
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
		rodaE.stop(true);
		rodaD.stop();
	}

	private static float[] convertePara_WdWe() {
		float Wd = (2 * VELO_INI - DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);
		float We = (2 * VELO_INI + DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);

		return new float[] { Wd, We };

	}

	/**
	 * instancia os motores
	 * @param initMotors: true ou false
	 */
	public static void init(boolean initMotors) {
		if(initMotors == true){
			rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
			rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
			motorG = new EV3MediumRegulatedMotor(MotorPort.C);

		}
	}
	
	public static void close(){
		rodaD.close();
		rodaE.close();
	}
}
