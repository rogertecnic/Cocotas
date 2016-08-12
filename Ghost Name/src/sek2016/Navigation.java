package sek2016;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;

public class Navigation {
	// --------------------Declaração de Motores---------------------------
	static EV3LargeRegulatedMotor rodaE;
	static EV3LargeRegulatedMotor rodaD;
	static EV3MediumRegulatedMotor motorG;
	
	
	
	// --------------Constantes de Descrição--------------------------
	private final static float VELO_INI = 15.0f;
	private final static float DISTANCIA_ENTRE_RODAS = 0.137f;
	private final static float RAIO = 0.026f;
	
	//-------------------------Constantes de Processo-----------------------
	static float[] WdWe = new float[2]; //Velocidade angular da roda Direita e Esquerda


	// ---------------------------Métodos----------------------------------
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

	public static void forward() {
		setVelocidade();
		rodaE.forward();
		rodaD.forward();
	}

	public static void backward() {
		rodaE.backward();
		rodaD.backward();

	}
	public static void setAcceleration(int motorD, int motorE){
		Navigation.rodaD.setAcceleration(motorD);
		Navigation.rodaE.setAcceleration(motorE);
	}
	
	private static void setVelocidade (){
		setAcceleration(700, 700);
		WdWe = convertePara_WdWe();
		Navigation.rodaD.setSpeed(WdWe[0]);
		Navigation.rodaE.setSpeed(WdWe[1]);
		

	}
	public static void closeGarra() {
		motorG.rotate(60);

	}
	public static void openGarra(){
		motorG.rotate(-60);
	}
	public static void stop() {
		rodaD.setAcceleration(1500);
		rodaE.setAcceleration(1500);
		rodaD.stop(true);
		rodaE.stop();
	}
	
	private static float[] convertePara_WdWe() {
		float Wd = (2 * VELO_INI - DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);
		float We = (2 * VELO_INI + DISTANCIA_ENTRE_RODAS * PID.getPID()) / (2 * RAIO);

		return new float[] { Wd, We };

	}

	public static void init(boolean initMotors) {
		if(initMotors == true){
			rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
			rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
			motorG = new EV3MediumRegulatedMotor(MotorPort.C);
			
		}
	}

}
