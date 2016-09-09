package sek2016;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * 
 * @author Equipe Sek 2016:<br>
 * Diego Costa<br>
 * Karinny Golçalves<br>
 * Lucas *não sei o sobrenome*<br>
 * Mariana *nao sei o sobrenome*<br>
 * Rogério Pereira Batista - Eng. Elétrica<br>
 */
public class AlienRescue implements Runnable{
	/**
	 * Variavel global que indica se a Thread do programa
	 * está executando (ON) ou fechada (OFF)
	 */
	public static boolean alienRescueON;
	
	/**
	 * Thread que comanda a execução do PID
	 */
	public static Thread threadPID;

	/**
	 * Metodo que rege todo o codigo do robo
	 */
	@Override
	public void run() {
		try{ // o codigo deve ficar dentro desse try gigante
//======INICIO DO CODIGO=============================================================
			threadPID = new Thread(new PID());
			threadPID.setDaemon(true);
			threadPID.setName("threadPID");
			PID.pidRunning = true;
			threadPID.start();
			Navigation.setAcceleration(Navigation.aceleration,Navigation.aceleration);
			
			//victorySong();
			boolean flag = false; // utilidade de testes
			Navigation.openGarra();
			Navigation.forward(0.5f);
			Navigation.turn(-90);
			Navigation.forward(0.5f);
			Navigation.turn(-90);
			Navigation.forward(0.5f);
			Navigation.turn(-90);
			Navigation.forward(0.5f);
			Navigation.turn(-90);
			
			while (flag){
				if(Sensors.verificaObstaculo()==true){
					Navigation.stop();
					Navigation.closeGarra();
					flag = false;
				}
				else{
				}
			}
			
//======FINAL DO CODIGO=============================================================
			alienRescueON = false;
		}
		catch(ThreadDeath e){// quando o menu é chamado, essa thread é desligada e lança essa exception
			e.getStackTrace();
		}
	}
	
	private static void victorySong(){
		Sound.setVolume(50);
		Sound.playTone(3000, 100);
		Sound.playTone(4000, 100);
		Sound.playTone(4500, 100);
		Sound.playTone(5000, 100);
		Delay.msDelay(80);
		Sound.playTone(3000, 200);
		Sound.playTone(5000, 500);
	}
}
