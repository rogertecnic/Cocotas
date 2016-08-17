package sek2016;


import lejos.hardware.Button;
import lejos.utility.Delay;

public class AlienRescue implements Runnable{
	/**
	 * Thread que comanda o PID
	 */
	public static Thread threadPID;

	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		try{
			threadPID = new Thread(new PID());
			threadPID.setDaemon(true);
			threadPID.setName("threadPID");
			PID.pidON = true;
			threadPID.start();

			boolean flag = true;
			Navigation.openGarra();
			PID.pidON = false;
			Navigation.setVelocidade(90);
			Navigation.turn(360*2);
			PID.pidON = true;
			//Navigation.forward();
			while (flag){
				if(Sensors.verificaObstaculo()==true){
					Navigation.stop();
					Navigation.closeGarra();
					flag = false;
				}
				else{
				}
			}	
			EV3MainMenuClass.AlienRescueON = false;
		}
		catch(ThreadDeath e){
			e.getStackTrace();
		}
	}

}
