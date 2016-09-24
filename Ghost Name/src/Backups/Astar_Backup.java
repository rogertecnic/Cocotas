package Backups;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import sek2016.Posicao;

public class Astar_Backup {

	Celula[][] mapa;
	ArrayList<Celula> listaAberta = new ArrayList<Celula>();
	ArrayList<Celula> listaFechada = new ArrayList<Celula>();

	Astar_Backup(Celula[][] mapa) {
		this.mapa = mapa;
		listaAberta = new ArrayList<Celula>();
		listaFechada = new ArrayList<Celula>();
	}

	/**
	 * Método A* para busca de caminho mais próximo
	 * 
	 * @param inicio
	 *            posição inicial
	 * @param fim
	 *            posição final
	 * @return lista de posições para caminho mais próximo
	 * @throws PathNotFoundException
	 *             caminho não encontrado
	 */
	
	public List<Celula> search(Posicao inicio, Posicao fim) throws Exception {
		// Apagam os valores calculados do mapa e as listas
		apagarMap();
		listaAberta.clear();
		listaFechada.clear();
		// ------------------------------------------------

		listaAberta.add(mapa[inicio.x][inicio.y]);

		while (listaAberta.size() > 0) {
			
			// Pega o menor f(x) para ir para o proximo--------------------------
			int indiceMenor = 0;
			for (int i = 0; i < listaAberta.size(); i++) {
				if (listaAberta.get(i).f < listaAberta.get(indiceMenor).f) {
					indiceMenor = i;
				}
			//--------------------------------------------------------------------
				
			}
			
			Celula noAtual = listaAberta.get(indiceMenor);
			// Fim -- o resultado foi encontrado, retorne o caminho encontrado
			
			if (noAtual == mapa[fim.x][fim.y]) {
				Celula atual = noAtual;
				Stack<Celula> ret = new Stack<Celula>();
				while (atual.parent != null) {
					ret.push(atual);
					atual = atual.parent;
				}
				System.out.println("FIM");
				return reverse(ret);
			}

			// Caso normal -- mude o CurrentNode da lista aberta para a fechada
			// para
			// cada um dos vizinhos
			listaAberta.remove(noAtual);
			listaFechada.add(noAtual);
			List<Celula> vizinhos = vizinhos(noAtual);

			for (int i = 0; i < vizinhos.size(); i++) {
				Celula vizinho = vizinhos.get(i);
				if (vizinho.isOccupied() || listaFechada.contains(vizinho)) {
					// não é um nó válido, pule para o proximo vizinho
					continue;
				}

				// g é a menor distancia do começo até o nó atual,
				// precisamos checar se o caminho que chegamos até esse vizinho
				// é o menor
				// que vimos até agora
				int pontoG = noAtual.g + 1; // 1 é a distancia do nó até o seu
											// vizinho
				boolean pontoGmelhor = false;

				if (!listaAberta.contains(vizinho)) {
					// Essa é a primeira vez que chegamos a esse nó, ele deve
					// ser o melhor
					// Também devemos somar com h (heuristica) já que não
					// fizemos isso ainda
					pontoGmelhor = true;
					vizinho.h = heuristica(vizinho.getPosicao(), fim);
					listaAberta.add(vizinho);
				} else if (pontoG < vizinho.g) {
					// Já vimos esse nó, mas da ultima vez tinha um pior g
					pontoGmelhor = true;
				}

				if (pontoGmelhor) {
					// Encontrou o caminho até agora para esse nó. Guarda a
					// informação
					// de como chegou aqui e o quão bom é
					vizinho.parent = noAtual;
					vizinho.g = pontoG;
					vizinho.f = vizinho.g + vizinho.h;
				}
			}
		}
		throw new Exception();
	}

