package sek2016;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 *Controla o PID
 */
public class PID implements Runnable {
	
	/**
	 * @pidRunning
	 * variavel que determina se a Thread do PID esta pausada ou
	 * rodando, lembre-se de reajustar a velocidade dos motores quando necessario.
	 */
	public static boolean pidRunning;
	
	// ------------Variáveis do PID-----------------------------
	private static float PID = 0, // valor final do PID para calculo da velocidade das rodas
			e = 0, // erro, diferença de angulo entre o valor de zero do gyro e o valor lido do gyro
			eAnt = 0, // erro na execucao anterior do pid
			P = 0, // valor do controle proporcional
			I = 0, // valor do controle integral
			D = 0, // valor do controle derivativo
			Kp = 1f, // parametro do controle proporcional
			Ki = 0.003f, // parametro do controle integral
			Kd = 0.05f, // parametro do controle derivativo
			angEsperado = 0f, // 
			angReal = 0f; // 
	
	/**
	 * metodo que zera o PID
	 */
	public static void zeraPID(){
		PID = 0;
		e = 0;
		eAnt = 0;
		P = 0;
		I = 0;
		D = 0;
		angEsperado = 0f;
		angReal = 0f;
	}
	
	/**
	 * Thread PID
	 */
	@Override
	public void run() {
		while(AlienRescue.alienRescueON){
			while(!pidRunning && AlienRescue.alienRescueON){
				Delay.msDelay(10);
			}
			calculaPID();
			Navigation.setVelocidade();
			// apagar daqui pra frente
		}
	}
	
	/**
	 * metodo que calcula o PID
	 */
	private static void calculaPID() {
		angReal = Sensors.getAngle();


		if (angReal != 0) {
			e = angReal - angEsperado;
		}
		P = Kp * e;
		I += e * Ki;
		D = (eAnt - e) * Kd;
		eAnt = e;
		PID = P + I + D;
	}
	
	/**
	 * @return Retorna o valor atual da variavel PID
	 */
	public static float getPID(){
		return PID;

	}

	public static void setAngEsperado(float angEsper){
		angEsperado = angEsper;

	}
	public static float getAngEsperado(){
		return angEsperado;
	}

}
