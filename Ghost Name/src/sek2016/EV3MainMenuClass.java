package sek2016;

import java.lang.Thread.State;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class EV3MainMenuClass {
	/**
	 * Thread principal do codigo
	 */
	public static Thread threadPrograma = new Thread(new AlienRescue());
	public static boolean AlienRescueON = false;
	public static boolean AlienRescueShutdown = false;

	/**
	 * @exit encerrar o programa todo
	 */
	private static boolean exit = false;

	/**
	 * Variavel que controla a instancia dos sensores e motores.<br>
	 * <b>true:</b> ja foi instanciado, (resetar gyroscopio);<br>
	 * <b>false:</b> ainda não foi instanciado, (primeira execução do codigo);
	 */
	public static boolean jaInstanciado = false;
	//=====================MAIN===================================
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		threadPrograma.setDaemon(true);
		threadPrograma.setName("AlienRescue");
		while (!exit) {
			controleMenu();
			if (!exit) {
				Button.waitForAnyPress();
				Navigation.stop();
				Navigation.close();
				Sensors.close();
				//AlienRescueON = false;
				//threadPrograma.interrupt(); // menu aparece mas a thread não para (seta status de interrupção para possivel parada prevista dentro da thread)
				threadPrograma.stop(); // menu aparece mas lança exceção não se sabe onde exatamente (exception ThreadDeath)
				//threadPrograma.suspend(); // menu aparece mas a thread não para (continua running) não se sabe o que ocorre
			}
		}
	}
	//=============================================================

	/**
	 * Métpdo que imprime o menu <br>
	 * @param opcao inteiro que indica qual opção está realçada:<br>
	 * <b>1:</b> (Re)iniciar o codigo<br>
	 * <b>2:</b> sai do programa<br>
	 */
	public static void mostraMenu(int opcao) {
		LCD.clear();
		LCD.drawString("MENU DE CODIGO", 0, 0);
		LCD.drawString((opcao == 1 ? ">" : " ") + " (Re)iniciar codigo", 0, 1);
		LCD.drawString((opcao == 2 ? ">" : " ") + " Sair", 0, 2);
	}

	/**
	 * Metodo que seleciona a opção realçada no menu
	 * @param opcao  inteiro que indica qual opção está realçada:<br>
	 * <b>1:</b> (Re)iniciar o codigo<br>
	 * <b>2:</b> sai do programa<br>
	 */
	@SuppressWarnings("deprecation")
	public static void selecionaOpcao(int opcao) {
		State s = threadPrograma.getState();
		switch (opcao) {
		case 1: {
			Navigation.init(!jaInstanciado);
			Sensors.init(!jaInstanciado,!jaInstanciado,!jaInstanciado,!jaInstanciado);
			jaInstanciado = true;
			Sensors.resetAngle();
			if (s == State.NEW)
				threadPrograma.start();
			else if (s == State.TIMED_WAITING) {
				threadPrograma.resume();
				threadPrograma.interrupt();
				threadPrograma = new Thread(new AlienRescue());
				threadPrograma.setDaemon(true);
				threadPrograma.setName("AlienRescue");
				threadPrograma.start();
			} else if(s == State.TERMINATED){
				threadPrograma = new Thread(new AlienRescue());
				threadPrograma.setDaemon(true);
				threadPrograma.setName("AlienRescue");
				threadPrograma.start();
			}
			break;
		}
		case 2: {
			if (s == State.NEW) {
				threadPrograma.start();
				threadPrograma.interrupt();
			} else if (s == State.TIMED_WAITING) {
				threadPrograma.resume();
				threadPrograma.interrupt();
			} else if (s == State.RUNNABLE)
				threadPrograma.interrupt();
			else if (s == State.WAITING)
				threadPrograma.interrupt();

			exit = true;
			break;
		}
		}
	}

	/**
	 * Metodo que faz o controle do menu, a interação da tecla com o menu
	 */
	public static void controleMenu() {
		int opcao = 1;// indica qual opcao foi selecionada
		boolean noMenu = true;
		while (noMenu) {
			mostraMenu(opcao);
			switch (Button.waitForAnyPress()) {
			case Button.ID_DOWN: {
				if (opcao == 2)
					opcao = 1;
				else
					opcao++;
				break;
			}
			case Button.ID_UP: {
				if (opcao == 1)
					opcao = 2;
				else
					opcao--;
				break;
			}
			case Button.ID_ENTER: {
				LCD.clear();
				noMenu = false;
				break;
			}
			}
		}
		selecionaOpcao(opcao);
	}
}
