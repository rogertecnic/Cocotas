package sek2016;


import lejos.hardware.Button;
import lejos.utility.Delay;

public class AlienRescue implements Runnable{
	private static boolean initMotors = !EV3MainMenuClass.jaInstanciado,
			initUS = !EV3MainMenuClass.jaInstanciado,
			initGyro = !EV3MainMenuClass.jaInstanciado,
			initDollColor  = !EV3MainMenuClass.jaInstanciado,
			initFloorColor = !EV3MainMenuClass.jaInstanciado;
	
	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		init();
		PID pidAngle = new PID();
		Thread pid = new Thread(pidAngle);
		Navigation nav = new Navigation();
	//	Thread navigation = new Thread(nav);
		pid.start();
		while (Button.ESCAPE.isUp()){
			if(Sensors.verificaObstaculo()==true){
				System.out.println("Angulo: "+Sensors.getAngle());
				Navigation.stop();
				Navigation.closeGarra();
				
			}
			
			else{
				Navigation.forward();
			}
		}
		Navigation.closeGarra();
		Delay.msDelay(100);
		Navigation.openGarra();
		
	}

	public static void init() {
		Navigation.init(initMotors);
		Sensors.init(initDollColor,initUS,initFloorColor,initGyro);
		EV3MainMenuClass.jaInstanciado = true;
	}

	
}
