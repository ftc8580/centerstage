package org.firstinspires.ftc.teamcode.util

import android.annotation.SuppressLint
import android.content.Context
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl.DefaultOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier.Notifications
import com.qualcomm.robotcore.util.RobotLog
import com.qualcomm.robotcore.util.WebHandlerManager
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import org.firstinspires.ftc.ftccommon.external.WebHandlerRegistrar
import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import org.firstinspires.ftc.teamcode.config.DriveConstants
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.drive.StandardTrackingWheelLocalizer
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Objects

object LogFiles {
    private val ROOT = File(AppUtil.ROOT_FOLDER.toString() + "/RoadRunner/logs/")
    var log = LogFile("uninitialized")
    fun record(
        targetPose: Pose2d,
        pose: Pose2d,
        voltage: Double,
        lastDriveEncPositions: List<Int>,
        lastDriveEncVels: List<Int>,
        lastTrackingEncPositions: List<Int>,
        lastTrackingEncVels: List<Int>
    ) {
        val nsTime = System.nanoTime()
        if (nsTime - log.nsStart > 3 * 60 * 1000000000L) {
            return
        }
        log.nsTimes.add(nsTime)
        log.targetXs.add(targetPose.x)
        log.targetYs.add(targetPose.y)
        log.targetHeadings.add(targetPose.heading)
        log.xs.add(pose.x)
        log.ys.add(pose.y)
        log.headings.add(pose.heading)
        log.voltages.add(voltage)
        while (log.driveEncPositions.size < lastDriveEncPositions.size) {
            log.driveEncPositions.add(ArrayList())
        }
        while (log.driveEncVels.size < lastDriveEncVels.size) {
            log.driveEncVels.add(ArrayList())
        }
        while (log.trackingEncPositions.size < lastTrackingEncPositions.size) {
            log.trackingEncPositions.add(ArrayList())
        }
        while (log.trackingEncVels.size < lastTrackingEncVels.size) {
            log.trackingEncVels.add(ArrayList())
        }
        for (i in lastDriveEncPositions.indices) {
            log.driveEncPositions[i].add(lastDriveEncPositions[i])
        }
        for (i in lastDriveEncVels.indices) {
            log.driveEncVels[i].add(lastDriveEncVels[i])
        }
        for (i in lastTrackingEncPositions.indices) {
            log.trackingEncPositions[i].add(lastTrackingEncPositions[i])
        }
        for (i in lastTrackingEncVels.indices) {
            log.trackingEncVels[i].add(lastTrackingEncVels[i])
        }
    }

    private val notifHandler: Notifications = object : Notifications {
        @SuppressLint("SimpleDateFormat")
        val dateFormat: DateFormat = SimpleDateFormat("yyyy_MM_dd__HH_mm_ss_SSS")
        val jsonWriter = ObjectMapper(JsonFactory())
            .writerWithDefaultPrettyPrinter()

        override fun onOpModePreInit(opMode: OpMode) {
            log = LogFile(opMode.javaClass.canonicalName)

            // clean up old files
            val fs = Objects.requireNonNull(ROOT.listFiles())
            Arrays.sort(fs) { a: File, b: File ->
                a.lastModified().compareTo(b.lastModified())
            }
            var totalSizeBytes: Long = 0
            for (f in fs) {
                totalSizeBytes += f.length()
            }
            var i = 0
            while (i < fs.size && totalSizeBytes >= 32 * 1000 * 1000) {
                totalSizeBytes -= fs[i].length()
                if (!fs[i].delete()) {
                    RobotLog.setGlobalErrorMsg("Unable to delete file " + fs[i].absolutePath)
                }
                ++i
            }
        }

        override fun onOpModePreStart(opMode: OpMode) {
            log.nsStart = System.nanoTime()
        }

        override fun onOpModePostStop(opMode: OpMode) {
            log.nsStop = System.nanoTime()
            if (opMode !is DefaultOpMode) {
                ROOT.mkdirs()
                val filename =
                    dateFormat.format(Date(log.msInit)) + "__" + opMode.javaClass.simpleName + ".json"
                val file = File(ROOT, filename)
                try {
                    jsonWriter.writeValue(file, log)
                } catch (e: IOException) {
                    RobotLog.setGlobalErrorMsg(
                        RuntimeException(e),
                        "Unable to write data to " + file.absolutePath
                    )
                }
            }
        }
    }

