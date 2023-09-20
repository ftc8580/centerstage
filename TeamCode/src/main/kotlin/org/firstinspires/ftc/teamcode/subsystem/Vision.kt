package org.firstinspires.ftc.teamcode.subsystem

// Hardware
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName

// Vision
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import org.firstinspires.ftc.vision.tfod.TfodProcessor

// Op Mode Stuff
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

/*
 * This OpMode illustrates the basics of using both AprilTag recognition and TensorFlow
 * Object Detection.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */


@TeleOp(name = "Vision Subsystem Test", group = "Subsystems")
//@Disabled
class ConceptDoubleVision : LinearOpMode() {
    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private var aprilTag: AprilTagProcessor? = null

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private var tfod: TfodProcessor? = null

    /**
     * The variable to store our instance of the vision portal.
     */
    private var myVisionPortal: VisionPortal? = null
    override fun runOpMode() {
        initDoubleVision()
        // This OpMode loops continuously, allowing the user to switch between
        // AprilTag and TensorFlow Object Detection (TFOD) image processors.
        while (!isStopRequested) {
            if (opModeInInit()) {
                telemetry.addData("DS preview on/off", "3 dots, Camera Stream")
                telemetry.addLine()
                telemetry.addLine("----------------------------------------")
            }
            if (myVisionPortal!!.getProcessorEnabled(aprilTag)) {
                // User instructions: Dpad left or Dpad right.
                telemetry.addLine("Dpad Left to disable AprilTag")
                telemetry.addLine()
                telemetryAprilTag()
            } else {
                telemetry.addLine("Dpad Right to enable AprilTag")
            }
            telemetry.addLine()
            telemetry.addLine("----------------------------------------")
            if (myVisionPortal!!.getProcessorEnabled(tfod)) {
                telemetry.addLine("Dpad Down to disable TFOD")
                telemetry.addLine()
                telemetryTfod()
            } else {
                telemetry.addLine("Dpad Up to enable TFOD")
            }
            // Push telemetry to the Driver Station.
            telemetry.update()
            if (gamepad1.dpad_left) {
                myVisionPortal!!.setProcessorEnabled(aprilTag, false)
            } else if (gamepad1.dpad_right) {
                myVisionPortal!!.setProcessorEnabled(aprilTag, true)
            }
            if (gamepad1.dpad_down) {
                myVisionPortal!!.setProcessorEnabled(tfod, false)
            } else if (gamepad1.dpad_up) {
                myVisionPortal!!.setProcessorEnabled(tfod, true)
            }
            sleep(20)
        } // end while loop
    } // end method runOpMode()

    /**
     * Initialize AprilTag and TFOD.
     */
    private fun initDoubleVision() {
        // -----------------------------------------------------------------------------------------
        // AprilTag Configuration
        // -----------------------------------------------------------------------------------------
        aprilTag = AprilTagProcessor.Builder()
                .build()

        // -----------------------------------------------------------------------------------------
        // TFOD Configuration
        // -----------------------------------------------------------------------------------------
        tfod = TfodProcessor.Builder().setModelFileName("/sdcard/FIRST/tflitemodels/CDNewSleeve.tflite") //TODO Update Model
                .build()
        // -----------------------------------------------------------------------------------------
        // Camera Configuration
        // -----------------------------------------------------------------------------------------
        myVisionPortal = if (USE_WEBCAM) {
            VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName::class.java, "webcam"))
                    .addProcessors(tfod, aprilTag)
                    .build()
        } else {
            VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessors(tfod, aprilTag)
                    .build()
        }
    } // end initDoubleVision()

    /**
     * Add telemetry about AprilTag detections.
     */
    private fun telemetryAprilTag() {
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
        } // end for() loop
    } // end method telemetryAprilTag()

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    private fun telemetryTfod() {
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
        } // end for() loop
    } // end method telemetryTfod()

    companion object {
        private const val USE_WEBCAM = true // true for webcam, false for phone camera
    }
} // end class
