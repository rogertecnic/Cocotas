package sek2016;


import lejos.hardware.Button;
import lejos.utility.Delay;

public class AlienRescue {
	private static boolean initMotors = true,
			initUS = true,
			initGyro = true,
			initDollColor  = false,
			initFloorColor = false;
	public static void main(String[] args) {
		boolean Flag = false;
		init();
		PID pidAngle = new PID();
		Thread pid = new Thread(pidAngle);
		Navigation nav = new Navigation();
	//	Thread navigation = new Thread(nav);
		pid.start();
		while (Button.ESCAPE.isUp()){
			if(Sensors.verificaObstaculo()==true && Flag == false){
				System.out.println("Angulo: "+Sensors.getAngle());
				Navigation.stop();
				Navigation.closeGarra();
				Flag = true;
				Navigation.turnLeft();
				Navigation.turnLeft();
				
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
		Sensors.init(initDollColor,initUS,initFloorColor,initGyro );
	}
}
