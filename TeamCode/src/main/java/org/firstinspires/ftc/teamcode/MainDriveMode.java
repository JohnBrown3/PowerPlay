
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "MainDriveMode", group = "Linear Opmode")
public class MainDriveMode extends LinearOpMode 
{
  private DcMotor DriveLeft, DriveRight, Carousel, ArmMotor;
  private Servo LeftClaw, RightClaw, ArmServo;

  private double speedDivider = 1.5;
  private double rightFactor;
  private double leftFactor;
  private boolean isLocked = true;

  @Override
  public void runOpMode() 
  {
    //Maps hardware on robot to previously declared variables
    DriveLeft = hardwareMap.dcMotor.get("DriveLeft");
    DriveRight = hardwareMap.dcMotor.get("DriveRight");
    Carousel = hardwareMap.dcMotor.get("Carousel");
    ArmMotor = hardwareMap.dcMotor.get("ArmMotor");
    LeftClaw = hardwareMap.servo.get("LeftClaw");
    RightClaw = hardwareMap.servo.get("RightClaw");
    ArmServo = hardwareMap.servo.get("ArmServo");

    waitForStart();
    
    if (opModeIsActive()) 
    {
      //Initializes modes and encoders for arm servo
      ArmMotor.setTargetPosition(0);
      ArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      ArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      ArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      ArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      
      //Reverses directions for backwards motors/servos
      DriveRight.setDirection(DcMotorSimple.Direction.REVERSE);
      RightClaw.setDirection(Servo.Direction.REVERSE);
      
      DriveRight.setPower(0);
      DriveLeft.setPower(0);
      ArmMotor.setTargetPosition(100);

      ArmServo.setPosition(0.25);
      
      while (opModeIsActive()) 
      {
        //Pressing 'mode' on gamepad switches the left joystick and the d-pad controls

        //Holding left trigger speeds up robot
        if(gamepad1.left_trigger == 1)
        {
          speedDivider = 1.5;
        }
        else
        {
          speedDivider = 2;
        }

        //Drive code
        if(DriveLeft.getPower() < 0 && DriveRight.getPower() < 0)
        {
          rightFactor = 1.00;
          leftFactor = 0.88;
        }
        else if(DriveLeft.getPower() < 0 || DriveRight.getPower() < 0)
        {
          rightFactor = 1.00;
          leftFactor = 1.00;
        }
        else
        {
          rightFactor = 0.88;
          leftFactor = 1.00;
        }

        DriveLeft.setPower(Math.max(-1.0, Math.min(-gamepad1.left_stick_y / speedDivider + gamepad1.right_stick_x / speedDivider, 1.0)) * leftFactor);
        DriveRight.setPower(Math.max(-1.0, Math.min(-gamepad1.left_stick_y / speedDivider - gamepad1.right_stick_x / speedDivider, 1.0)) * rightFactor);

        //Arm Motor
        ArmMotor.setPower(1);

        //Sets arm motor to position 0
        if(gamepad2.x)
        {
          ArmMotor.setTargetPosition(190 - 70);
          ArmServo.setPosition(0.645);
        }
        else if(gamepad2.a)
        {
          ArmMotor.setTargetPosition(300 - 70);
          ArmServo.setPosition(0.685);
        }
        else if(gamepad2.b)
        {
          ArmMotor.setTargetPosition(490 - 50);
          ArmServo.setPosition(0.8);
        }
        else if(gamepad2.y)
        {
          ArmMotor.setTargetPosition(660 - 70);
          ArmServo.setPosition(0.80);
        }

        if(gamepad2.dpad_left)
        {
          if(isLocked)
          {
            isLocked = false;
          }
          else
          {
            isLocked = true;
          }
        }

        //Manually adjusts arm motor
        if(isLocked)
        {
          ArmMotor.setTargetPosition(Math.max(100, Math.min(ArmMotor.getTargetPosition() - (int)(gamepad2.right_stick_y * 6), 800)));
        }
        else
        {
          ArmMotor.setTargetPosition(Math.max(100, ArmMotor.getTargetPosition() - (int)(gamepad2.right_stick_y * 6)));
        }


        //Spins carousel
        Carousel.setPower(gamepad2.left_stick_x);

        //Controls claw clamping with triggers
        if (gamepad2.right_trigger != 0)
        {
          LeftClaw.setPosition(.7);
          RightClaw.setPosition(.7);
        } 
        else
        {
          LeftClaw.setPosition(0.05);
          RightClaw.setPosition(0);
        }
        
        //Preset arm servo positions
        if(gamepad2.dpad_up)
        {
          ArmServo.setPosition(0.25);
        }
        else if (gamepad2.dpad_down)
        {
          ArmServo.setPosition(0.645);
        }
        
        //Manually adjusts arm servo
        ArmServo.setPosition(Math.max(0.25, Math.min(ArmServo.getPosition() + gamepad2.left_stick_y / 150, 0.80)));

        
        /* Telemetry */
        //Useful data displayed on driver hub for testing
        telemetry.addData("Left Power", DriveLeft.getPower());
        telemetry.addData("Right Power", DriveRight.getPower());
        telemetry.addData("Carousel Power", Carousel.getPower());
        telemetry.addData("Arm Position", ArmMotor.getCurrentPosition());
        telemetry.addData("Target Position", ArmMotor.getTargetPosition());
        telemetry.addData("Arm Servo", ArmServo.getPosition());
        telemetry.update();
      }
    }
  }
}
