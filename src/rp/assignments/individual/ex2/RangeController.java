package rp.assignments.individual.ex2;

import lejos.robotics.RangeFinder;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import rp.config.RangeFinderDescription;
import rp.robotics.DifferentialDriveRobot;
import rp.systems.StoppableRunnable;

public class RangeController implements StoppableRunnable {

	private boolean running;
	
	private DifferentialDriveRobot robot;
	private RangeFinderDescription desc;
	private RangeFinder ranger;
	private float maxDistance;
	
	private DifferentialPilot pilot;
	
	private double minSpeed;
	private double maxSpeed;
	private double currentWallSpeed;
	
	private float minDistance;
	
	private int delay = 10;
	
	
	public RangeController(DifferentialDriveRobot robot, RangeFinderDescription desc, RangeFinder ranger, float maxDistance) {
		this.robot = robot;
		this.desc = desc;
		this.ranger = ranger;
		this.maxDistance = maxDistance;
		
		pilot = robot.getDifferentialPilot();
		
		minSpeed = 0;
		maxSpeed = pilot.getMaxTravelSpeed();
		
		minDistance = 0;
	}
	
	@Override
	public void run() {
		
		double newSpeed = maxSpeed;
		double oldSpeed = maxSpeed;
		running = true;
		
		setSpeed(newSpeed);
		
		pilot.forward();
		
		while (running) {
			if (distance() / maxDistance > 1.8) {
				newSpeed = maxSpeed;
			} else if (maxDistance / distance() > 1.8) {
				newSpeed = 0;
			} else {
				if (newSpeed - oldSpeed > 0) {
					newSpeed -= 0.005;
				} else {
					newSpeed += 0005;
				}
			}

			setSpeed(clamp(minSpeed, maxSpeed, newSpeed));
			
			oldSpeed = newSpeed;
			
			Delay.msDelay(delay);;
		}
	}

	@Override
	public void stop() {
		running = false;
	}
	
	private double clamp(double min, double max, double value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}
	
	private double currentSpeed() {
		return pilot.getTravelSpeed();
	}
	
	private void setSpeed(double speed) {
		pilot.setTravelSpeed(speed);
	}
	
	private float distance() {
		return ranger.getRange();
	}

}
