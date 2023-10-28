package org.firstinspires.ftc.teamcode.vision

// Hardware
import org.firstinspires.ftc.teamcode.hardware.HardwareManager

// DoubleVision
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import org.firstinspires.ftc.vision.tfod.TfodProcessor

/*
 * This OpMode illustrates the basics of using both AprilTag recognition and TensorFlow
 * Object Detection.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */


class DoubleVision (private val hardware:HardwareManager, private val telemetry: MultipleTelemetry, private val tfliteModelFileName:String, private val tfliteLabels:List<String>){
    /**
     * The variable to store our instance of the AprilTag processor.
     */
    var aprilTag: AprilTagProcessor? = null

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    var tfod: TfodProcessor? = null

    /**
     * The variable to store our instance of the vision portal.
     */
    var myVisionPortal: VisionPortal? = null

    /**
     * Initialize AprilTag and TFOD.
     */
    fun initDoubleVision() {
        if (hardware.webcam == null) return
        // -----------------------------------------------------------------------------------------
        // AprilTag Configuration
        // -----------------------------------------------------------------------------------------
        aprilTag = AprilTagProcessor.Builder()
            .build()

        // -----------------------------------------------------------------------------------------
        // TFOD Configuration
        // -----------------------------------------------------------------------------------------
        tfod = TfodProcessor.Builder().setModelLabels(tfliteLabels).setModelFileName(tfliteModelFileName)
            .build()
        // -----------------------------------------------------------------------------------------
        // Camera Configuration
        // -----------------------------------------------------------------------------------------
        myVisionPortal = VisionPortal.Builder()
            .setCamera(hardware.webcam)
            .addProcessors(tfod, aprilTag)
            .build()
    }

    /**
     * Add telemetry about AprilTag detections.
     */
    fun telemetryAprilTag() {
        val currentDetections: List<AprilTagDetection> = aprilTag!!.detections
        telemetry.addData("# AprilTags Detected", currentDetections.size)

        // Step through the list of detections and display info for each one.
        for (detection in currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name))
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z))
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw))
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation))
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id))
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y))
            }
        }
    }

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    fun telemetryTfod() {
        val currentRecognitions = tfod!!.recognitions
        telemetry.addData("# Objects Detected", currentRecognitions.size)

        // Step through the list of recognitions and display info for each one.
        for (recognition in currentRecognitions) {
            val x = ((recognition.left + recognition.right) / 2).toDouble()
            val y = ((recognition.top + recognition.bottom) / 2).toDouble()
            telemetry.addData("", " ")
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.label, recognition.confidence * 100)
            telemetry.addData("- Position", "%.0f / %.0f", x, y)
            telemetry.addData("- Size", "%.0f x %.0f", recognition.width, recognition.height)

        }
    }
}
