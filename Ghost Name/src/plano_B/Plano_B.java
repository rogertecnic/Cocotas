package plano_B;

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
	arenaSek = false; // true se a arena que vamos rodar for a nossa, false se nao



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
	 *Metodo que procura os bonecos no modulo central
	 *  busca em diagonais, comeca contando da diagonal direita 
	 *  baixo, sendo essa igual a 1 e vai contando no
	 *  anti-horario ate a diagonal esquerda baixo que eh a 4
	 *  se preocupar com a variavel que faz o controle de qual
	 *  diagonal ele ja pesquisou
	 */
	private static void sequenciaBuscaCentral(){
		Navegacao_secundaria.orientacao = Const.NORTE;
		Navegacao_secundaria.x = 0;
		Navegacao_secundaria.y = Const.PROFUNDIDADE_BUNDA_ROBO;
		c1 = 0;
		float distAndaProcura = 0.15f;
		boolean achou = false,
				resgatar = false;
		while(!resgatar ){
			achou = Navegacao_secundaria.giroBuscaResgata(55, Const.CENTRAL); // testar se esta virando e indo ate o boneco e fechando a garra
			if(achou){
				printDebug("mais pontos");
				resgatar = true;
				Delay.msDelay(666666);
			}else{
				printDebug("nao achou");
				switch(c1){ // busca pela direita da caverna depois volta e busca pela esquerda
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // (0,1)
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // (0,2)
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // (0,3)
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // (0,4)
					c1++;
					break;
				}case 4:{ // ATE AQUI ELE DEVE EXECUTAR EM TODAS AS 4 DIAGONAIS
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // (0,5)
					switch(MainMenuClass.diagonalModuloCentral){
					case 0:{
						c1++;
						break;
					}case 1:{
						c1=11; // case que ele executa a diagonal 2
						break;
					}case 2:{
						c1=17;
						break;
					}case 3:{
						c1+=23;
						break;
					}
					}
					break;
				}case 5:{
					Navegacao_secundaria.andarPilha(-(90+36), distAndaProcura, Const.CENTRAL, 0); // Diag1 1 SUDESTE
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag1 2
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag1 3
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag1 4
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag1 5
					MainMenuClass.diagonalModuloCentral = 1; // ele ja cobriu a diagonal 1
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL); // desempilha ate (0,5)
					Navigation.turn(90+36); // NORTE, +36 parece q ta bom, nao eh 45 devido ao erro do pid, o robo para torto no centro
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(90); // OESTE
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(-45);// NOROESTE
					Navigation.andar(5*distAndaProcura);
					Navigation.turn(-45); //NORTE
					Navegacao_secundaria.orientacao = Const.NORTE;
					Navegacao_secundaria.x = 0;
					Navegacao_secundaria.y = Const.LADO_MODULO_CENTRAL/2-0.1f; // -0.1f eh necessario para ele voltar direito
					c1++;
					break;
				}case 11:{//DIAGONAL 2, neste momento: orientacao NORTE posicao (0,5)
					Navegacao_secundaria.andarPilha(-45, distAndaProcura, Const.CENTRAL,0); // Diag2 1 NORDESTE
					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag2 2
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag2 3
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag2 4
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag2 5
					MainMenuClass.diagonalModuloCentral = 2; // aqui ele terminou de procurar na diagonal 2
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL); // desempilha ate (0,5)
					Navigation.turn(90+45); // OESTE, , verificar esse +45
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(90); // SUL
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(-45); // SUDOESTE
					Navigation.andar(5*distAndaProcura);
					Navigation.turn(-(45+90)); // NORTE
					Navegacao_secundaria.orientacao = Const.NORTE;
					Navegacao_secundaria.x = 0;
					Navegacao_secundaria.y = Const.LADO_MODULO_CENTRAL/2-0.1f; // -0.1f eh necessario para ele voltar direito
					c1++;
					break;
				}case 17:{//DIAGONAL 3, neste momento: orientacao NORTE posicao (0,5)
					Navegacao_secundaria.andarPilha(45, distAndaProcura, Const.CENTRAL,0); // Diag3 1 NOROESTE
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag3 2
					c1++;
					break;
				}case 19:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag3 3
					c1++;
					break;
				}case 20:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag3 4
					c1++;
					break;
				}
				case 21:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag3 5
					MainMenuClass.diagonalModuloCentral = 3; // aqui ele terminou de procurar na diagonal 3
					c1++;
					break;
				}case 22:{
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL);
					Navegacao_secundaria.pullSegmento(Const.CENTRAL); // desempilha ate (0,5)
					Navigation.turn(-(90+45)); // LESTE, , verificar esse +45
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(-90); // SUL
					Navigation.andar(-0.4f); // valores que precisam ser testados inssessantemente, parece q ta bom
					Navigation.andar(0.3f);
					Navigation.turn(45); // SUDESTE
					Navigation.andar(5*distAndaProcura);
					Navigation.turn((45+90)); // NORTE
					Navegacao_secundaria.orientacao = Const.NORTE;
					Navegacao_secundaria.x = 0;
					Navegacao_secundaria.y = Const.LADO_MODULO_CENTRAL/2-0.1f; // -0.1f eh necessario para ele voltar direito
					c1++;
					break;
				}case 23:{//DIAGONAL 4, neste momento: orientacao NORTE posicao (0,5)
					Navegacao_secundaria.andarPilha(90+45, distAndaProcura, Const.CENTRAL,0); // Diag4 1 SUDOESTE
					c1++;
					break;
				}case 24:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag4 2
					c1++;
					break;
				}case 25:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag4 3
					c1++;
					break;
				}case 26:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag4 4
					c1++;
					break;
				}
				case 27:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CENTRAL,0); // Diag4 5
					MainMenuClass.diagonalModuloCentral = 0; // teoricamente ele terminou o mod central
					c1++;
					break;
				}
				}
			}
		}


	}

	/**
	 *  Metodo que procura os bonecos no modulo da caverna
	 *  deve se preocupar com a caverna, ela varia de 50 a 70 cm de diametro
	 */
	private static void sequenciaBuscaCave(){
		Navegacao_secundaria.orientacao = Const.NORTE;
		Navegacao_secundaria.x = 0;
		Navegacao_secundaria.y = Const.PROFUNDIDADE_BUNDA_ROBO;
		c1 = 0;
		float distAndaProcura = 0.15f;
		boolean achou = false,
				resgatar = false;
		while(!resgatar ){
			achou = Navegacao_secundaria.giroBuscaResgata(55, Const.CAVE); // testar se esta virando e indo ate o boneco e fechando a garra
			if(achou){
				printDebug("mais pontos");
				resgatar = true;
				Delay.msDelay(666666);
			}else{
				printDebug("nao achou");
				switch(c1){ // busca pela direita da caverna depois volta e busca pela esquerda
				case 0:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (0,1)
					c1++;
					break;
				}case 1:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (0,2)
					c1++;
					break;
				}case 2:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.CAVE,0); // (1,2)
					c1++;
					break;
				}case 3:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (2,2)
					c1++;
					break;
				}case 4:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,2)
					c1++;
					break;
				}case 5:{
					Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.CAVE,1); // (3,3)
					c1++;
					break;
				}case 6:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,4)
					c1++;
					break;
				}case 7:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,5)
					c1++;
					break;
				}case 8:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,6)
					c1++;
					break;
				}case 9:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,7)
					c1++;
					break;
				}case 10:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (3,8)
					c1++;
					break;
				}case 11:{ // alinhar e voltar para o inicio, desfazer a pilha ate esse ponto
					Navigation.turn(90); // OESTE
					Navigation.andar(-(Const.LADO_MODULO_CENTRAL/2-Navegacao_secundaria.x+0.1f));
					Navigation.andar(Const.LADO_MODULO_CENTRAL/2-Navegacao_secundaria.x-Const.PROFUNDIDADE_BUNDA_ROBO);
					Navigation.turn(90); // NORTE
					Navigation.andar(-(Const.LADO_MODULO_CENTRAL-Navegacao_secundaria.y+0.1f));
					Navigation.andar(Const.LADO_MODULO_CENTRAL-Navegacao_secundaria.y
							-Const.PROFUNDIDADE_BUNDA_ROBO+distAndaProcura*6); // (3,2)
					Navigation.turn(-90); // OESTE
					Navigation.andar(distAndaProcura*3); // (0,2)
					Navegacao_secundaria.x = 0;
					Navegacao_secundaria.y = 0.3f;
					Navegacao_secundaria.orientacao = Const.OESTE;
					for(int i=1; i<=9;i++){
						Navegacao_secundaria.pullSegmento(Const.CAVE); // volta a pilha para a posicao atual
					}
					Navegacao_secundaria.pushSegmento(90, 0, Const.CAVE, 0);// vira o robo para a orientacao atual OESTE

					c1++;
					break;
				}case 12:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-1,2)
					c1++;
					break;
				}case 13:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-2,2)
					c1++;
					break;
				}case 14:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,2)
					c1++;
					break;
				}case 15:{
					Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.CAVE,4); // (-3,3)
					c1++;
					break;
				}case 16:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,4)
					c1++;
					break;
				}case 17:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,5)
					c1++;
					break;
				}case 18:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,6)
					c1++;
					break;
				}case 19:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,7)
					c1++;
					break;
				}case 20:{
					Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.CAVE,0); // (-3,8)
					c1++;
					break;
				}
				}
			}
		}
	}

	/**
	 * Metodo que procura os bonecos na sala que tem parede
	 * deve se preocupar com a parede tambem
	 */
	private static void sequenciaBuscaObstaculo() {
		Navegacao_secundaria.orientacao = Const.NORTE;
		Navegacao_secundaria.x = 0;
		Navegacao_secundaria.y = Const.PROFUNDIDADE_BUNDA_ROBO;
		c1 = 0;
		float distAndaProcura = 0.15f;
		boolean achou = false,
				resgatar = false;
		while(!resgatar ){
			achou = Navegacao_secundaria.giroBuscaResgata(55, Const.OBSTACULO); // testar se esta virando e indo ate o boneco e fechando a garra
			if(achou){
				printDebug("mais pontos");
				resgatar = true;
				Delay.msDelay(999999);
			}else{
				printDebug("nao achou");
				switch(configObstaculo){
				case 1:{// ======================PAREDE POSICAO 1
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,1)
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(-90, 0, Const.OBSTACULO,0); // (0,1)
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.turn(90);
						Navegacao_secundaria.orientacao = Const.NORTE;
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0); // (-1,1)
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-2,1)
						c1++;
						break;
					}
					case 4:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (-2,2)
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0); // (-3,2)
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,4); // (-3,3)
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,4)
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,5)
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,6)
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,7)
						c1++;
						break;
					}
					case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,8)
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,3); // (-2,8)
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-1,8)
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,8)
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (1,8)
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (2,8)
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,8)
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (3,7)
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,6)
						c1++;
						break;
					}
					}
					break;
				}case 2:{// ======================PAREDE POSICAO 2
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,1)
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (0,2)
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0);// (1,2)
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (2,2)
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (3,2)
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.andar(-distAndaProcura*3); // (0,2)
						Navegacao_secundaria.x = 0;
						Navigation.turn(90);
						Navegacao_secundaria.orientacao = Const.NORTE;
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);// (-1,2)
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-2,2)
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-3,2)
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,4); // (-3,3)
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,4)
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);  // (-4,5)
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);  // (-3,6)
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);  // (-3,7)
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,8)
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(-90, 0, Const.OBSTACULO,3); // (-2,8)  ALINHARRRRRR
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-1,8)
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,8)
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (1,8)
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (2,8)
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,8)
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(-90, 0, Const.OBSTACULO,0); // (3,7)
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(2*360, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}
					}
					break;
				}case 3:{// ======================PAREDE POSICAO 3
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,1)
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,2)
						c1++;
						break;
					}
					case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (1,2)
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (2,2)
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0); // (2,3)
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (3,3)
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,1); // (3,4) ALINHARRRR
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,5)
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.andar(-3*distAndaProcura); // (3,2) NORTE
						Navigation.turn(90); // (3,2) OESTE
						Navigation.andar(3*distAndaProcura); // ( 0,2) OESTE
						Navegacao_secundaria.orientacao = Const.OESTE;
						Navegacao_secundaria.x = 0;
						Navegacao_secundaria.y = 2*distAndaProcura+Const.PROFUNDIDADE_BUNDA_ROBO;
						Navegacao_secundaria.pushSegmento(90, 0, Const.OBSTACULO, 0);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0,distAndaProcura, Const.OBSTACULO,0); // (-1,2)
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-2,2)
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,4);// (-2,3) ALINHARRR
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-2,4) 
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-2,5)
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-2,6)
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-2,7)
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,3);// (-2,8)
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (-1,8)
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,8)
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (1,8)
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (2,8)
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(0, 0, Const.OBSTACULO,0); // (3,8)
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}
					}
					break;
				}case 4:{// ======================PAREDE POSICAO 4
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,1)
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (0,2) NORTE
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0); // (1,2)
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (2,2)
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,2)
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,1); // (3,3) ALINHARR
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,4)
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,5)
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,6)
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (3,7)
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navegacao_secundaria.pullSegmento(Const.OBSTACULO);
						Navigation.andar(-4*distAndaProcura); // (3,3)
						Navigation.turn(90); // OESTE
						Navigation.andar(3*distAndaProcura); // (0,3)
						Navegacao_secundaria.pushSegmento(0, distAndaProcura, Const.OBSTACULO, 0);
						Navegacao_secundaria.pushSegmento(90, 0, Const.OBSTACULO, 0);
						Navegacao_secundaria.x = 0;
						Navegacao_secundaria.y = 3*distAndaProcura;
						Navegacao_secundaria.orientacao = Const.OESTE;
						Navegacao_secundaria.andarPilha(0,distAndaProcura, Const.OBSTACULO,0); // (-1,3)
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-2,3)
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-3,3)
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,4);// (-3,4)
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-3,5)
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);// (-3,6)
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,7)
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0); // (-3,8)
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(-360, distAndaProcura, Const.OBSTACULO,0); // final
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(-90, 0, Const.OBSTACULO,0);
						c1++;
						break;
					}case 23:{
						Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}
					}
					break;
				}case 5:{// ======================PAREDE POSICAO 5 (inverso da 3)

					break;
				}case 6:{// ======================PAREDE POSICAO 6 (inverso da 2)
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
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
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 21:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 22:{
						Navegacao_secundaria.andarPilha(360, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}
					}
					break;
				}case 7:{// ======================PAREDE POSICAO 7 (inverso da 1)
					switch(c1){
					case 0:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 1:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 2:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 3:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 4:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 5:{
						Navegacao_secundaria.andarPilha(-90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 6:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 7:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 8:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 9:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 10:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 11:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 12:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 13:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 14:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 15:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 16:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 17:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 18:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 19:{
						Navegacao_secundaria.andarPilha(90, distAndaProcura, Const.OBSTACULO,0);
						c1++;
						break;
					}case 20:{
						Navegacao_secundaria.andarPilha(0, distAndaProcura, Const.OBSTACULO,0);
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


}

