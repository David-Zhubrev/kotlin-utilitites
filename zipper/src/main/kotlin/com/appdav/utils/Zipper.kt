package com.appdav.utils

import java.io.File
import java.io.InputStream

/**
 * Zip/unzip archives
 */
object Zipper {

    /**
     * Unzip zipFile into destination folder.
     * @param zipFile zip file to unzip
     * @param destination folder where unzipped file should be put. If destination folder does not exist, it will be created
     */
    fun unzip(zipFile: File, destination: File) {
        ZipHandler.unzip(zipFile.inputStream(), destination)
    }

    /**
     * Use inputStream acquired from zip-file to unzip it into destination folder. Basically, it is a convenience overload for jarred resources that are usually acquired as an InputStream
     * @param inputStream zip-file input stream (will be auto-closed after unzipping)
     * @param destination folder where unzipped file should be put. If destination folder does not exist, it will be created
     */
    fun unzip(inputStream: InputStream, destination: File) {
        ZipHandler.unzip(inputStream, destination)
    }

    /**
     * Zip collection of files into destination zip-file. This operation is recursive for folders, meaning that all the folders will preserve its hierarchy.
     * @param destination zip archive where provided files will be put. If parent folder does not exist, creates the whole hierarchy
     * @param files files to put into zip archive. Files will be put into the root of the target zip archive, folders will preserve its hierarchy
     * @throws IllegalStateException if destination file already exists
     */
    fun zip(destination: File, files: Collection<File>): File {
        ZipHandler.zip(files, destination)
        return destination
    }

    fun zip(destination: File, vararg files: File): File {
        ZipHandler.zip(files.toList(), destination)
        return destination
    }
}
