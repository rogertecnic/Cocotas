package sek2016;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

/**
 * 
 * @author Rogerio
 *Classe que controla a Thread main (metodo main) e 
 *chama a Thread do codigo da sek da classe AlienRescue
 */
public class EV3MainMenuClass {
	/**
	 * @exit
	 *  variavel que confirma se é pra sair do programa todo
	 */
	private static boolean exit = false;
	
	/**
	 * @MotorInstanciado
	 * Variavel que controla a instancia dos sensores e motores.<br>
	 * <b>true:</b> ja foi instanciado, (resetar gyroscopio);<br>
	 * <b>false:</b> ainda não foi instanciado, (primeira execução do codigo);
	 */
	private static boolean MotorInstanciado = false;
	
	/**
	 * @threadPrograma
	 *  principal do codigo, é instanciada dentro do selecionaOpcao
	 */
	public static Thread threadPrograma = null;
	
	
	//=====================MAIN===================================
	public static void main(String[] args) {
		while (!exit) {
			controleMenu();
			if (!exit) {
				Button.waitForAnyPress();
				Navigation.stop();
				AlienRescue.alienRescueON = false;
				threadPrograma.stop();
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
	public static void selecionaOpcao(int opcao) {
		switch (opcao) {
		case 1: {
			AlienRescue.alienRescueON = true;
			Navigation.init(!MotorInstanciado);
			Sensors.init(!MotorInstanciado,!MotorInstanciado,!MotorInstanciado,!MotorInstanciado);
			MotorInstanciado = true;
			Sensors.resetAngle();
			threadPrograma = new Thread(new AlienRescue());
			threadPrograma.setDaemon(true);
			threadPrograma.setName("AlienRescue");
			threadPrograma.start();
			break;
		}
		case 2: {
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
