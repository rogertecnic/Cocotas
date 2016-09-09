package testes_variados;

import lejos.utility.Delay;

public class Teste_largura_robo {
	private static final float graus_rad = (float)Math.PI/180,
			rad_graus = 180/(float)Math.PI;
	private static int aceleration = MainTeste.aceleration;

	public static float
	l = 0.13445f; // distancia entre as rodas em metros


	public static void testa_largura(float graus){
		
		Testa_PID.running = false;
		while(!Testa_PID.zerado){
			Delay.msDelay(20);
		}
		System.out.println("CURVA");
		float wRoda = 0.1f/Teste_raio_roda.raio_roda*rad_graus;
		MainTeste.rodaE.setSpeed(wRoda);
		MainTeste.rodaD.setSpeed(wRoda);

		float theta = (graus*l)/(2*Teste_raio_roda.raio_roda); // angulo que cada roda tera que girar
		float positionfinalE = MainTeste.rodaE.getTachoCount(); // posicao em graus da roda e
		float positionfinalD = MainTeste.rodaD.getTachoCount(); // posicao em graus da roda d

		if(graus>0){
			MainTeste.rodaD.forward();
			MainTeste.rodaE.backward();
			float ang_defasado = wRoda*(wRoda/aceleration)-(aceleration/2)*(wRoda/aceleration)*(wRoda/aceleration);
			while(MainTeste.rodaE.getTachoCount()>(positionfinalE-theta+ang_defasado) && 
					MainTeste.rodaD.getTachoCount()<(positionfinalD+theta-ang_defasado)){
			}
		}
		if(graus<0){
			MainTeste.rodaE.forward();
			MainTeste.rodaD.backward();
			float ang_defasado = wRoda*(wRoda/aceleration)-(aceleration/2)*(wRoda/aceleration)*(wRoda/aceleration);
			while(MainTeste.rodaD.getTachoCount()>(positionfinalD+theta+ang_defasado) && 
					MainTeste.rodaE.getTachoCount()<(positionfinalE-theta-ang_defasado)){
			}
		}
		MainTeste.rodaE.stop(true);
		MainTeste.rodaD.stop(true);
		while(MainTeste.rodaE.isMoving() ||
		MainTeste.rodaD.isMoving()){
			System.out.println("esperando parar da curva");
		}
		System.out.println("PAREI");
	}
}