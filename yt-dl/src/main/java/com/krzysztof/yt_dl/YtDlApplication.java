package com.krzysztof.yt_dl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

	record DownloadRequest(String url, boolean wideo, int quality, int quality_sound, String path, boolean playlist) {}

	@PostMapping("/download")
	public SseEmitter downloadStream(@RequestBody DownloadRequest req) {
		SseEmitter emitter = new SseEmitter(0L); // 0 = bez limitu czasu

		new Thread(() -> {
			try {
				List<String>  command = new ArrayList<>();
				command.add("C:\\Users\\KrzysztofWojciechDab\\Documents\\projekty\\yt-downloader\\yt downloader-api\\yt-dlp.exe");
				if (req.quality()==0){
					command.add("-f");
					command.add("bestvideo+bestaudio/best");
					command.add("--merge-output-format");
					command.add("mp4");
				}else{
					command.add("-f");
					command.add("bestvideo[height<=" + req.quality() + "]+bestaudio/best[height<=" + req.quality() + "]");
					command.add("--merge-output-format");
					command.add("mp4");
				}

				if (req.quality_sound()!=0){
					command.add("--audio-quality");
					command.add(String.valueOf(req.quality_sound()));
				}
				command.add("--js-runtimes");
				command.add("node");
				command.add("-o");
				if(!req.path().equals("0")){
					command.add(req.path()+"\\%(title)s.%(ext)s");
				}else{
					command.add("C:/Users/KrzysztofWojciechDab/Downloads/%(title)s.%(ext)s");
				}

				if (!req.wideo()) {
					command.add("-x");
					command.add("--audio-format");
					command.add("mp3");
				}

				if (req.playlist()) {
					command.add("--yes-playlist");
				}else{
					command.add("--no-playlist");
				}
				command.add(req.url());// Twoja istniejąca logika budowania komendy

				ProcessBuilder pb = new ProcessBuilder(command);
				pb.redirectErrorStream(true);
				Process process = pb.start();

				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						emitter.send(line); // wysyłamy KAŻDĄ linię od razu, nie czekamy na koniec
					}
				}

				int exitCode = process.waitFor();
				emitter.send(exitCode == 0 ? "DONE" : "ERROR");
				emitter.complete();

			} catch (Exception e) {
				try { emitter.send("ERROR: " + e.getMessage()); } catch (Exception ignored) {}
				emitter.completeWithError(e);
			}
		}).start();

		return emitter;
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