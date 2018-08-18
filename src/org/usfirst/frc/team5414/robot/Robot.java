/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5414.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	Victor motor0 = new Victor(0);
	Victor motor1 = new Victor(1);
	Victor motor2 = new Victor(2);
	Victor motor3 = new Victor(3);
	TalonSRX shooter = new TalonSRX(3);
	TalonSRX geararm = new TalonSRX(2);
	Joystick JS = new Joystick(0);
	Compressor cr = new Compressor(0);
	Spark gearwheels;
	DigitalInput limitSwitch;
	DoubleSolenoid FL,FR,BL,BR;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		cr.start();
		
		gearwheels = new Spark(4); 
		limitSwitch = new DigitalInput(0);
		
		FL = new DoubleSolenoid(3,4);
    	FR = new DoubleSolenoid(2,5);
    	BL = new DoubleSolenoid(0,7);
    	BR = new DoubleSolenoid(1,6);

    	FL.set(DoubleSolenoid.Value.kForward);
    	FR.set(DoubleSolenoid.Value.kForward);
    	BL.set(DoubleSolenoid.Value.kForward);
    	BR.set(DoubleSolenoid.Value.kForward);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		motor2.setInverted(true);
		motor3.setInverted(true);
		
		double speed = JS.getRawAxis(3);
		double throttle = -JS.getRawAxis(1);
		double twist = JS.getRawAxis(2);
		double strafe = JS.getRawAxis(0);
		boolean traction = JS.getRawButton(1);
		boolean raise = JS.getRawButton(7);
		boolean lower = JS.getRawButton(8);
		boolean intake = JS.getRawButton(11);
		boolean outtake = JS.getRawButton(12);
		boolean shoot = JS.getRawButton(3);
		
		motor0.set(twist+throttle-strafe); //L
		motor1.set(twist+throttle+strafe); //L
		motor2.set(-twist+throttle+strafe); //R
		motor3.set(-twist+throttle-strafe); //R
//		   shooter.set(ControlMode.PercentOutput, (speed+1)/2.);
		
		if(shoot) 
			shooter.set(ControlMode.PercentOutput, .4);
		else
			shooter.set(ControlMode.PercentOutput, 0);
			
		
		if(traction) FullTraction();
		else FullButterfly();
		
		if(intake) gearwheels.set(-1);
		else if(outtake) gearwheels.set(.5);
		else gearwheels.set(0);
		
		if(raise) geararm.set(ControlMode.PercentOutput, -.8);
		else if(lower) geararm.set(ControlMode.PercentOutput, .7);
		else geararm.set(ControlMode.PercentOutput, 0);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
	
	public void FullTraction(){
    	FL.set(DoubleSolenoid.Value.kReverse);		//this sets all of the solenoids to reverse so that tractions are engaged
    	FR.set(DoubleSolenoid.Value.kReverse);
    	BL.set(DoubleSolenoid.Value.kReverse);
    	BR.set(DoubleSolenoid.Value.kReverse);
    }
	
    public void FullButterfly(){
    	FL.set(DoubleSolenoid.Value.kForward);		//this sets all of the solenoids to full thrust, engaging the mecanum 
    	FR.set(DoubleSolenoid.Value.kForward);
    	BL.set(DoubleSolenoid.Value.kForward);
    	BR.set(DoubleSolenoid.Value.kForward);
    }
}
