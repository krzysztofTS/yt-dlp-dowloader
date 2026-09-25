package com.krzysztof.yt_dl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class YtDlApplication {
	public static void main(String[] args) {
		SpringApplication.run(YtDlApplication.class, args);
	}
}

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // dla developmentu; docelowo podaj konkretny adres
class DownloadController {

	record DownloadRequest(String url, boolean wideo, boolean best, boolean playlist) {}

	@PostMapping("/download")
	public ResponseEntity<String> download(@RequestBody DownloadRequest req) {
		try {
			List<String> command = new ArrayList<>();
			command.add("C:\\Users\\KrzysztofWojciechDab\\Documents\\projekty\\yt-downloader\\yt downloader-api\\yt-dlp.exe");
			if (req.best()){
				command.add("-f");
				command.add("bestvideo+bestaudio/best");
			}else{

			}
			command.add("--merge-output-format");
			command.add("mp4");
			command.add("--js-runtimes");
			command.add("node");
			command.add("-o");
			command.add("C:/Users/KrzysztofWojciechDab/Downloads/%(title)s.%(ext)s");

			if (!req.wideo()) {
				command.add("-x");
				command.add("--audio-format");
				command.add("mp3");
			}
			if (req.playlist()) {
				command.add("--yes-playlist");
			}

			command.add(req.url());

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process process = pb.start();

			StringBuilder output = new StringBuilder();
			try (var reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				return ResponseEntity.internalServerError().body("Błąd yt-dlp:\n" + output);
			}

			return ResponseEntity.ok("Download complete:\n" + output);

		} catch (Exception e) {
			String error = e.getMessage() != null ? e.getMessage() : e.toString();
			return ResponseEntity.internalServerError().body(error);
		}
	}


	@PostMapping("/update")
	public ResponseEntity<String> update() {
		try {
			List<String> command = new ArrayList<>();
			command.add("C:\\Users\\KrzysztofWojciechDab\\Documents\\projekty\\yt-downloader\\yt downloader-api\\yt-dlp.exe");
			command.add("-U");

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			Process process = pb.start();

			StringBuilder output = new StringBuilder();
			try (var reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				return ResponseEntity.internalServerError().body("Błąd yt-dlp:\n" + output);
			}

			return ResponseEntity.ok("Update complete:\n" + output);

		} catch (Exception e) {
			String error = e.getMessage() != null ? e.getMessage() : e.toString();
			return ResponseEntity.internalServerError().body(error);
		}
	}

}