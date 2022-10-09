
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
  private DcMotor RearLeftWheel, RearRightWheel, FrontLeftWheel, FrontRightWheel, SpoolMotor;
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
    SpoolMotor = hardwareMap.dcMotor.get("ArmServo");
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
      SpoolMotor.setPower(0);
      SpoolMotor.setTargetPosition(0);
      //ClawServo.setPosition(0);
      
      // Runloop
      while (opModeIsActive()) 
      {
        /* Notes */
        // To set Gamepad as 1 or 2, hold 'Start' and press 'A' or 'B'
        // Pressing 'mode' on gamepad switches the left joystick and the d-pad controls

        /* Movement */
        // Driver Input
        float leftStickY = gamepad1.left_stick_y;
        float leftStickX = -gamepad1.left_stick_x;
        float rightStickX = -gamepad1.right_stick_x;

        // Direction/Power Calculation
        float denominator = Math.max(Math.abs(leftStickY) + Math.abs(leftStickX) + Math.abs(rightStickX), 1);
        float rearLeftPower = (leftStickY - leftStickX + rightStickX) / denominator;
        float rearRightPower = (leftStickY + leftStickX - rightStickX) / denominator;
        float frontLeftPower = (leftStickY + leftStickX + rightStickX) / denominator;
        float frontRightPower = (leftStickY - leftStickX - rightStickX) / denominator;
        
        // Direction/Power Assignment
        RearLeftWheel.setPower(rearLeftPower);
        RearRightWheel.setPower(rearRightPower);
        FrontLeftWheel.setPower(frontLeftPower);
        FrontRightWheel.setPower(frontRightPower);

        /* Slide Control */
        // Manual slide control
        SpoolMotor.setPower(1);
        SpoolMotor.setTargetPosition(ArmMotor.getTargetPosition() - (int)(gamepad2.right_stick_y * 6));

        // Preset Positions
        if (gamepad2.x)
        {
            SpoolMotor.setTargetPosition(0);
        }
        else if (gamepad2.a)
        {
            SpoolMotor.setTargetPosition(550);
        }
        else if (gamepad2.b)
        {
            SpoolMotor.setTargetPosition(1100);
        }
        else if (gamepad2.y)
        {
            SpoolMotor.setTargetPosition(1600);
        }

        /* Not Implemented */
        /*

        

        // Servo Control //
        if (gamepad2.right_trigger != 0)
        {
            ClawServo.setPosition(0.50);
        }
        else
        {
            ClawServo.setPosition(0);
        }
        */
        
        /* Telemetry */
        // Displayed on the Driver Station's right panel
        telemetry.addData("RearLeftWheel", RearLeftWheel.getPower());
        telemetry.addData("RearRightWheel", RearRightWheel.getPower());
        telemetry.addData("FrontLeftWheel", FrontLeftWheel.getPower());
        telemetry.addData("FrontRightWheel", FrontRightWheel.getPower());
        telemetry.addData("SpoolMotorPosition", SpoolMotor.getCurrentPosition());
        //telemetry.addData("ClawServo", ClawServo.getPosition());
        telemetry.update();
      }
    }
  }
}
