
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SleeveDetection extends OpenCvPipeline {

    public enum ParkingPosition {
        ONE, // Yellow
        TWO, // Cyan
        THREE // Magenta
    }

    // Rectangle used to detect beacon
    Point topLeftPoint = new Point (390, 810);
    Point bottomRightPoint = new Point (690, 1110)

    static final Rect CENTER_BOX = new Rect (topLeftPoint, bottomRightPoint);

    // Lower and upper boundaries for colors
    private static final Scalar
            low_one_bounds  = new Scalar(200, 200, 0, 255),
            upper_one_bounds  = new Scalar(255, 255, 130, 255),
            low_two_bounds    = new Scalar(0, 200, 200, 255),
            upper_two_bounds    = new Scalar(150, 255, 255, 255),
            low_three_bounds = new Scalar(170, 0, 170, 255),
            upper_three_bounds = new Scalar(255, 60, 255, 255);

    // Color definitions
    private final Scalar
            ONE  = new Scalar(255, 255, 0),
            TWO    = new Scalar(0, 255, 255),
            THREE = new Scalar(255, 0, 255);

    // Percent and mat definitions
    private double onePercent, twoPercent, threePercent;
    private Mat oneMat = new Mat(), twoMat = new Mat(), threeMat = new Mat(), blurredMat = new Mat();

    // Running variable storing the parking position
    private volatile ParkingPosition position = ParkingPosition.ONE;

    @Override
    public Mat processFrame(Mat input) {
        // Noise reduction
        Imgproc.blur(input, blurredMat, new Size(5, 5));
        blurredMat = blurredMat.submat(CENTER_BOX);

        // Apply Morphology
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(blurredMat, blurredMat, Imgproc.MORPH_CLOSE, kernel);

        // Gets channels from given source mat
        Core.inRange(blurredMat, low_one_bounds, upper_one_bounds, oneMat);
        Core.inRange(blurredMat, low_two_bounds, upper_two_bounds, twoMat);
        Core.inRange(blurredMat, low_three_bounds, upper_three_bounds, threeMat);

        // Gets color specific values
        onePercent = Core.countNonZero(oneMat);
        twoPercent = Core.countNonZero(twoMat);
        threePercent = Core.countNonZero(threeMat);

        // Calculates the highest amount of pixels being covered on each side
        double maxPercent = Math.max(onePercent, Math.max(twoPercent, threePercent));

        // Checks all percentages, will highlight bounding box in camera preview
        // based on what color is being detected
        if (maxPercent == onePercent) {
            position = ParkingPosition.ONE
            Imgproc.rectangle(
                    input,
                    topLeftPoint,
                    bottomRightPoint,
                    ONE,
                    2
            );
        } else if (maxPercent == twoPercent) {
            position = ParkingPosition.TWO;
            Imgproc.rectangle(
                    input,
                    topLeftPoint,
                    bottomRightPoint,
                    TWO,
                    2
            );
        } else if (maxPercent == threePercent) {
            position = ParkingPosition.THREE;
            Imgproc.rectangle(
                    input,
                    topLeftPoint,
                    bottomRightPoint,
                    THREE,
                    2
            );
        }

        // Memory cleanup
        blurredMat.release();
        oneMat.release();
        twoMat.release();
        threeMat.release();

        return input;
    }

    // Returns an enum being the current position where the robot will park
    public ParkingPosition getPosition() {
        return position;
    }
}
