package testes_variados;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

/**
 * Classe teste somente para encontrar o raio da roda empiricamente
 * @author Rogério
 *
 */
public class Teste_largura_robo {
	static EV3LargeRegulatedMotor rodaE = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor rodaD = new EV3LargeRegulatedMotor(MotorPort.B);
	public static void main(String[] args){
		rodaE.setAcceleration(400);
		rodaD.setAcceleration(400);
		rodaE.setSpeed(300);
		rodaD.setSpeed(300);

		float graus = 360f; // angulo que o robo vai girar em graus
		float raio = 0.0280f; // raio da roda em metros (verificado antes na classe Teste_raio_roda)
		float l = 0.1378f; // distancia entre as rodas em metros
		float angle = (graus*l)/(2*raio); // angulo que cada roda tera que girar
		rodaE.rotate((int)angle);
		rodaD.rotate((int)angle);
	}
}