package sek2016;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Sensors {

	// ===================declaracao dos sensores ============================
	private static EV3GyroSensor gyro; // nem preciso falar
	private static EV3UltrasonicSensor ultrasonic; // nem esse
	private static EV3ColorSensor dollColor; // verificar cor do boneco no modo
												// RGB
	private static EV3ColorSensor floorColor; // verificar a cor do chao
	private static float[] angleSample, // vetor contendo um elemento do gyro,
										// angulo em graus
			distSample, // vetor contendo um elemento do ultrassom, a dist em
						// metros
			rgbSample; // vetor contendo 3 elementos de nivel de intensidade RGB
						// de 0 a 1 do doll color

	// =====================constantes de processo=======================
	private static final float DIST_MIN = 0.07320f, // distancia minima do
													// boneco
			DIST_MAX = 0.0953f, // distancia maxima do boneco
			/*
			 * REFERENTES A VARIAVEL DO SENSOR RGB
			 * r: red
			 * g: green
			 * b: blue
			 * 
			 * REFERENTES A COR DO BONECO
			 * v: vermelho
			 * b: branco
			 * p: preto
			 */
			vr1 = 0.08f, vr2 = 0.2f, vg1 = 0.05f, vg2 = 0.15f, vb1 = 0.015f, vb2 = 0.02f, // Vermelho
			br1 = 0.01f, br2 = 0.08f, bg1 = 0.015f, bg2 = 0.03f, bb1 = 0.015f, bb2 = 0.02f, // Branco
			pr1 = 0f, pr2 = 0.01f, pg1 = 0f, pg2 = 0.05f, pb1 = 0f, pb2 = 0.015f; // Preto

	// ======================metodos======================
	/**
	 * Metodo que instancia todos os sensores.
	 * 
	 * @param DC
	 *            true ou false
	 * @param US
	 *            true ou false
	 * @param FC
	 *            true ou false
	 * @param GR
	 *            true ou false
	 */
	public static void init(boolean DC, boolean US, boolean FC, boolean GR) {
		if (GR == true) {
			gyro = new EV3GyroSensor(SensorPort.S2);
			angleSample = new float[1];
		}
		if (US == true) {
			ultrasonic = new EV3UltrasonicSensor(SensorPort.S3);
			distSample = new float[1];
		}
		if (FC == true) {
			floorColor = new EV3ColorSensor(SensorPort.S4);
		}
		if (DC == true) {
			dollColor = new EV3ColorSensor(SensorPort.S1);
			rgbSample = new float[3];
		}
	}

	/**
	 * Metodo que verifica se tem algo na frente do ultrassom entre DIST_MIN e
	 * DIST_MAX;
	 * 
	 * @return true se tiver;<br>
	 *         false se nao tiver;
	 */
	public static boolean verificaObstaculo() {
		ultrasonic.getDistanceMode().fetchSample(distSample, 0);
		if ((distSample[0] >= DIST_MIN) && (distSample[0] <= DIST_MAX))
			return true;
		else
			return false;
	}

	/**
	 * Metodo que verifica somente a posicao do gyro;
	 * 
	 * @return valor float com a diferenca em graus da posicao da ultima zerada
	 *         para a posicao atual
	 */
	public static float getAngle() {
		gyro.getAngleMode().fetchSample(angleSample, 0);
		return angleSample[0];
	}

	/**
	 * Modifica a posicao angular do gyro para 0 graus para recalibrar;<br>
	 * <h1>O ROBO DEVE ESTAR PARADO;
	 */
	public static void resetAngle() {
		if (Navigation.rodaD.isMoving() || Navigation.rodaE.isMoving()) {
			Navigation.stop();
		}
		gyro.reset();
	}

	/**
	 * metodo para ver a cor do boneco
	 * 
	 * @return 3 branco 4 preto 5 vermelho
	 * 
	 */
	public static int VerificaCorDoll() {
		dollColor.getRGBMode().fetchSample(rgbSample, 0);
		if (rgbSample[0] < vr2 && rgbSample[0] > vr1) { // verificação vermelho
			if (rgbSample[1] < vg2 && rgbSample[1] > vg1) {
				if (rgbSample[2] < vb2 && rgbSample[2] > vb1) {
					System.out.println("vermelho");
					return 5;
					
				}
			}

		}

		if (rgbSample[0] < br2 && rgbSample[0] > br1) { // verificação branco
			if (rgbSample[1] < bg2 && rgbSample[1] > bg1) {
				if (rgbSample[2] < bb2 && rgbSample[2] > bb1) {
					System.out.println("branco");
					return 3;
				}
			}

		}
		
		if (rgbSample[0] < pr2 && rgbSample[0] > pr1) { // verificação preto
			if (rgbSample[1] < pg2 && rgbSample[1] > pg1) {
				if (rgbSample[2] < pb2 && rgbSample[2] > pb1) {
					System.out.println("preto");
					return 4;
				}
			}

		}
			System.out.println("erro na cor");
		return 0;
	}
}
