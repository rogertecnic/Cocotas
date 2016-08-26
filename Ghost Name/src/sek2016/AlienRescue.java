package sek2016;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * 
 * @author Equipe Sek 2016:<br>
 * Diego Costa<br>
 * Karinny Gol�alves<br>
 * Lucas *n�o sei o sobrenome*<br>
 * Mariana *nao sei o sobrenome*<br>
 * Rog�rio Pereira Batista - Eng. El�trica<br>
 */
public class AlienRescue implements Runnable{
	/**
	 * Variavel global que indica se a Thread do programa
	 * est� executando (ON) ou fechada (OFF)
	 */
	public static boolean alienRescueON;
	
	/**
	 * Thread que comanda a execu��o do PID
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
			Navigation.setAcceleration(700, 700);
			
			boolean flag = true; // utilidade de testes
			Navigation.openGarra();
			//Navigation.forward(1.5f);
			PID.pidRunning = false;
			Navigation.setVelocidade(360);
			Navigation.turn(360*5);
			PID.pidRunning = true;
			//Navigation.forward();
			
			/*while (flag){
				if(Sensors.verificaObstaculo()==true){
					Navigation.stop();
					Navigation.closeGarra();
					flag = false;
				}
				else{
				}
			}*/
			
//======FINAL DO CODIGO=============================================================
			alienRescueON = false;
		}
		catch(ThreadDeath e){// quando o menu � chamado, essa thread � desligada e lan�a essa exception
			e.getStackTrace();
		}
	}
}
