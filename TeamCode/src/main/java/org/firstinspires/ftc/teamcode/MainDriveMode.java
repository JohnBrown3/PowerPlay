
// Adds FTC libraries
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

// Creates OpMode for Driver Hub
@TeleOp(name = "MainDriveMode", group = "Linear Opmode")
public class MainDriveMode extends LinearOpMode 
{
  // Declares hardware variables
  private DcMotor RearLeftWheel, RearRightWheel, FrontLeftWheel, FrontRightWheel;
  //private DcMotor RearLeftWheel, RearRightWheel, FrontLeftWheel, FrontRightWheel, SpoolMotor;
  //private Servo ClawServo;

  // When the Start Buttons is pressed
  @Override
  public void runOpMode() 
  {
    // Maps hardware to hardware variables
    RearLeftWheel = hardwareMap.dcMotor.get("RearLeftWheel");
    RearRightWheel = hardwareMap.dcMotor.get("RearRightWheel");
    FrontLeftWheel = hardwareMap.dcMotor.get("FrontLeftWheel");
    FrontRightWheel = hardwareMap.dcMotor.get("FrontRightWheel");
    //SpoolMotor = hardwareMap.dcMotor.get("ArmServo");
    //ClawServo = hardwareMap.servo.get("ClawServo");

    // Reverse right motors
    RearRightWheel.setDirection(DcMotorSimple.Direction.REVERSE);
    FrontRightWheel.setDirection(DcMotorSimple.Direction.REVERSE);

    waitForStart();
    
    // When the Play Button is pressed
    if (opModeIsActive()) 
    {
      // Initializes modes and encoders for spool motor
      SpoolMotor.setTargetPosition(0);
      SpoolMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
      SpoolMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      SpoolMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      SpoolMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      
      // Reverses directions for backwards motors
      RearRightWheel.setDirection(DcMotorSimple.Direction.REVERSE);
      FrontRightWheel.setDirection(DcMotorSimple.Direction.REVERSE);
      
      // Set powers and positions to motors & servos
      RearLeftWheel.setPower(0);
      RearRightWheel.setPower(0);
      FrontLeftWheel.setPower(0);
      FrontRightWheel.setPower(0);
      SpoolMotor.setTargetPosition(0);
      ClawServo.setPosition(0);
      
      // Runloop
      while (opModeIsActive()) 
      {
        /* Notes */
        // To set Gamepad as 1 or 2, hold 'Start' and press 'A' or 'B'
        // Pressing 'mode' on gamepad switches the left joystick and the d-pad controls

        /* Movement */
        // Driver Input
        float leftStickY = -gamepad1.left_stick_y;
        float leftStickX = gamepad1.left_stick_x;
        float rightStickX = gamepad1.right_stick_x;

        // Direction/Power Calculation
        float denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        float rearLeftPower = (leftStickY - leftStickX + rightStickX) / denominator;
        float rearRightPower = (leftStickY + leftStickX - rightStickX) / denominator;
        float frontLeftPower = (leftStickY + leftStickX + rightStickX) / denominator;
        float frontRightPower = (leftStickY - leftStickX - rightStickX) / denominator;
        
        // Direction/Power Assignment
        RearLeftWheel.setPower(rearLeftPower);
        RearRightWheel.setPower(rearRightPower);
        FrontLeftWheel.setPower(frontLeftPower);
        FrontRightWheel.setPower(frontRightPower);

        /* OLD CODE BELOW */
        /*
        
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
        
        */
        
        /* Telemetry */
        // Displayed on the Driver Station's right panel
        telemetry.addData("RearLeftWheel", RearLeftWheel.getPower());
        telemetry.addData("RearRightWheel", RearRightWheel.getPower());
        telemetry.addData("FrontLeftWheel", FrontLeftWheel.getPower());
        telemetry.addData("FrontRightWheel", FrontRightWheel.getPower());
        //telemetry.addData("SpoolMotorPosition", SpoolMotor.getCurrentPosition());
        //telemetry.addData("ClawServo", ClawServo.getPosition());
        telemetry.update();
      }
    }
  }
}
