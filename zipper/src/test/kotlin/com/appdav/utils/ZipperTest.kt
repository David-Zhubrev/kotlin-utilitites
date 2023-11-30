package com.appdav.utils

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream
import kotlin.io.path.toPath

class ZipperTest {

    private fun getArchiveStream() = this::class.java.getResourceAsStream("/test.zip")

    @Test
    fun unzip() {
        val archive = getArchiveStream()
        val testDir = getTestDir()
        Zipper.unzip(archive, testDir)
        val fileContent = File(testDir, "test.txt").readText(Charsets.UTF_8)
        assertEquals(fileContent, "test string")
        archive.close()
    }

    @Test
    fun testUnzip() {
        val stream = getArchiveStream()
        val testDir = getTestDir()
        val archiveFile = File(testDir, "testarchive.zip").apply {
            parentFile.mkdirs()
            createNewFile()
        }
        FileOutputStream(archiveFile).use {
            it.write(stream.readBytes())
        }
        stream.close()
        Zipper.unzip(archiveFile, testDir)
        val fileContent = File(testDir, "test.txt").readText()
        assertEquals(fileContent, "test string")
    }

    //TODO: this test sucks, rewrite
    @Test
    fun testZip(){
        val testDir = getTestDir().apply { mkdirs() }
        fun createFile(name: String, dir: File = testDir) =
            File(dir, name).apply { parentFile.mkdirs(); createNewFile(); writeText("file $name content") }
        val file1 = createFile("file1")
        val file2 = createFile("file2", File(testDir, "folder1"))
        val file3 = createFile("file3", File(testDir, "folder2"))
        val folder = File(testDir, "folder3").apply { mkdirs() }
        File(folder, "file4").apply { createNewFile() }
        File(folder, "file5").apply { createNewFile() }
        val outputZip = File(testDir, "output.zip")
        Zipper.zip(outputZip, file1, file2, file3, folder)
        val outputDir = File(testDir, "output")
        Zipper.unzip(outputZip, outputDir)
        fun assertFileExists(file: File){
            assert(file.exists() && file.readText() == "file ${file.nameWithoutExtension} content")
        }
        assertFileExists(File(outputDir, "file1"))
        assertFileExists(File(outputDir, "file2"))
        assertFileExists(File(outputDir, "file3"))
        assert(File(outputDir, "folder3\\file4").exists())
        assert(File(outputDir, "folder3\\file5").exists())
    }

    @AfterEach
    fun cleanTestDir() {
        getTestDir().deleteRecursively()
    }

    private companion object {

        fun getTestDir(): File =
            File(this::class.java.protectionDomain.codeSource.location.toURI().toPath().toFile(), "test_dir")

    }
}
