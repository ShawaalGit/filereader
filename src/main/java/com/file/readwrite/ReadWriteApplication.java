package com.file.readwrite;

import com.file.readwrite.entity.XmlData;
import com.file.readwrite.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@SpringBootApplication
public class ReadWriteApplication implements CommandLineRunner {
	@Autowired
	private FileService fileService;

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(ReadWriteApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * Getting the folder directory path with the help of FileSystemResource
		 * as the folder is with in the projects directory
		 **/
		String path = new FileSystemResource("").getFile().getAbsolutePath();
		System.out.println(path);
		List<XmlData> data = fileService.readFile(path + "//xml");
		fileService.createExcel(data,path);
	}
}
