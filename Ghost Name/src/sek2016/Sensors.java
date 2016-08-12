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

	public static void init(boolean DC,boolean US, boolean FC, boolean GR) {
		if(DC == true){
			dollColor = new EV3ColorSensor(SensorPort.S1);
		}
		
		if(US == true){
			ultrasonic = new EV3UltrasonicSensor(SensorPort.S2);
		}
		
		if(GR == true){
			gyro = new EV3GyroSensor(SensorPort.S3);
		}
		
		if(FC == true){
			floorColor = new EV3ColorSensor(SensorPort.S4);
		}
		

	}
	private static final float DIST_MIN = 0.01f;
	private static final float DIST_MAX = 0.04f;
	
	
	
	
	public static boolean verificaObstaculo(){
		SampleProvider sampleSensor = ultrasonic.getDistanceMode();
		float[] valor = new float[ sampleSensor.sampleSize()];
		sampleSensor.fetchSample(valor, 0);
		if((valor[0] >= DIST_MIN) && (valor[0] <= DIST_MAX))
			return true;
		else 
			return false;
		
	}

	public static float getAngle() {
		SampleProvider angle = gyro.getAngleMode();
		float[] angleSample = new float[angle.sampleSize()];
		angle.fetchSample(angleSample, 0);
		return angleSample[0];
	}
	
	public static void resetAngle(){
		gyro.reset();
	}
}
