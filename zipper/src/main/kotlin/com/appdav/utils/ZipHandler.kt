package com.appdav.utils

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.IllegalStateException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal object ZipHandler {

    fun unzip(zipArchiveInputStream: InputStream, unzipPath: File) {
        ZipInputStream(zipArchiveInputStream).use { zis ->
            var zipEntry = zis.nextEntry
            while (zipEntry != null) {
                val isDirectory = zipEntry.isDirectory
                val newPath = unzipPath.toPath().resolve(zipEntry.name).normalize()
                if (!newPath.startsWith(unzipPath.toPath())) {
                    throw ZipSlipException(zipEntry.name)
                }
                if (isDirectory) {
                    Files.createDirectories(newPath)
                } else {
                    if (newPath.parent != null) {
                        if (Files.notExists(newPath.parent)) {
                            Files.createDirectories(newPath.parent)
                        }
                    }
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING)
                }
                zipEntry = zis.nextEntry
            }
            zis.closeEntry()
        }
    }

    fun zip(files: Collection<File>, destination: File) {
        if (destination.isFile){
            throw IllegalStateException("$destination file already exists")
        }
        if (destination.isDirectory) {
            throw IllegalStateException("${destination.absolutePath} is not a file")
        }
        if (!destination.isFile) {
            if (!destination.parentFile.exists()) {
                destination.parentFile.mkdirs()
            }
            destination.createNewFile()
        }
        ZipOutputStream(FileOutputStream(destination)).use { zipOutputStream ->
            for (file in files) {
                if (file.isDirectory) {
                    val dirPath = file.toPath()
                    Files.walkFileTree(dirPath, object : SimpleFileVisitor<Path>() {
                        override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                            if (file == null) return FileVisitResult.CONTINUE
                            val targetFile = dirPath.parent.relativize(file)
                            zipOutputStream.putNextEntry(ZipEntry(targetFile.toString()))
                            Files.readAllBytes(file).also { bytes ->
                                zipOutputStream.write(bytes)
                                zipOutputStream.closeEntry()
                            }
                            return FileVisitResult.CONTINUE
                        }
                    })
                } else if (file.isFile) {
                    zipOutputStream.putNextEntry(ZipEntry(file.name))
                    Files.readAllBytes(file.toPath()).also { bytes ->
                        zipOutputStream.write(bytes)
                        zipOutputStream.closeEntry()
                    }
                }
            }

        }
    }

}
