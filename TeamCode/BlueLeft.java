
package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "BlueLeft", group = "Linear Opmode")
public class BlueLeft extends LinearOpMode
{
    //Hardware
    private DcMotor RearLeftWheel, RearRightWheel, FrontLeftWheel, FrontRightWheel, SpoolMotor;
    //private Servo ClawServo;

    //Numbers
    private int rearLeftPosition, rearRightPosition, frontLeftPosition, frontRightPosition;
    private double targetTime;

    static final double conversionFactor = 3000/44;

    @Override
    public void runOpMode()
    {
        //Assignment
        RearLeftWheel = hardwareMap.dcMotor.get("RearLeftWheel");
        RearRightWheel = hardwareMap.dcMotor.get("RearRightWheel");
        FrontLeftWheel = hardwareMap.dcMotor.get("FrontLeftWheel");
        FrontRightWheel = hardwareMap.dcMotor.get("FrontRightWheel");
        SpoolMotor = hardwareMap.dcMotor.get("ArmServo");
        //ClawServo = hardwareMap.servo.get("ClawServo");

        waitForStart();

        if (opModeIsActive())
        {
            //Initialization
            InitializeHardware();


        }
    }

    private void WaitForSeconds(double time)
    {
        targetTime = getRuntime() + time;

        while(getRuntime() < targetTime)
        {
            idle();
        }
    }

    private void VerticalMovement(double target) {
        rearLeftPosition += (int) (target);
        rearRightPosition += (int) (target);
        frontLeftPosition += (int) (target);
        frontRightPosition += (int) (target);


        RearLeftWheel.setTargetPosition();
        RearRightWheel.setTargetPosition();
        FrontLeftWheel.setTargetPosition();
        FrontRightWheel.setTargetPosition();
    }

    private void Drive(double leftTarget, double rightTarget) {

        leftPosition += (int) (leftTarget * conversionFactor * leftFactor);
        rightPosition += (int) (rightTarget * conversionFactor * rightFactor);

        DriveLeft.setTargetPosition(leftPosition);
        DriveRight.setTargetPosition(rightPosition);

        DriveLeft.setPower(0.5 * leftFactor);
        DriveRight.setPower(0.5 * rightFactor);

        while (DriveLeft.isBusy() && DriveRight.isBusy()) {
            idle();
        }
    }

    private void InitializeHardware() {
        SpoolMotor.setTargetPosition(0);
        SpoolMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SpoolMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SpoolMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SpoolMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RearLeftWheel.setTargetPosition(0);
        RearLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RearLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RearLeftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RearLeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        RearRightWheel.setTargetPosition(0);
        RearRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RearRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RearRightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RearRightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FrontLeftWheel.setTargetPosition(0);
        FrontLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontLeftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontLeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FrontRightWheel.setTargetPosition(0);
        FrontRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        FrontRightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}

