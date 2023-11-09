package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.PtzControl
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.teamcode.hardware.HardwareManager
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.tfod.TfodProcessor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class TensorFlowObjectDetection(
    hardware: HardwareManager,
    private val telemetry: MultipleTelemetry,
    tfliteModelFileName: String? = null,
    tfliteLabels: List<String>? = null
) {
    private val tfod: TfodProcessor
    private val visionPortal: VisionPortal

    init {
        if (tfliteModelFileName == null) {
            tfod = TfodProcessor.easyCreateWithDefaults()
        } else {
            var tfodBuilder = TfodProcessor.Builder()
            tfodBuilder = tfodBuilder.setModelFileName(tfliteModelFileName)

            if (tfliteLabels != null) {
                tfodBuilder = tfodBuilder.setModelLabels(tfliteLabels)
            }

            tfod = tfodBuilder.build()
        }

        tfod.setMinResultConfidence(0.5f)

        visionPortal = VisionPortal.easyCreateWithDefaults(hardware.webcam, tfod)
    }

    fun getRandomizedSpikeLocation(): RandomizedSpikeLocation {
        val highestConfidenceRecognition = tfod.recognitions.maxByOrNull { it.confidence }

        return if (highestConfidenceRecognition == null) {
            RandomizedSpikeLocation.UNKNOWN
        } else {
            getOverlapValues(highestConfidenceRecognition)
        }
    }


    fun telemetryTfod() {
        val currentRecognitions = tfod.recognitions
        telemetry.addData("# Objects Detected", currentRecognitions.size)

        // Step through the list of recognitions and display info for each one.
        for (recognition in currentRecognitions) {
            val x = ((recognition.left + recognition.right) / 2).toDouble()
            val y = ((recognition.top + recognition.bottom) / 2).toDouble()
            telemetry.addData("", " ")
            telemetry.addData(
                "Image",
                "%s (%.0f %% Conf.)",
                recognition.label,
                recognition.confidence * 100
            )
            telemetry.addData("- Position", "%.0f / %.0f", x, y)
            telemetry.addData("- Size", "%.0f x %.0f", recognition.width, recognition.height)
        }

        telemetry.addData("- Spike Location", getRandomizedSpikeLocation())
    }

    fun suspend() {
        visionPortal.stopStreaming()
    }

    fun resume() {
        visionPortal.resumeStreaming()
    }

    private fun getOverlapValues(recognition: Recognition): RandomizedSpikeLocation {
        val imageWidth = recognition.imageWidth.toDouble()
        val imageHeight = recognition.imageHeight.toDouble()

        val centerLine = floor(imageWidth * 0.5)

        val left = Rectangle(0.0, centerLine, 0.0, imageHeight)
        val center = Rectangle(centerLine + 0.0001, imageWidth, 0.0, imageHeight)

        val recognitionRectangle = Rectangle(
            recognition.left.toDouble(),
            recognition.right.toDouble(),
            recognition.top.toDouble(),
            recognition.bottom.toDouble()
        )

        val leftOverlap = left.overlapArea(recognitionRectangle)
        val centerOverlap = center.overlapArea(recognitionRectangle)

        return if (leftOverlap > centerOverlap) {
            RandomizedSpikeLocation.LEFT
        } else if (centerOverlap > leftOverlap) {
            RandomizedSpikeLocation.CENTER
        } else {
            RandomizedSpikeLocation.RIGHT
        }
    }

    class Rectangle(private val left: Double, private val right: Double, private val top: Double, private val bottom: Double) {
        fun overlapArea(otherRectangle: Rectangle): Double {
            val xOverlap = max(0.0, min(this.right, otherRectangle.right) - max(this.left, otherRectangle.left))
            val yOverlap = max(0.0, min(this.bottom, otherRectangle.bottom) - max(this.top, otherRectangle.top))

            return xOverlap * yOverlap
        }
    }
}