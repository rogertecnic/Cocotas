package testes_variados;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

/**
 * Classe teste somente para encontrar o raio da roda empiricamente
 * @author Rogério
 *
 */
public class Teste_raio_roda {
	static EV3LargeRegulatedMotor rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
	public static void main(String[] args){
		rodaE.setAcceleration(400);
		rodaD.setAcceleration(400);
		rodaE.setSpeed(300);
		rodaD.setSpeed(300);
		
		float dist = 1.5f; // distancia andada em metros
		float raio = 0.0280f; // raio da roda em metros
		float angle = (dist*180)/(raio*3.14159265f);
		rodaE.rotate((int)angle);
		rodaD.rotate((int)angle);
	}
}