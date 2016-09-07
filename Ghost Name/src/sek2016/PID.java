package sek2016;

import java.util.ArrayList;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 *Controla o PID
 */
public class PID implements Runnable {
	//public static ArrayList<Float> lista = new ArrayList<Float>(); //teste
	
	/**
	 * @pidRunning
	 * variavel que determina se a Thread do PID esta pausada ou
	 * rodando, lembre-se de reajustar a velocidade dos motores quando necessario.
	 */
	public static boolean pidRunning;
	
	// ------------Variáveis do PID-----------------------------
	public static float PID = 0, // valor final do PID para calculo da velocidade das rodas
			e = 0, // erro, diferença de angulo entre o valor de zero do gyro e o valor lido do gyro
			eAnt = 0, // erro na execucao anterior do pid
			P = 0, // valor do controle proporcional
			I = 0, // valor do controle integral
			D = 0, // valor do controle derivativo
			Kp = 2f, // parametro do controle proporcional
			Ki = 0.03f, // parametro do controle integral
			Kd = 0.0025f, // parametro do controle derivativo
			angEsperado = 0f, // 
			angReal = 0f; //
			
			/**
			 * [0]: velocidade angular em graus/s<br>
			 * [1]: posicao angular em graus
			 */
	public static float[] veloAng = new float[2];
	
	// ------------Metodos do PID-----------------------------
	/**
	 * metodo que zera: PID, erro, erro anterior, angulo esperado (objetivo)
	 * e angulo real (lido pelo gyro)
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
		Sensors.resetAngle();
		zeraPID();
		//lista.clear();
		while(AlienRescue.alienRescueON){
			calculaPID();
			Navigation.setVelocidade();
			while(!pidRunning && AlienRescue.alienRescueON){
				Delay.msDelay(20);
			}
		}
		Sensors.resetAngle();
		zeraPID();
	}
	
	/**
	 * metodo que calcula o PID
	 */
	public static void calculaPID() {
		angReal = Sensors.getAngle();
		veloAng = Sensors.getAllGyro();
		//System.out.println("ang:"+angReal+"   ac:" + veloAng[1]);
		//if(veloAng[0]!=0)
		//lista.add(new Float(veloAng[0]));
		
		//if(lista.size() >=100){
			//AlienRescue.alienRescueON = false;
		//}
		
		e = angReal - angEsperado;
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
