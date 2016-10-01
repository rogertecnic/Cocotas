package plano_B;

import cx.ath.matthew.debug.Debug;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import sek2016.MainMenuClass;
import sek2016.Navigation;
import sek2016.Sensors;

public class Plano_B {
	//======================VARIAVEIS DE CONDICAO INICIAL DA ARENA=================
	public static int
	c1 = 0, c2 = 0, c3 = 0, // contadora auxiliar usada em varios casos
	configObstaculo = 0, // posicao do obstaculo
	cor_resgate = 0, // cor que deve ser resgatada
	modIniciaBusca = 0, // modulo que vai iniciar o resgate
	configArena = 0, // config A = 1, config B = 2, config C = 3
	configCave = 0; // da uma lida no metodo mostraMenu2

	//====================VARIAVEIS QUE CONTROLAM O PLANO B============
	public static boolean
	planob = true, // true se o plano b for executado, false se nao
	arenaSek = true; // true se a arena que vamos rodar for a nossa, false se nao



	/**
	 * Metodo que rege todo o plano B, seria o main do plano B
	 */
	public static void partiu(){
		configArena = MainMenuClass.configArena;
		configCave = MainMenuClass.configCave;
		modIniciaBusca = MainMenuClass.modIniciaBusca;
		cor_resgate = Sensors.verificaFloor();
		Navegacao_secundaria.initVariaveis();

		printDebug("RESGATAR: " + (cor_resgate == 3? "BRANCO":"PRETO"));
		Delay.msDelay(500); // TIRAR DEPOIS

		switch(MainMenuClass.modIniciaBusca){
		case Const.CENTRAL:{
			Navegacao_secundaria.inicioModuloCentral();
			trocaModulo(arenaSek);
			sequenciaBuscaCentral();
			break;
		}case Const.CAVE:{
			Navegacao_secundaria.inicioModuloCaverna(configArena, configCave);
			trocaModulo(arenaSek);
			sequenciaBuscaCave();
			break;
		}case Const.OBSTACULO:{
			Navegacao_secundaria.inicioModuloObstaculo(configArena, configCave); // testar todas as 9 possibilidades
			trocaModulo(arenaSek);
			sequenciaBuscaObstaculo(); // testar se esta buscando e resgatando mesmo
			break;
		}
		}





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
			LCD.drawString("<2> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    |    ---|", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 3:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<3> ---------", 0, 1);
			LCD.drawString("    |      /|", 0, 2);
			LCD.drawString("    |     / |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 4:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<4> ----|----", 0, 1);
			LCD.drawString("    |   |   |", 0, 2);
			LCD.drawString("    |   |   |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 5:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<5> ---------", 0, 1);
			LCD.drawString("    |\\      |", 0, 2);
			LCD.drawString("    | \\     |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 6:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<6> ---------", 0, 1);
			LCD.drawString("    |       |", 0, 2);
			LCD.drawString("    |---    |", 0, 3);
			LCD.drawString("    |       |", 0, 4);
			LCD.drawString("    |--   --|", 0, 5);
			break;
		}
		case 7:{
			LCD.drawString("CONFIG PAREDE", 0, 0);
			LCD.drawString("<7> ---------", 0, 1);
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
				configObstaculo = parede;
				LCD.clear();
				noMenu = false;
				break;
			}
			}
		}
	}

	/**
	 * Ainda nao feito
	 */
	private static void sequenciaBuscaCentral(){
		// FAZER
	}

	/**
	 * Ainda nao feito
	 */
	private static void sequenciaBuscaCave(){
		// FAZER
	}

	/**
	 * Metodo que procura os bonecos na sala que tem parede
	 * deve se preocupar com a parede tambem
	 */
	private static void sequenciaBuscaObstaculo() {
		c1 = 0;
		float distAndaProcura = 0.15f;
		boolean achou = false,
				resgatar = false;
		while(!resgatar ){
			achou = Navegacao_secundaria.giroBusca(45, Const.OBSTACULO); // testar se esta virando e indo ate o boneco e fechando a garra
			if(achou){
				resgatar = Navegacao_secundaria.verificaBoneco(); // verificar se esta retornando o correto inclusive com o vermelho deve retornar false
				if(resgatar){
					printDebug("RESGATAR");
					Navegacao_secundaria.voltaNaPilha(Const.OBSTACULO);
					trocaModulo(arenaSek);
					Navegacao_secundaria.voltaNaPilha(Const.CENTRAL);
					Navigation.openGarra();
					// aqui ele volta e tem que reiniciar, objetivo concluido
				}else{
					printDebug("KILL HIM!!");
					Delay.msDelay(1000);
					Navegacao_secundaria.tirarBonecoErrado(Const.OBSTACULO);
					achou = false;
					c1=0;
					c2=0;
					c3 = 0;
				}

			}else{
				printDebug("nao achou");
				switch(configObstaculo){
				case 1:{
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}
					}
					break;
				}case 2:{
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.andar(-distAndaProcura*3);
						Navigation.turn(90);
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}
					}
					break;
				}case 3:{
					
					break;
				}case 4:{

					break;
				}case 5:{

					break;
				}case 6:{
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
					Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
					Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.andar(-distAndaProcura*3);
						Navigation.turn(-90);
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}
					}
					break;
				}case 7:{
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
						c1++;
						break;
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

	public static void debugBusca(){
		c1 = 0; c2=0; c3=0;
		float distAndaProcura = 0.15f;
		while(true){
			printDebug("nao achou");
			switch(configObstaculo){
			case 1:{
				switch(c1){
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 4:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 5:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 11:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 17:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}
				}
				break;
			}case 2:{
				switch(c1){
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 4:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 5:{
					/*Navegacao_secundaria.pullSegmento(local);
				Navegacao_secundaria.pullSegmento(local);
				Navegacao_secundaria.pullSegmento(local);*/
					Navegacao_secundaria.andarPilha(0, -distAndaProcura*3, Const.OBSTACULO);
					Navigation.turn(90);
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 11:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 17:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 19:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 20:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 21:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 22:{
					Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}
				}
				break;
			}case 3:{
				
				break;
			}case 4:{

				break;
			}case 5:{

				break;
			}case 6:{
				switch(c1){
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 4:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 5:{
					/*Navegacao_secundaria.pullSegmento(local);
				Navegacao_secundaria.pullSegmento(local);
				Navegacao_secundaria.pullSegmento(local);*/
					Navegacao_secundaria.andarPilha(0, -distAndaProcura*3, Const.OBSTACULO);
					Navigation.turn(-90);
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 11:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 17:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 19:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 20:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 21:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 22:{
					Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}
				}
				break;
			}case 7:{
				switch(c1){
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 4:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 5:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 11:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 17:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 19:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}case 20:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO);
					c1++;
					break;
				}
				}
				break;
			}
			}
		}
	}
}

