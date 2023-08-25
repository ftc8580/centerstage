package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.robotcore.internal.system.AppUtil
import java.io.File

/**
 * Utility functions for log files.
 */
object LoggingUtil {
    private val ROAD_RUNNER_FOLDER = File(AppUtil.ROOT_FOLDER.toString() + "/RoadRunner/")
    private const val LOG_QUOTA = (25 * 1024 * 1024).toLong() // 25MB log quota

    private fun buildLogList(logFiles: MutableList<File>, dir: File) {
        val files = dir.listFiles() ?: return

        for (file in files) {
            if (file.isDirectory) {
                buildLogList(logFiles, file)
            } else {
                logFiles.add(file)
            }
        }
    }

    private fun pruneLogsIfNecessary() {
        val logFiles: MutableList<File> = ArrayList()
        buildLogList(logFiles, ROAD_RUNNER_FOLDER)
        logFiles.sortWith { lhs: File, rhs: File ->
            lhs.lastModified().compareTo(rhs.lastModified())
        }
        var dirSize: Long = 0
        for (file in logFiles) {
            dirSize += file.length()
        }
        while (dirSize > LOG_QUOTA) {
            if (logFiles.size == 0) break
            val fileToRemove = logFiles.removeAt(0)
            dirSize -= fileToRemove.length()
            fileToRemove.delete()
        }
    }

    /**
     * Obtain a log file with the provided name
     */
    fun getLogFile(name: String): File {
        ROAD_RUNNER_FOLDER.mkdirs()
        pruneLogsIfNecessary()
        return File(ROAD_RUNNER_FOLDER, name)
    }
}