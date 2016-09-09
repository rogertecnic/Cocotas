package testes_variados;

import lejos.utility.Delay;


public class Teste_raio_roda {
	private static final float graus_rad = (float)Math.PI/180,
			rad_graus = 180/(float)Math.PI;


	public static float
			raio_roda = 0.0272f; // raio da roda em metros
	private static int aceleration = MainTeste.aceleration;
	
	public static void testaRaio(float dist){
		if(Testa_PID.running == false){
			Testa_PID.running = true;
			while(Testa_PID.zerado){
				
			}
		}
		Testa_PID.running = false;
		while(!Testa_PID.zerado){
			Delay.msDelay(20);
		}
		Testa_PID.running = true;
		
		float theta; // graus que a roda deve girar theta=dist/r
		theta=(dist/raio_roda)*rad_graus; 
		float positionE = MainTeste.rodaE.getTachoCount(); // posicao em graus da roda e
		float positionD = MainTeste.rodaD.getTachoCount(); // posicao em graus da roda d
		MainTeste.rodaE.forward();
		MainTeste.rodaD.forward();
		float wRoda = Testa_PID.VELO_INI/raio_roda*rad_graus;
		float ang_defasado = wRoda*(wRoda/aceleration)-(aceleration/2)*(wRoda/aceleration)*(wRoda/aceleration);
		while(MainTeste.rodaE.getTachoCount()<(positionE+theta-ang_defasado) && 
				MainTeste.rodaD.getTachoCount()<(positionD+theta-ang_defasado)){
		}
		

		MainTeste.rodaE.stop(true);
		MainTeste.rodaD.stop(true);
		while(MainTeste.rodaE.isMoving() ||
		MainTeste.rodaD.isMoving()){
			System.out.println("esperando parar da reta");
		}

		Testa_PID.running = false;
		while(!Testa_PID.zerado){
			Delay.msDelay(20);
		}
		System.out.println("PAREI");
	}
	
}
