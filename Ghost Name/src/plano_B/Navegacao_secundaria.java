package plano_B;

import java.util.Stack;

import lejos.utility.Delay;
import sek2016.*;

public class Navegacao_secundaria {
	// =================VARIAVEIS DE PROCESSO==================
	public static int bonecosErrados = 0;
	// ======================PILHAS DE SEGMENTOS=========================
	/**
	 * pilha de segmentos que o robo vai seguir no modulo central voltando ate
	 * trocar de modulo
	 */
	public static Stack<Segmento> segmentosCentral ;

	/**
	 * pilha de segmentos que o robo vai seguir no modulocaverna voltando ate
	 * trocar de modulo
	 */
	public static Stack<Segmento> segmentosCaverna ;
	/**
	 * pilha de segmentos que o robo vai seguir no modulo parede ate trocar de
	 * modulo
	 */
	public static Stack<Segmento> segmentosObstaculo ;

	// =========================METODOS===========================
	/**
	 * inicia a busca na sala central
	 * @param configArena
	 */
	public static void inicioModuloCentral() {
		andarPilha(0, Const.LADO_MODULO_RESGATE, Const.CENTRAL);
	}

	/**
	 * inicia a busca na sala que tem a cave
	 * @param configArena
	 */
	public static void inicioModuloCaverna(int configArena, int configCave) {
		switch (configArena) {
		case Const.ARENA_A: {
			andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			if (configCave == Const.CAV_DIR) {
				andarPilha(-90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			} else {
				andarPilha(90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			}

			break;
		}
		case Const.ARENA_B: {
			if (configCave == Const.CAV_CIMA) {
				andarPilha(0, Const.LADO_MODULO_CENTRAL + Const.LADO_MODULO_RESGATE, Const.CENTRAL);

			} else {
				andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
				andarPilha(-90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);	
			}
			break;
		}
		case Const.ARENA_C: {
			if (configCave == Const.CAV_CIMA) {
				andarPilha(0, Const.LADO_MODULO_CENTRAL + Const.LADO_MODULO_RESGATE, Const.CENTRAL);
			} else {
				andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
				andarPilha(90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);	
			}
			break;
		}
		}
	}

	/**
	 * inicia a busa na sala que tem obstaculo (parede)
	 * 
	 * @param configArena
	 */
	public static void inicioModuloObstaculo(int configArena, int configCave) {
		switch (configArena) {
		case Const.ARENA_A: { // anda ate metade do mod central
			andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			if (configCave == Const.CAV_DIR) { // anda ate o modulo obstaculo a esquerda
				andarPilha(90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			} else { // anda ate o modulo obstaculo a direita
				andarPilha(-90, (Const.LADO_MODULO_CENTRAL/2)+Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			}

			break;
		}
		case Const.ARENA_B: {
			if (configCave == Const.CAV_CIMA) {// anda ate metade do mod central depois anda ate o modulo obstaculo direita
				andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
				andarPilha(-90, (Const.LADO_MODULO_CENTRAL/2) +Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			} else {
				andarPilha(0, Const.LADO_MODULO_CENTRAL + Const.LADO_MODULO_RESGATE, Const.CENTRAL);
			}
			break;
		}
		case Const.ARENA_C: {
			if (configCave == Const.CAV_CIMA) {
				andarPilha(0, (Const.LADO_MODULO_CENTRAL/2) + Const.LADO_MODULO_RESGATE-Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
				andarPilha(90, (Const.LADO_MODULO_CENTRAL/2) +Const.PROFUNDIDADE_BUNDA_ROBO, Const.CENTRAL);
			} else {
				andarPilha(0, Const.LADO_MODULO_CENTRAL + Const.LADO_MODULO_RESGATE, Const.CENTRAL);
			}
			break;
		}
		}
	}

	/**
	 * o robo gira ate encontrar um boneco
	 * @param graus abertura do giro de busca
	 * @param local local onde o robo esta: ( central, cave ou parede)
	 * 
	 * @return boolean o boneco foi encontrado ou nao
	 */
	public static boolean giroBusca(float graus, int local){
		PID.pidRunning=false; // pausa o pid para o pid nao recalcular a velocidade durante o turn
		while(!PID.PIDparado){ // aguarda o pid realmente parar
		}
		PID.zeraPID(); // apos o pid parado ele eh zerado
		Navigation.setVelocidade(Const.VELO_PROCURA,Const.VELO_PROCURA); // seta a velocidade da curva


		float theta = (graus*Navigation.DISTANCIA_ENTRE_RODAS)/(2*Navigation.RAIO); // angulo que a roda precisa girar para o robo girar os graus passados
		float positioninicialE = Navigation.rodaE.getTachoCount(); // posicao inicial em graus da roda e
		float positioninicialD = Navigation.rodaD.getTachoCount(); // posicao inicial em graus da roda d
		float wRoda = Const.VELO_PROCURA/Navigation.RAIO*(float)(180/Math.PI); // velo angular das rodas em graus/s
		float acc = (Navigation.aceleration)/Navigation.RAIO*(float)(180/Math.PI); // aceleracao das rodas em graus/s^2 
		float t = wRoda/(acc); // tempo que o robo demora a parar depois que ele chama o metodo stop devido a desaceleracao normal do lejos
		float ang_defasado = wRoda*t-(acc/2)*t*t; // robo deve chamar o metodo stop antes do local de parar, esse ang_defasado é essa distancia em graus da roda
		boolean achouBoneco = false;
		float dist = 0f;

		//======virar para a esquerda procurando
		Navigation.rodaD.forward();
		Navigation.rodaE.backward();
		while(Navigation.rodaE.getTachoCount()>(positioninicialE-theta+ang_defasado) && // espera o robo girar as rodas ate a posicao de chamar o metodo stop
				Navigation.rodaD.getTachoCount()<(positioninicialD+theta-ang_defasado)&&
				!achouBoneco){ // no momento certo antes da posical final do giro, sai do while e vai direto para o metodo stop
			dist = Sensors.verificaDistObstaculo();
			if(dist !=0f){
				// ajuste fino
				Delay.msDelay(Const.T_PARAR_APOS_VER_BONECO);
				Navigation.stop();
				graus = (Navigation.rodaD.getTachoCount()-positioninicialD)*Navigation.RAIO/(Navigation.DISTANCIA_ENTRE_RODAS/2);
				achouBoneco = true;
			}
		}
		Navigation.stop();

		//=====virar para a direita procurando
		Navigation.rodaE.forward();
		Navigation.rodaD.backward();
		while(Navigation.rodaD.getTachoCount()>(positioninicialD-theta+ang_defasado) && // mesma ideia do de cima
				Navigation.rodaE.getTachoCount()<(positioninicialE+theta-ang_defasado) &&
				!achouBoneco){ //
			dist = Sensors.verificaDistObstaculo();
			if(dist !=0f){
				// ajuste fino
				Delay.msDelay(Const.T_PARAR_APOS_VER_BONECO);
				Navigation.stop();
				graus = (Navigation.rodaD.getTachoCount()-positioninicialD)*Navigation.RAIO/(Navigation.DISTANCIA_ENTRE_RODAS/2);
				achouBoneco = true;
			}
		}
		Navigation.stop();

		//======se achou vai buscar o boneco, se nao, gira p o angulo inicial
		if(achouBoneco){// vai buscar o boneco
			dist = Sensors.verificaDistObstaculo();
			Navigation.andar(Const.DIST_FRENTE_CAPTURA);
			pushSegmento((int)graus, Const.DIST_FRENTE_CAPTURA, local);
			Navigation.closeGarra();
			return true;
		}else{// volta pro angulo inicial antes de iniciar a busca
			Navigation.setVelocidade(Navigation.VELO_CURVA, Navigation.VELO_CURVA);
			Navigation.turn(graus);
			return false;
		}
	}


	/**
	 *  Metodo que adiciona (push) empurra no stack certo dependendo da onde o robo esta
	 * @param ang ang que ele virou relativo a posicao atual
	 * @param dist distancia que o robo andou
	 * @param local onde o robo esta ( CENTRAL, CAVE OU PAREDE)
	 */
	public static void pushSegmento(int ang, float dist, int local){
		switch(local){
		case Const.CENTRAL: {
			/*Plano_B.printDebug("push pilha central");
			Delay.msDelay(1000);*/
			segmentosCentral.push(new Segmento(ang, dist));
			break;
		}case Const.CAVE:{
			segmentosCaverna.push(new Segmento(ang, dist));
			break;
		}case Const.OBSTACULO:{
			/*Plano_B.printDebug("push pilha obst");
			Delay.msDelay(1000);*/
			segmentosObstaculo.push(new Segmento(ang, dist));
			break;
		}
		}
	}

	/**
	 *  faz pull (tira o primeiro item da stack) e o retorna
	 * @param local define de qual pilha estamos tratando
	 * @return segmento imediatamente anterior a posicao atual do robo
	 */
	public static Segmento pullSegmento( int local){
		switch(local){
		case Const.CENTRAL: {
			/*Plano_B.printDebug("pull pilha central");
			Delay.msDelay(1000);*/
			if(segmentosCentral.size()>0)
				return segmentosCentral.pop();
			else return null;
		}case Const.CAVE:{
			if(segmentosCaverna.size()>0)
				return segmentosCaverna.pop();
			else return null;
		}case Const.OBSTACULO:{
			/*Plano_B.printDebug("pull pilha obst");
			Delay.msDelay(1000);*/
			if(segmentosObstaculo.size()>0)
				return segmentosObstaculo.pop();
			else return null;
		}default: return null;
		}
	}


	/**
	 * Metodo que verifica se o boneco que pegamo eh nosso ou nao
	 * @return true se for nosso e false se nao for nosso
	 */
	public static boolean verificaBoneco(){
		int boneco = Sensors.verificaCorDoll();
		if(boneco == Plano_B.cor_resgate){
			//retornar pela(S) pilha(s) referente ao local que o robo esta
			return true;
		}else{
			// dar um jeito de tirar o boneco do caminho
			return false;
		}
	}

	public static void tirarBonecoErrado(int local){
		voltaNaPilha(local);
		// nesse ponto o robo voltou ao inicio do modulo que esta e
		// agora dependendo do local (modulo) que o robo estiver ele vai
		// executar um esquema diferente p tirar o boneco errado do caminho
		switch(local){
		case Const.CENTRAL:{

			break;
		}
		case Const.CAVE:{

			break;
		}
		case Const.OBSTACULO:{
			/*a ideia aqui eh fazer o robo colocar o boneco no canto que tem
			 o obstaculo (parede) se a parede estiver num dos cantos do lado que tem
			 a entrada do modulo, se a parede estiver em qualquer outro lugar o robo
			 so vai largar o boneco no canto direito e depois no cando esquerdo
			 */
			int grau = 0; // angulo que o robo vai virar para largar o boneco
			switch(Plano_B.configObstaculo){
			case 1:{
				grau = -90;
				break;
			}default :{
				grau = 90;
				break;
			}
			}
			if((Const.LADO_MODULO_CENTRAL/2)-(Const.PROFUNDIDADE_BUNDA_ROBO+Const.DIST_EIXO_GARRA_FECHADA+0.07f)-0.05f*bonecosErrados<= 0.2f){
				grau = -grau;
			}

			Navigation.turn(grau);
			Navigation.andar( (Const.LADO_MODULO_CENTRAL/2)-(Const.PROFUNDIDADE_BUNDA_ROBO+Const.DIST_EIXO_GARRA_FECHADA+0.07f)-0.05f*bonecosErrados );
			Navigation.openGarra();
			bonecosErrados ++;
			Navigation.andar(-((Const.LADO_MODULO_CENTRAL/2)-(Const.PROFUNDIDADE_BUNDA_ROBO+Const.DIST_EIXO_GARRA_FECHADA+0.07f)-0.05f*bonecosErrados));
			Navigation.turn(-grau);
			break;
		}
		}
	}

	/**
	 * Metodo que retorna o caminho da pilha do local especificado onde o robo esta
	 * @param local modulo onde o robo esta: (CENTRAL, CAVE, PAREDE)
	 */
	public static void voltaNaPilha(int local){
		while(true){
			Segmento seg = pullSegmento(local);
			if(seg == null){
				break;
			}else{
				Navigation.andar(-seg.dist);
				Navigation.turn(-seg.ang);
			}
		}
	}

	/**
	 * Metodo que inicializa/zera todas as variaveis do
	 * Navegacao_secundaria
	 */
	public static void initVariaveis(){
		segmentosCentral = new Stack<Segmento>();
		segmentosCaverna = new Stack<Segmento>();
		segmentosObstaculo = new Stack<Segmento>();
	}

	/**
	 * metodo que faz o robo girar e/ou andar
	 * @param graus inteiro positivo (anti-horário)
	 * inteiro negativo (horário)
	 * @param dist ditancia em metros que o robo vai andar
	 * @param local onde o robo esta ( CENTRAL, CAVE OU PAREDE)
	 */
	public static void andarPilha(int graus, float dist, int local){
		Navigation.turn(graus);
		Navigation.andar(dist);
		Navegacao_secundaria.pushSegmento(graus, dist, local);
	}
	
	/**
	 * metodo que anda pra frente buscando, se ele achar ele para
	 * @param graus o robo vira graus antes de andar
	 * @param dist distancia que o robo vai andar
	 * @param local modulo onde o robo esta
	 * @return distancia que ele andou ate parar
	 */
	public static float andarBusca(int graus, float dist, int local){
		Navigation.turn(graus);
		float achou = 0f;
		PID.pidRunning=false; // pausa o pid para reinicia-lo
		while(!PID.PIDparado){ // espera o pid realmente parar
		}
		PID.zeraPID(); // zera o pid
		PID.pidRunning = true; // inicia o pid
		while(!PID.PIDparado){ // espera o pid ter a primeira iteracao para ja ter alterado a velocidade, se nao, o metodo continuaria e o robo andaria antes do pid setar as velocidades pois sao threads diferentes
		}

		float theta =(dist/Const.RAIO)*(float)(180/Math.PI); // graus que a roda deve girar para o robo andar a distancia determinada
		float positionE = Navigation.rodaE.getTachoCount(); // posicao inicial em graus da roda e
		float positionD = Navigation.rodaD.getTachoCount(); // posicao inicial em graus da roda d
		float wRoda = Const.VELO_INI/Const.RAIO*(float)(180/Math.PI); // velocidade angular em graus/s das rodas de modo geral, nao eh a velocidade que o pid regula, essa velocidade seria a velocidade que o pid mantem se o erro fosse 0 e seria igual para as 2 rodas
		float acc = Const.ACELERATION/Const.RAIO*(float)(180/Math.PI); // aceleracao angular  em graus/s^2 das rodas
		float t = wRoda/(acc); // tempo que o robo demora a parar depois que ele chama o metodo stop devido a desaceleracao normal do lejos
		float ang_defasado = wRoda*t-(acc/2)*t*t; // robo deve chamar o metodo stop antes do local de parar, esse ang_defasado é essa distancia em graus da roda

		Delay.msDelay(100); /* tem que ter esse delay, o motivo nao sabemos ao certo o por que, verificamos a 
		 *velocidade do pid nesse instante e ela continua certinha, 
		 *se nao o robo exporadicamente vai girar a roda direita para traz por um curto 
		 *periodo de tempo com velocidade maxima quando o robo for se movimentar
		 */

			Navigation.rodaE.forward();
			Navigation.rodaD.forward();
			while(Navigation.rodaE.getTachoCount()<(positionE+theta-ang_defasado) && 
					Navigation.rodaD.getTachoCount()<(positionD+theta-ang_defasado)){
					if(verificaBonecoNaGarra()){
						achou = (Navigation.rodaE.getTachoCount() - positionE)*((float)Math.PI/180)*Const.RAIO;
						Navegacao_secundaria.pushSegmento(graus, achou, local);
						break;
					}
			}
		Navigation.stop();
		if(achou !=0f)Navegacao_secundaria.pushSegmento(graus, dist, local);
		return achou;
	}
	
	public static boolean verificaBonecoNaGarra(){
		float offset = 0.06f;
		Sensors.ultrasonic.getDistanceMode().fetchSample(Sensors.distSample, 0);
		if ((Sensors.distSample[0] >= 0.02f) && (Sensors.distSample[0] <= offset)){
			Navigation.stop();
			Navigation.closeGarra();

			Sensors.dollColor.getRGBMode().fetchSample(Sensors.rgbSample, 0);
			if (Sensors.rgbSample[0] > Sensors.r1) { // vermelho
				if (Sensors.rgbSample[1] > Sensors.g1 && Sensors.rgbSample[2] > Sensors.b1){// branco
					return true;
				} else return true;
			}else{// pode ser preto ou nenhum boneco
				Navigation.openGarra();
				Delay.msDelay(200);
				Navigation.andar(-offset);
				Sensors.ultrasonic.getDistanceMode().fetchSample(Sensors.distSample, 0);
				if ((Sensors.distSample[0] >= 0.02f) && (Sensors.distSample[0] <= offset)){
					Navigation.andar(offset);
					Navigation.closeGarra();
				return true;
				}else{
					Navigation.andar(offset);
					return false;
				}
			}
		}
		else return false;
	}
}














