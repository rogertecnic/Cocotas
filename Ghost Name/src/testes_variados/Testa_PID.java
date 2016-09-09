package testes_variados;

import lejos.utility.Delay;

public class Testa_PID implements Runnable {
	private static final float graus_rad = (float)Math.PI/180,
			rad_graus = 180/(float)Math.PI;

	public static float PID = 0,
			e = 0, // erro, diferença de angulo entre o valor de zero do gyro e o valor lido do gyro
			eAnt = 0, // erro na execucao anterior do pid
			P = 0, // valor do controle proporcional
			I = 0, // valor do controle integral
			D = 0, // valor do controle derivativo
			Kp = 2f, // parametro do controle proporcional
			Ki = 0.03f, // parametro do controle integral
			Kd = 0.025f, // parametro do controle derivativo
			Wd = 0,
			We = 0,
			VELO_INI =0.14f; // v=w*r*rad-graus
	public static float[] ang = new float[1];
	public static boolean running = true;
	public static boolean zerado = false;
	// ------------Metodos do PID-----------------------------
	@Override
	public void run() {
		while(true){
			if(!running){
				PID = 0;
				e = 0;
				eAnt = 0;
				P = 0;
				I = 0; 
				D = 0; 
				Wd = 0;
				We = 0;
				VELO_INI =0.14f; // v=w*r*rad-graus
				Delay.msDelay(20);
				MainTeste.gyro.reset();
				zerado = true;
				while(!running){
				}
			}

			MainTeste.gyro.getAngleMode().fetchSample(ang, 0);
			System.out.println(ang[0]);
			e = ang[0];
			P = Kp * e;
			I += e * Ki;
			D = (eAnt - e) * Kd;
			eAnt = e;
			PID = P + I + D;

			Wd = (2 * rad_graus*VELO_INI - Teste_largura_robo.l * PID) / (2 * Teste_raio_roda.raio_roda);
			We = (2 * rad_graus*VELO_INI + Teste_largura_robo.l * PID) / (2 * Teste_raio_roda.raio_roda);
			MainTeste.rodaD.setSpeed(Wd);
			MainTeste.rodaE.setSpeed(We);
			zerado = false;
		}
	}
}
