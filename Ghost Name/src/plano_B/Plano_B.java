package plano_B;

import cx.ath.matthew.debug.Debug;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import sek2016.EV3MainMenuClass;
import sek2016.Navigation;
import sek2016.Sensors;

public class Plano_B {
	//======================VARIAVEIS DE CONDICAO INICIAL DA ARENA=================
	public static int
	c1 = 0, c2 = 0, c3 = 0, // contadora auxiliar usada em varios casos
	configParede = 0,
	cor_resgate = 0,
	configArena = 0, // config A = 1, config B = 2, config C = 3
	configCave = 0; // da uma lida no metodo mostraMenu2
	public static boolean
	planob = true,
	arenaSek = true,
	bonecoNoCentro = false; // autoexplicativo (alterado a cada reinicio)


	//========================CONSTANTES int==========================
	public static final int
	ARENA_A = 1,
	ARENA_B = 2,
	ARENA_C = 3,
	CAV_DIR = 1,
	CAV_ESQ = 2,
	CAV_CIMA = 3;


	/**
	 * Metodo que rege todo o plano B, seria o main do plano B
	 */
	public static void partiu(){
		configArena = EV3MainMenuClass.configArena;
		configCave = EV3MainMenuClass.configCave;
		bonecoNoCentro = EV3MainMenuClass.bonecoNoCentro;
		Navegacao_secundaria.initVariaveis();
		cor_resgate = Sensors.verificaFloor();
		printDebug("RESGATAR: " + (cor_resgate == 3? "BRANCO":"PRETO"));
		Delay.msDelay(500);
		Navegacao_secundaria.inicioSemBoneco(configArena, configCave); // testar todas as 9 possibilidades
		trocaModulo(arenaSek);
		sequenciaBuscaParede(); // testar se esta buscando e resgatando mesmo



	}


	public static void mostraMenuParede(int configParede){
		LCD.clear();
		switch(configParede){
		case 1:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    |     \\ |", 0, 3);
			LCD.drawString("    |      \\|", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 2:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    |    ---|", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 3:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |      /|", 0, 2);
			LCD.drawString("    |     / |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 4:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ----|----", 0, 1);
			LCD.drawString("    |   |   |", 0, 2);
			LCD.drawString("    |   |   |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 5:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |\\      |", 0, 2);
			LCD.drawString("    | \\     |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 6:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    |---    |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 7:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<1> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    | /     |", 0, 3);
			LCD.drawString("    |/      |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		}
	}
	
	/**
	 * Metodo que controla o menu da posicao da parede
	 * no modulo que tem a parede
	 */
	public static void controleMenuParede() {
		int parede = 1; // indica qual opcao foi selecionada no menu da arena
		boolean 
		noMenu = true;
		//================MENU DA PAREDE==========
		while (noMenu) {
			mostraMenuParede(parede);
			switch (Button.waitForAnyPress()) {
			case Button.ID_RIGHT: {
				if(parede<7)
				parede ++;
				else parede = 1;
				break;
			}
			case Button.ID_LEFT: {
				if(parede>1)
					parede --;
					else parede = 7;
				break;
			}
			case Button.ID_ENTER: {
				configParede = parede;
				LCD.clear();
				noMenu = false;
				break;
			}
			}
		}
	}
	/**
	 * Metodo que procura os bonecos na sala que tem parede
	 * deve se preocupar com a parede tambem
	 */
	public static void sequenciaBuscaParede() {
		c1 = 0; c2 = 0; c3 = 0;
		boolean achou = false,
				resgatar = false;
		while(!resgatar ){
			achou = Navegacao_secundaria.giroBusca(45, Navegacao_secundaria.PAREDE); // testar se esta virando e indo ate o boneco e fechando a garra
			if(achou){
				resgatar = Navegacao_secundaria.verificaBoneco(); // verificar se esta retornando o correto inclusive com o vermelho deve retornar false
				if(resgatar){
					printDebug("RESGATAR");
					Navegacao_secundaria.voltaNaPilha(Navegacao_secundaria.PAREDE);
					trocaModulo(arenaSek);
					Navegacao_secundaria.voltaNaPilha(Navegacao_secundaria.CENTRAL);
					Navigation.openGarra();
					// aqui ele volta e tem que reiniciar, objetivo concluido
				}else{
					printDebug("KILL HIM!!");
					Navegacao_secundaria.tirarBonecoErrado(Navegacao_secundaria.PAREDE);
					achou = false;
					c1=0;
					c2=0;
				}
				
			}else{
				printDebug("NAO ACHOU!!!");
				switch(configParede){
				case 1:{
					if(c1<2){
						Navigation.andar(0.15f);
						Navegacao_secundaria.pushSegmento(0, 0.15f, Navegacao_secundaria.PAREDE);
						c1++;
					}else{
						if(c2==0){
							Navigation.turn(90);
							Navigation.andar(0.15f);
							Navegacao_secundaria.pushSegmento(90, 0.1f, Navegacao_secundaria.PAREDE);
							c1=0;
							c2=1;
						}else{
							Navigation.turn(-90);
							Navigation.andar(0.15f);
							Navegacao_secundaria.pushSegmento(-90, 0.15f, Navegacao_secundaria.PAREDE);
							c1=0;
							c2=0;
						}
					}
				break;
				}
				
				}
			}
		}
	}
	
	
	//==============METODOS DE DEBUG=========
	public static void printDebug(String word){
		LCD.clear();
		LCD.drawString(word, 0, 0);
	}
	
	/**
	 * espera apertar o botao pra cima
	 * @param troca
	 */
	public static void trocaModulo(boolean troca){
		if(troca){
			Button.UP.waitForPressAndRelease();
		}
	}
}