    @WebHandlerRegistrar
    fun registerRoutes(context: Context?, manager: WebHandlerManager) {
        ROOT.mkdirs()

        // op mode manager only stores a weak reference, so we need to keep notifHandler alive ourselves
        // don't use @OnCreateEventLoop because it's unreliable
        OpModeManagerImpl.getOpModeManagerOfActivity(
            AppUtil.getInstance().activity
        ).registerListener(notifHandler)
        manager.register("/logs") {
            val sb = StringBuilder()
            sb.append("<!doctype html><html><head><title>Logs</title></head><body><ul>")
            val fs = Objects.requireNonNull(ROOT.listFiles())
            Arrays.sort(fs) { a: File, b: File ->
                b.lastModified().compareTo(a.lastModified())
            }
            for (f in fs) {
                sb.append("<li><a href=\"/logs/download?file=")
                sb.append(f.name)
                sb.append("\" download=\"")
                sb.append(f.name)
                sb.append("\">")
                sb.append(f.name)
                sb.append("</a></li>")
            }
            sb.append("</ul></body></html>")
            NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                NanoHTTPD.MIME_HTML, sb.toString()
            )
        }
        manager.register("/logs/download") { session: IHTTPSession ->
            val pairs =
                session.queryParameterString.split("&".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            if (pairs.size != 1) {
                return@register NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT, "expected one query parameter, got " + pairs.size
                )
            }
            val parts = pairs[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts[0] != "file") {
                return@register NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.BAD_REQUEST,
                    NanoHTTPD.MIME_PLAINTEXT, "expected file query parameter, got " + parts[0]
                )
            }
            val f = File(ROOT, parts[1])
            if (!f.exists()) {
                return@register NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_FOUND,
                    NanoHTTPD.MIME_PLAINTEXT, "file $f doesn't exist"
                )
            }
            NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK,
                "application/json", FileInputStream(f)
            )
        }
    }

    class LogFile(var opModeName: String?) {
        var version = "quickstart1 v2"
        var msInit = System.currentTimeMillis()
        var nsInit = System.nanoTime()
        var nsStart: Long = 0
        var nsStop: Long = 0
        var ticksPerRev = DriveConstants.TICKS_PER_REV
        var maxRpm = DriveConstants.MAX_RPM
        var runUsingEncoder = DriveConstants.RUN_USING_ENCODER
        var motorP = DriveConstants.MOTOR_VELO_PID!!.p
        var motorI = DriveConstants.MOTOR_VELO_PID!!.i
        var motorD = DriveConstants.MOTOR_VELO_PID!!.d
        var motorF = DriveConstants.MOTOR_VELO_PID!!.f
        var wheelRadius = DriveConstants.WHEEL_RADIUS
        var gearRatio = DriveConstants.GEAR_RATIO
        var trackWidth = DriveConstants.TRACK_WIDTH
        var kV = DriveConstants.kV
        var kA = DriveConstants.kA
        var kStatic = DriveConstants.kStatic
        var maxVel = DriveConstants.MAX_VEL
        var maxAccel = DriveConstants.MAX_ACCEL
        var maxAngVel = DriveConstants.MAX_ANG_VEL
        var maxAngAccel = DriveConstants.MAX_ANG_ACCEL
        var mecTransP: Double = SampleMecanumDrive.TRANSLATIONAL_PID.kP
        var mecTransI: Double = SampleMecanumDrive.TRANSLATIONAL_PID.kI
        var mecTransD: Double = SampleMecanumDrive.TRANSLATIONAL_PID.kD
        var mecHeadingP: Double = SampleMecanumDrive.HEADING_PID.kP
        var mecHeadingI: Double = SampleMecanumDrive.HEADING_PID.kI
        var mecHeadingD: Double = SampleMecanumDrive.HEADING_PID.kD
        var mecLateralMultiplier: Double = SampleMecanumDrive.LATERAL_MULTIPLIER
        var trackingTicksPerRev: Double = StandardTrackingWheelLocalizer.TICKS_PER_REV
        var trackingWheelRadius: Double = StandardTrackingWheelLocalizer.WHEEL_RADIUS
        var trackingGearRatio: Double = StandardTrackingWheelLocalizer.GEAR_RATIO
        var trackingLateralDistance: Double =
            StandardTrackingWheelLocalizer.LATERAL_DISTANCE
        var trackingForwardOffset: Double = StandardTrackingWheelLocalizer.FORWARD_OFFSET
        var logoFacingDirection = DriveConstants.LOGO_FACING_DIR
        var usbFacingDirection = DriveConstants.USB_FACING_DIR
        var nsTimes: MutableList<Long> = ArrayList()
        var targetXs: MutableList<Double> = ArrayList()
        var targetYs: MutableList<Double> = ArrayList()
        var targetHeadings: MutableList<Double> = ArrayList()
        var xs: MutableList<Double> = ArrayList()
        var ys: MutableList<Double> = ArrayList()
        var headings: MutableList<Double> = ArrayList()
        var voltages: MutableList<Double> = ArrayList()
        var driveEncPositions: MutableList<MutableList<Int>> = ArrayList()
        var driveEncVels: MutableList<MutableList<Int>> = ArrayList()
        var trackingEncPositions: MutableList<MutableList<Int>> = ArrayList()
        var trackingEncVels: MutableList<MutableList<Int>> = ArrayList()
    }
}