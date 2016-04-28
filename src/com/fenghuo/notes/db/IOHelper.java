package com.fenghuo.notes.db;

import com.fenghuo.notes.bean.Note;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IOHelper {

	public static String read(String path) {
		File file = new File(path);
		if (file.exists()) {
			FileReader fileReader;
			BufferedReader reader;
			try {
				fileReader = new FileReader(file);
				reader = new BufferedReader(fileReader);
				String content = "";
				String str = null;
				for (str = reader.readLine(); str != null; str = reader
						.readLine()) {
					content += str;
				}
				reader.close();
				fileReader.close();
				return content;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean Write(String path, Note note) {
		File file=new File(path);
		File file2=new File(file.getParent());
		//如果目录不存在 则创建目录 
		if(!file2.exists())
			file2.mkdirs();
		try {
			FileWriter writer = new FileWriter(file, false);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(note.getContent());
			bufferedWriter.flush();
			bufferedWriter.close();
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static void Delete(String filename){
		File file=new File(filename);
		if(file.exists()){
			file.delete();
		}
	}

}
