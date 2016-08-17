package sek2016;


import lejos.hardware.Button;
import lejos.utility.Delay;

public class AlienRescue implements Runnable{
/*	private static boolean initMotors = !EV3MainMenuClass.jaInstanciado,
			initUS = !EV3MainMenuClass.jaInstanciado,
			initGyro = !EV3MainMenuClass.jaInstanciado,
			initDollColor  = !EV3MainMenuClass.jaInstanciado,
			initFloorColor = !EV3MainMenuClass.jaInstanciado;*/
	
	public static Thread threadPID;
	
	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		EV3MainMenuClass.AlienRescueShutdown = false;
		EV3MainMenuClass.AlienRescueON = true;
		//init(); iniciando na thread do menu
		threadPID = new Thread(new PID());
		threadPID.setDaemon(true);
		threadPID.setName("threadPID");
		threadPID.start();
		Navigation.openGarra();
		Navigation.forward();
		while (!EV3MainMenuClass.AlienRescueShutdown){
			if(Sensors.verificaObstaculo()==true){
				Navigation.stop();
				Navigation.closeGarra();
				EV3MainMenuClass.AlienRescueShutdown = true;
			}
			
			else{
				//Navigation.forward();
			}
		}	
		Navigation.stop();
		Delay.msDelay(500);
		EV3MainMenuClass.AlienRescueON = false;
		}

/*	public static void init() {
		Navigation.init(initMotors);
		Sensors.init(initDollColor,initUS,initFloorColor,initGyro);
		EV3MainMenuClass.jaInstanciado = true;
	}*/

}
