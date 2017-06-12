package com.fenghuo.notes.db

import com.fenghuo.notes.bean.Note

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

object IOHelper {

    fun read(path: String): String? {
        val file = File(path)
        if (file.exists()) {
            val fileReader: FileReader
            val reader: BufferedReader
            try {
                fileReader = FileReader(file)
                reader = BufferedReader(fileReader)
                var content = ""
                var str: String? = null
                str = reader.readLine()
                while (str != null) {
                    content += str
                    str = reader
                            .readLine()
                }
                reader.close()
                fileReader.close()
                return content
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
        return null
    }

    fun Write(path: String, note: Note): Boolean {
        val file = File(path)
        val file2 = File(file.parent)
        //如果目录不存在 则创建目录
        if (!file2.exists())
            file2.mkdirs()
        try {
            val writer = FileWriter(file, false)
            val bufferedWriter = BufferedWriter(writer)
            bufferedWriter.write(note.content)
            bufferedWriter.flush()
            bufferedWriter.close()
            writer.close()
            return true
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return false
    }

    fun Delete(filename: String) {
        val file = File(filename)
        if (file.exists()) {
            file.delete()
        }
    }

}
