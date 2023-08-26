package fakes.util

import com.qualcomm.robotcore.robocol.TelemetryMessage
import org.firstinspires.ftc.robotcore.internal.opmode.TelemetryImpl

class FakeTelemetry : TelemetryImpl(null) {
    private var outputToStdout = false
    private var currentTelemetryData: String? = null

    fun setOutputToStdout(outputToStdout: Boolean) {
        this.outputToStdout = outputToStdout
    }

    fun getCurrentTelemetryData(): String? {
        return currentTelemetryData
    }

    fun markDirty() {
        isDirty = true
    }

    fun markClean() {
        isDirty = false
    }

    fun isDirty(): Boolean {
        return isDirty
    }

    // This is copied out of the SDK, to mimic the ordering/presentation
    // that is in real telemetry. This allows us to write tests that has data
    // show up in the same way it does on the DS phone.
    override fun tryUpdate(updateReason: UpdateReason): Boolean {
        synchronized(theLock) {
            var result = false
            val intervalElapsed =
                transmissionTimer.milliseconds() > msTransmissionInterval
            val wantToTransmit =
                updateReason == UpdateReason.USER || updateReason == UpdateReason.LOG || updateReason == UpdateReason.IFDIRTY && isDirty() /* || log.isDirty() */
            val recompose = (updateReason == UpdateReason.USER
                    || isDirty()) // only way we get dirty is from a previous UpdateReason.USER
            if (wantToTransmit) {
                // Evaluate any delayed actions we've been asked to do
                for (action in actions) {
                    action.run()
                }

                // Build an object to cary our telemetry data
                val transmitter = TelemetryMessage()
                saveToTransmitter(recompose, transmitter)

                // Transmit if there's anything to transmit
                if (transmitter.hasData()) {
                    val output = StringBuilder()
                    val toPrint =
                        transmitter.dataStrings
                    val lines: Set<Map.Entry<String, String>> =
                        toPrint.entries
                    for ((_, value) in lines) {
                        output.append(value)
                        output.append("\n")
                    }
                    currentTelemetryData = output.toString()
                    if (outputToStdout) {
                        println("\n[Telemetry Start] ------------------------\n")
                        println(currentTelemetryData)
                        println("\n[Telemetry End] ------------------------\n")
                    }
                }

                // We've definitely got nothing lingering to transmit
                // this.log.markClean();
                markClean()

                // Update for the next time around
                transmissionTimer.reset()
                result = true
            }

            // In all cases, if it's a user requesting the update, then the next add clears
            if (updateReason == UpdateReason.USER) {
                // Postponing the clear vs doing it right now allows future log updates to
                // transmit before the user does more addData()
                clearOnAdd = isAutoClear()
            }
            return result
        }
    }
}