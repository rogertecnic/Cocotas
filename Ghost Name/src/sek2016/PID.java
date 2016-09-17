package sek2016;

import java.util.ArrayList;

import lejos.hardware.Sound;
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
	public static boolean pidRunning = false;
	
	
	// ------------Variáveis do PID-----------------------------
	public static float PID = 0, // valor final do PID para calculo da velocidade das rodas
			e = 0, // erro, diferença de angulo entre o valor de zero do gyro e o valor lido do gyro
			eAnt = 0, // erro na execucao anterior do pid
			P = 0, // valor do controle proporcional
			I = 0, // valor do controle integral
			D = 0, // valor do controle derivativo
			Kp = 2f, // parametro do controle proporcional
			Ki = 0.03f, // parametro do controle integral
			Kd = 0.0025f; // parametro do controle derivativo
	public static float[] WdWe = new float[2]; //Velocidade angular da roda Direita e Esquerda
	
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
		Sensors.resetAngle();
	}
	
	/**
	 * Thread PID
	 */
	@Override
	public void run() {
		zeraPID();
		while(AlienRescue.alienRescueON){
			calculaPID();
			setWdWePID();
			while(!pidRunning && AlienRescue.alienRescueON){
				zeraPID();
				Delay.msDelay(50);
			}
		}
	}
	
	/**
	 * metodo que calcula o PID
	 */
	public static void calculaPID() {
		e  = Sensors.getAngle();
		P = Kp * e;
		I += e * Ki;
		D = (eAnt - e) * Kd;
		eAnt = e;
		PID = P + I + D;
	}
	
	/**
	 * Metodo que converte o valor do PID para velocidades
	 * angulares das rodas em graus/seg
	 * @return float[]:<br>
	 
	 */
	
	/**
	 * seta velocidade de cada roda de acordo com o PID em graus/s DA RODA<br>
	 * Vetor WdWe de duas posicoes:<Br>
	 * [0] Wd, graus/seg da roda direita;<br>
	 * [1] We, graus/seg da roda esquerda;
	 */
	public static void setWdWePID(){
		WdWe[0] = (2 * (float)(180/Math.PI)*Navigation.VELO_INI - Navigation.DISTANCIA_ENTRE_RODAS * PID) / (2 * Navigation.RAIO);
		WdWe[1] = (2 * (float)(180/Math.PI)*Navigation.VELO_INI + Navigation.DISTANCIA_ENTRE_RODAS * PID) / (2 * Navigation.RAIO);
		Navigation.rodaD.setSpeed(WdWe[0]);
		Navigation.rodaE.setSpeed(WdWe[1]);
	}
}
