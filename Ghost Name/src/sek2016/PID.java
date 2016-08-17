package sek2016;

import java.lang.Thread.State;

import lejos.hardware.Sound;

public class PID implements Runnable {

	// ------------Variáveis do PID----------------------------------
	private static float PID = 0,
			e = 0,
			eAnt = 0,
			P = 0,
			I = 0,
			D = 0,
			Kp = 1f,
			Ki = 0.003f,
			Kd = 0.05f,
			angEsperado = 0f, angReal = 0f;

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
	@Override
	public void run() {
		try{
			while(EV3MainMenuClass.AlienRescueON){
				PIDAngle();
				Navigation.setVelocidade();
			}
		}catch(ThreadDeath e){
			e.getStackTrace();
		}
	}

	private static void PIDAngle() {
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
