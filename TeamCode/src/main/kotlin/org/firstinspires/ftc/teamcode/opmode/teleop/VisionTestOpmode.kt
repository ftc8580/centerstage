package org.firstinspires.ftc.teamcode.opmode.teleop

// Op Mode Stuff
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.subsystem.Vision

/*
 * This OpMode illustrates the basics of using both AprilTag recognition and TensorFlow
 * Object Detection.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */

@TeleOp(name = "Vision Subsystem Test", group = "Subsystems")
//@Disabled
class VisionTestOpmode : OpModeBase() {
    private lateinit var vision: Vision
    // Update the tfliteModelFileName with the path to the tflite file for your prop.
    private var tfliteModelFileName = "/sdcard/FIRST/tflitemodels/CDNewSleeve.tflite"
    // Update the fliteLabels with your trained labels EXACTLY how you defined it when you trained your model.
    private var tfliteLabels = listOf<String>(
        "1 Pacman",
        "2 Cherry",
        "3 Ghost"
    )
    override fun initialize() {
        initHardware(false)
        vision = Vision(hardware, multitelemetry, tfliteModelFileName,tfliteLabels)
        vision.initDoubleVision()
    }

    override fun runOpMode() {
        // This OpMode loops continuously, allowing the user to switch between
        // AprilTag and TensorFlow Object Detection (TFOD) image processors.
        while (!isStopRequested) {
            if (opModeInInit()) {
                telemetry.addData("DS preview on/off", "3 dots, Camera Stream")
                telemetry.addLine()
                telemetry.addLine("----------------------------------------")
            }
            if (vision.myVisionPortal!!.getProcessorEnabled(vision.aprilTag)) {
                // User instructions: Dpad left or Dpad right.
                telemetry.addLine("Dpad Left to disable AprilTag")
                telemetry.addLine()
                vision.telemetryAprilTag()
            } else {
                telemetry.addLine("Dpad Right to enable AprilTag")
            }
            telemetry.addLine()
            telemetry.addLine("----------------------------------------")
            if (vision.myVisionPortal!!.getProcessorEnabled(vision.tfod)) {
                telemetry.addLine("Dpad Down to disable TFOD")
                telemetry.addLine()
                vision.telemetryTfod()
            } else {
                telemetry.addLine("Dpad Up to enable TFOD")
            }
            // Push telemetry to the Driver Station.
            telemetry.update()
            if (gamepad1.dpad_left) {
                vision.myVisionPortal!!.setProcessorEnabled(vision.aprilTag, false)
            } else if (gamepad1.dpad_right) {
                vision.myVisionPortal!!.setProcessorEnabled(vision.aprilTag, true)
            }
            if (gamepad1.dpad_down) {
                vision.myVisionPortal!!.setProcessorEnabled(vision.tfod, false)
            } else if (gamepad1.dpad_up) {
                vision.myVisionPortal!!.setProcessorEnabled(vision.tfod, true)
            }
            sleep(20)
        } // end while loop
    } // end method runOpMode()
} // end class
