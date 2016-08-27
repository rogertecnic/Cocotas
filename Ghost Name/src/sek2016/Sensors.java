package sek2016;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Sensors {

	private static EV3GyroSensor gyro;
	private static EV3UltrasonicSensor ultrasonic;
	private static EV3ColorSensor dollColor;
	private static EV3ColorSensor floorColor;
	static float[] angleSample;
	static float[] veloAngleSample;
	private static final float DIST_MIN = 0.07320f;
	private static final float DIST_MAX = 0.0953f;
	
	public static void init(boolean DC,boolean US, boolean FC, boolean GR) {
		if(GR == true){
			gyro = new EV3GyroSensor(SensorPort.S2);
			angleSample = new float[1];
			veloAngleSample = new float [2];
		}
		
		if(US == true){
			ultrasonic = new EV3UltrasonicSensor(SensorPort.S3);
		}
		
		if(FC == true){
			floorColor = new EV3ColorSensor(SensorPort.S4);
		}
		
		if(DC == true){
			dollColor = new EV3ColorSensor(SensorPort.S1);
		}
	}
	
	
	
	
	
	public static boolean verificaObstaculo(){
		SampleProvider sampleSensor = ultrasonic.getDistanceMode();
		float[] valor = new float[ sampleSensor.sampleSize()];
		sampleSensor.fetchSample(valor, 0);
		//System.out.println(valor[0]);
		if((valor[0] >= DIST_MIN) && (valor[0] <= DIST_MAX))
			return true;
		else 
			return false;
		
	}
	
	/**
	 * 
	 * @return array com a velocidade angular em graus/s e o segundo é o angulo
	 */
	public static float[] getAllGyro() {
		gyro.getAngleAndRateMode().fetchSample(veloAngleSample, 0);
		return veloAngleSample;
	}
	
	/**
	 * 
	 * @return array float com o angulo em graus
	 */
	public static float getAngle() {
		gyro.getAngleMode().fetchSample(angleSample, 0);
		return angleSample[0];
	}
	
	public static void resetAngle(){
		gyro.reset();
	}
	
	public static void close(){
		gyro.close();
		ultrasonic.close();
		dollColor.close();
		floorColor.close();
	}
}
