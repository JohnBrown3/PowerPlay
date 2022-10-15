
package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.common.powerplay.SleeveDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "BlueLeft", group = "Linear Opmode")
public class BlueLeft extends LinearOpMode
{
    SleeveDetection sleeveDetection = new SleeveDetection();
    OpenCvCamera camera;
    String webcamName = "Webcam 1"

    //Hardware
    private DcMotor RearLeftWheel, RearRightWheel, FrontLeftWheel, FrontRightWheel, SpoolMotor;
    //private Servo ClawServo;
    private BNO055IMU imu;

    //Numbers
    private Orientation lastAngles = new Orientation();

    private int rearLeftPosition, rearRightPosition, frontLeftPosition, frontRightPosition;
    private double targetTime;

    static final double conversionFactor = 3000/44;
    static final double turnSpeed = 1;

    @Override
    public void runOpMode()
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, webcamName), cameraMonitorViewId);
        sleeveDetection = new SleeveDetection();
        camera.setPipeline(sleeveDetection);

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(300,400, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {}
        });

        //Assignment
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        RearLeftWheel = hardwareMap.dcMotor.get("RearLeftWheel");
        RearRightWheel = hardwareMap.dcMotor.get("RearRightWheel");
        FrontLeftWheel = hardwareMap.dcMotor.get("FrontLeftWheel");
        FrontRightWheel = hardwareMap.dcMotor.get("FrontRightWheel");
        SpoolMotor = hardwareMap.dcMotor.get("ArmServo");
        //ClawServo = hardwareMap.servo.get("ClawServo");

        BNO055IMU.Parameters imuParameters = new BNO055IMU.Parameters();
        imuParameters.mode = BNO055IMU.SensorMode.IMU;
        imuParameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imuParameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuParameters.loggingEnabled = false;

        imu.initialize(imuParameters);

        while (!isStarted()) {
            telemetry.addData("ROTATION: ", sleeveDetection.getPosition());
            telemetry.update();
        }

        waitForStart();

        if (opModeIsActive())
        {
            //Initialization
            InitializeHardware();

            VerticalDrive(6);
            Turn(90);
            HorizontalDrive(6);
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

    private void VerticalDrive (double target)
    {
        rearLeftPosition += (int) (target * conversionFactor);
        rearRightPosition += (int) (target * conversionFactor);
        frontLeftPosition += (int) (target * conversionFactor);
        frontRightPosition += (int) (target * conversionFactor);

        RearLeftWheel.setTargetPosition(rearLeftPosition);
        RearRightWheel.setTargetPosition(rearRightPosition);
        FrontLeftWheel.setTargetPosition(frontLeftPosition);
        FrontRightWheel.setTargetPosition(frontRightPosition);

        while (DriveLeft.isBusy() && DriveRight.isBusy()) {
            idle();
        }
    }

    private void HorizontalDrive (double target)
    {
        rearLeftPosition -= (int) (target * conversionFactor);
        rearRightPosition += (int) (target * conversionFactor);
        frontLeftPosition += (int) (target * conversionFactor);
        frontRightPosition -= (int) (target * conversionFactor);

        RearLeftWheel.setTargetPosition(rearLeftPosition);
        RearRightWheel.setTargetPosition(rearRightPosition);
        FrontLeftWheel.setTargetPosition(frontLeftPosition);
        FrontRightWheel.setTargetPosition(frontRightPosition);

        while (DriveLeft.isBusy() && DriveRight.isBusy()) {
            idle();
        }
    }

    private void Turn (int targetDegree)
    {
        ResetPosition();

        RearLeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RearRightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrontLeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrontRightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double leftPower, rightPower;

        Orientation currentAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = targetDegree - currentAngles.firstAngle;

        if(deltaAngle < -180)
        {
            deltaAngle += 360;
        }
        else if(deltaAngle > 180)
        {
            deltaAngle -= 360;
        }

        if(deltaAngle > 0)
        {
            leftPower = -turnSpeed;
            rightPower = turnSpeed;
        }
        else if(deltaAngle < 0)
        {
            leftPower = turnSpeed;
            rightPower = -turnSpeed;
        }
        else
        {
            return;
        }

        RearLeftWheel.setPower(leftPower);
        FrontLeftWheel.setPower(leftPower);
        RearRightWheel.setPower(rightPower);
        FrontRightWheel.setPower(rightPower);

        while(opModeIsActive() && Math.abs(deltaAngle) > 1)
        {
            currentAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            deltaAngle = targetDegree - currentAngles.firstAngle;

            if(deltaAngle < -180)
            {
                deltaAngle += 360;
            }
            else if(deltaAngle > 180)
            {
                deltaAngle -= 360;
            }

            idle();
        }

        RearLeftWheel.setPower(0);
        FrontLeftWheel.setPower(0);
        RearRightWheel.setPower(0);
        FrontRightWheel.setPower(0);

        ResetPosition();

        InitializeWheels();
    }

    private void InitializeHardware ()
    {
        SpoolMotor.setTargetPosition(0);
        SpoolMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        SpoolMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        SpoolMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        SpoolMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        InitializeWheels();
    }

    private void InitializeWheels ()
    {
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
