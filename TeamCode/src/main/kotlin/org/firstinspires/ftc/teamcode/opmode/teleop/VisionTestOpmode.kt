package org.firstinspires.ftc.teamcode.opmode.teleop

// Op Mode Stuff
import android.annotation.SuppressLint
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.opmode.OpModeBase
import org.firstinspires.ftc.teamcode.vision.DoubleVision

/*
 * This OpMode illustrates the basics of using both AprilTag recognition and TensorFlow
 * Object Detection.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */

@TeleOp(name = "DoubleVision Subsystem Test", group = "Subsystems")
//@Disabled
class VisionTestOpmode : OpModeBase() {
    private lateinit var doubleVision: DoubleVision
    // Update the tfliteModelFileName with the path to the tflite file for your prop.
    @SuppressLint("SdCardPath")
    private var tfliteModelFileName = "/sdcard/FIRST/tflitemodels/CDNewSleeve.tflite"
    // Update the tfliteLabels with your trained labels EXACTLY how you defined it when you trained your model.
    private var tfliteLabels = listOf(
        "1 Pacman",
        "2 Cherry",
        "3 Ghost"
    )
    override fun initialize() {
        initHardware(false)
        doubleVision = DoubleVision(hardware, multiTelemetry, tfliteModelFileName,tfliteLabels)
        doubleVision.initDoubleVision()
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
            if (doubleVision.myVisionPortal!!.getProcessorEnabled(doubleVision.aprilTag)) {
                // User instructions: Dpad left or Dpad right.
                telemetry.addLine("Dpad Left to disable AprilTag")
                telemetry.addLine()
                doubleVision.telemetryAprilTag()
            } else {
                telemetry.addLine("Dpad Right to enable AprilTag")
            }
            telemetry.addLine()
            telemetry.addLine("----------------------------------------")
            if (doubleVision.myVisionPortal!!.getProcessorEnabled(doubleVision.tfod)) {
                telemetry.addLine("Dpad Down to disable TFOD")
                telemetry.addLine()
                doubleVision.telemetryTfod()
            } else {
                telemetry.addLine("Dpad Up to enable TFOD")
            }
            // Push telemetry to the Driver Station.
            telemetry.update()
            if (gamepad1.dpad_left) {
                doubleVision.myVisionPortal!!.setProcessorEnabled(doubleVision.aprilTag, false)
            } else if (gamepad1.dpad_right) {
                doubleVision.myVisionPortal!!.setProcessorEnabled(doubleVision.aprilTag, true)
            }
            if (gamepad1.dpad_down) {
                doubleVision.myVisionPortal!!.setProcessorEnabled(doubleVision.tfod, false)
            } else if (gamepad1.dpad_up) {
                doubleVision.myVisionPortal!!.setProcessorEnabled(doubleVision.tfod, true)
            }
            sleep(20)
        } // end while loop
    } // end method runOpMode()
} // end class
