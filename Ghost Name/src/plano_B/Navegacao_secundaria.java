package plano_B;

import java.util.Stack;

import lejos.utility.Delay;
import sek2016.*;

public class Navegacao_secundaria {
	// ========================CONSTANTES DESCRICAO==========================
	public static final int ARENA_A = 1, ARENA_B = 2, ARENA_C = 3, CAV_DIR = 1, CAV_ESQ = 2, CAV_CIMA = 3,
			CENTRAL = 0, CAVE = 1, PAREDE = 2, BRANCO = 3, VERMELHO = 4, PRETO = 5;
	public static final float VELO_PROCURA = Navigation.VELO_CURVA*3/4;

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
	public static Stack<Segmento> segmentosParede ;

	// =========================METODOS===========================
	/**
	 * se nao tiver boneco no centro, vai pra sala que nao tem cave, tem parede
	 * 
	 * @param configArena
	 */
	public static void inicioSemBoneco(int configArena, int configCave) {
		switch (configArena) {
		case ARENA_A: {
			Navigation.andar(0.925f + 0.33f);
			segmentosParede.push(new Segmento(0, 0.925f + 0.33f));
			if (configCave == CAV_DIR) {
				Navigation.turn(90);
				Navigation.andar(0.925f);
				segmentosParede.push(new Segmento(90, 0.925f));
			} else {
				Navigation.turn(-90);
				Navigation.andar(0.925f);
				segmentosParede.push(new Segmento(-90, 0.925f));
			}

			break;
		}
		case ARENA_B: {
			if (configCave == CAV_CIMA) {
				Navigation.andar(0.925f + 0.33f);
				Navigation.turn(-90);
				Navigation.andar(0.925f);
				segmentosParede.push(new Segmento(0, 0.925f + 0.33f));
				segmentosParede.push(new Segmento(-90, 0.925f));
			} else {
				Navigation.andar(0.33f + 1.85f);
				segmentosParede.push(new Segmento(0, 0.33f + 1.85f));
			}
			break;
		}
		case ARENA_C: {
			if (configCave == CAV_CIMA) {
				Navigation.andar(0.925f + 0.33f);
				Navigation.turn(90);
				Navigation.andar(0.925f);
				segmentosParede.push(new Segmento(0, 0.925f + 0.33f));
				segmentosParede.push(new Segmento(-90, 0.925f));
			} else {
				Navigation.andar(0.33f + 1.85f);
				segmentosParede.push(new Segmento(0, 0.33f + 1.85f));
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
		Navigation.setVelocidade(VELO_PROCURA,VELO_PROCURA); // seta a velocidade da curva


		float theta = (graus*Navigation.DISTANCIA_ENTRE_RODAS)/(2*Navigation.RAIO); // angulo que a roda precisa girar para o robo girar os graus passados
		float positioninicialE = Navigation.rodaE.getTachoCount(); // posicao inicial em graus da roda e
		float positioninicialD = Navigation.rodaD.getTachoCount(); // posicao inicial em graus da roda d
		float wRoda = VELO_PROCURA/Navigation.RAIO*(float)(180/Math.PI); // velo angular das rodas em graus/s
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
				Delay.msDelay(100);
				Navigation.stop();
				dist = Sensors.verificaDistObstaculo();
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
				Delay.msDelay(100);
				Navigation.stop();
				dist = Sensors.verificaDistObstaculo();
				graus = (Navigation.rodaD.getTachoCount()-positioninicialD)*Navigation.RAIO/(Navigation.DISTANCIA_ENTRE_RODAS/2);
				achouBoneco = true;
			}
		}
		Navigation.stop();

		//======se achou vai buscar o boneco, se nao, gira p o angulo inicial
		if(achouBoneco){
			dist = Sensors.verificaDistObstaculo();
			Navigation.andar(dist);
			pushSegmento((int)graus, dist, local);
			Navigation.closeGarra();
			return true;
		}else{
			Navigation.setVelocidade(Navigation.VELO_CURVA, Navigation.VELO_CURVA);
			Navigation.rodaD.forward();
			Navigation.rodaE.backward();
			while(Navigation.rodaE.getTachoCount()>(positioninicialE+ang_defasado) && // espera o robo girar as rodas ate a posicao de chamar o metodo stop
					Navigation.rodaD.getTachoCount()<(positioninicialD-ang_defasado)){ 
			}
			Navigation.stop();
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
		case CENTRAL: {
			segmentosCentral.push(new Segmento(ang, dist));
			break;
		}case CAVE:{
			segmentosCaverna.push(new Segmento(ang, dist));
			break;
		}case PAREDE:{
			segmentosParede.push(new Segmento(ang, dist));
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
		case CENTRAL: {
			if(segmentosCentral.size()>0)
				return segmentosCentral.pop();
		}case CAVE:{
			if(segmentosCaverna.size()>0)
				return segmentosCaverna.pop();
		}case PAREDE:{
			if(segmentosParede.size()>0)
				return segmentosParede.pop();
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
			//retornar pela pilha
			return true;
		}else{
			// dar um jeito de tirar o boneco do caminho
			return false;
		}
	}

	public static void tirarBonecoErrado(int local){
		voltaNaPilha(local);
		// nesse ponto o robo voltou ao inicio da arena e salvou o caminho
		// agora dependendo do local (modulo) que o robo estiver ele vai
		// executar um esquema diferente p tirar o boneco errado do caminho
		if(local == PAREDE){
			int grau = 0;
			switch(Plano_B.configParede){
			case 1:{
				grau = -90;
				break;
			}case 7:{
				grau = 90;
				break;
			}
			}
			if(0.8f-0.15f*bonecosErrados<= 0.2f){
				grau = -grau;
			}
			Navigation.turn(grau);
			Navigation.andar( 0.8f-0.15f*bonecosErrados );
			Navigation.openGarra();
			bonecosErrados ++;
			Navigation.andar(-(0.8f-0.15f*bonecosErrados));
			Navigation.turn(-grau);
		}else if(local == CAVE){
			int grau = 90;
			if(0.8f-0.15f*bonecosErrados<= 0.2f){
				grau = -grau;
			}
			Navigation.turn(grau);
			Navigation.andar( 0.8f-0.15f*bonecosErrados );
			Navigation.openGarra();
			bonecosErrados ++;
			Navigation.andar(-(0.8f-0.15f*bonecosErrados));
			Navigation.turn(-grau);
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

	public static void initVariaveis(){
		segmentosCentral = new Stack<Segmento>();
		segmentosCaverna = new Stack<Segmento>();
		segmentosParede = new Stack<Segmento>();
	}
}