	public List<Celula> searchReversePath(Posicao inicio, Posicao fim) throws Exception {

		// Apagam os valores calculados do mapa e as listas
		apagarMap();
		listaAberta.clear();
		listaFechada.clear();
		// ------------------------------------------------

		listaAberta.add(mapa[inicio.x][inicio.y]);
		while (listaAberta.size() > 0) {

			// Pega o menor f(x) para ir para o proximo
			int indiceMenor = 0;
			for (int i = 0; i < listaAberta.size(); i++) {
				if (listaAberta.get(i).f < listaAberta.get(indiceMenor).f) {
					indiceMenor = i;
				}
			}
			Celula noAtual = listaAberta.get(indiceMenor);

			// Fim -- o resultado foi encontrado, retorne o caminho encontrado
			if (noAtual == mapa[fim.x][fim.y]) {
				Celula atual = noAtual;
				Stack<Celula> ret = new Stack<Celula>();
				while (atual.parent != null) {

					ret.push(atual);
					atual = atual.parent;
				}
				System.out.println("FIM");
				return reverse(ret);
			}

			// Caso normal -- mude o CurrentNode da lista aberta para a fechada
			// para
			// cada um dos vizinhos
			listaAberta.remove(noAtual);
			listaFechada.add(noAtual);
			List<Celula> vizinhos = vizinhos(noAtual);

			for (int i = 0; i < vizinhos.size(); i++) {
				Celula vizinho = vizinhos.get(i);
				if (vizinho.isOccupied() || listaFechada.contains(vizinho) || !vizinho.isChecked()) {
					// não é um nó válido, pule para o proximo vizinho
					continue;
				}

				// g é a menor distancia do começo até o nó atual,
				// precisamos checar se o caminho que chegamos até esse vizinho
				// é o menor
				// que vimos até agora
				int pontoG = noAtual.g + 1; // 1 é a distancia do nó até o seu
											// vizinho
				boolean pontoGmelhor = false;

				if (!listaAberta.contains(vizinho)) {
					// Essa é a primeira vez que chegamos a esse nó, ele deve
					// ser o melhor
					// Também devemos somar com h (heuristica) já que não
					// fizemos isso ainda
					pontoGmelhor = true;
					vizinho.h = heuristica(vizinho.getPosicao(), fim);
					listaAberta.add(vizinho);
				} else if (pontoG < vizinho.g) {
					// Já vimos esse nó, mas da ultima vez tinha um pior g
					pontoGmelhor = true;
				}

				if (pontoGmelhor) {
					// Encontrou o caminho até agora para esse nó. Guarda a
					// informação
					// de como chegou aqui e o quão bom é
					vizinho.parent = noAtual;
					vizinho.g = pontoG;
					vizinho.f = vizinho.g + vizinho.h;
				}
			}
		}
		throw new Exception();
	}

	/**
	 * @param stack
	 *            pilha
	 * @return retorna a pilha em ordem reversa na forma de lista
	 */
	private List<Celula> reverse(Stack<Celula> stack) {
		ArrayList<Celula> listaReversa = new ArrayList<Celula>();
		while (!stack.empty()) {
			listaReversa.add(stack.pop());
		}
		return listaReversa;
	}

	/**
	 * 
	 * @param node
	 *            celula
	 * @return retorna uma lista de células vizinhas a celula passada como
	 *         parametro
	 */
	private List<Celula> vizinhos(Celula no) {
		ArrayList<Celula> ret = new ArrayList<Celula>();
		Posicao posicao = no.getPosicao();
		if (posicao.x > 0) {
			ret.add(mapa[posicao.x - 1][posicao.y]);
		}
		if (posicao.x < mapa.length - 1) {
			ret.add(mapa[posicao.x + 1][posicao.y]);
		}
		if (posicao.y > 0) {
			ret.add(mapa[posicao.x][posicao.y - 1]);

		}
		if (posicao.y < mapa.length - 1) {
			ret.add(mapa[posicao.x][posicao.y + 1]);
		}
		return ret;
	}

	/**
	 * @param p0
	 * @param p1
	 * @return retorna a distancia(Manhattan) do ponto p0 ao ponto p1
	 */
	private int heuristica(Posicao p0, Posicao p1) {
		int d1 = Math.abs(p1.x - p0.x);
		int d2 = Math.abs(p1.y - p0.y);
		return d1 + d2;
	}

	/**
	 * apaga os valores calculados do mapa
	 */
	private void apagarMap() {
		for (int i = 0; i < mapa.length; i++) {
			for (int j = 0; j < mapa.length; j++) {
				mapa[i][j].apagar();
			}
		}
	}

}