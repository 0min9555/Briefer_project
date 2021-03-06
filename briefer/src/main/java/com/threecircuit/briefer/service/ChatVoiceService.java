package com.threecircuit.briefer.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatVoiceService {

	@Value("${voice.clientId}")
	String clientId;
	
	@Value("${voice.clientSecret}")
	String clientSecret;
	
	@Value("${voicefile.SavePath}")
	String voicefilePath;

	@Value("${voicefile.folder}")
	String voicefileFolder;

	public String clovaSpeechToText(String vocFile, String language) {

		String result = "";
		try {
			File voiceFile = new File(vocFile);

			String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
			URL url = new URL(apiURL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

			OutputStream outputStream = conn.getOutputStream();
			FileInputStream inputStream = new FileInputStream(voiceFile);
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.flush();
			inputStream.close();
			BufferedReader br = null;
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else { // 오류 발생
				System.out.println("error!!!!!!! responseCode= " + responseCode);
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			String inputLine;

			if (br != null) {
				StringBuffer response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				System.out.println(response.toString());

				result = jsonToString(response.toString());

			} else {
				System.out.println("error !!!");
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	public String jsonToString(String jsonStr) {

		String text = "";

		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonStr);
			text = (String) jsonObj.get("text");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return text;

	}

	public String clovaTextToSpeech(String txtInput, String voice) {

		String voiceFileName = "";

		try {

			String text = URLEncoder.encode(txtInput, "UTF-8"); // 13자
			String apiURL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			// post request
			String postParams = "speaker=" + voice + "&volume=0&speed=0&pitch=0&format=mp3&text=" + text;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			
//			System.out.println("Filesave: "+ voicefilePath);
			
			if (responseCode == 200) { // 정상 호출
				InputStream is = con.getInputStream();
				int read = 0;
				byte[] bytes = new byte[1024];
				
				String tempname = Long.valueOf(new Date().getTime()).toString();

				voiceFileName = "tts_" + tempname + ".mp3";
				
//				System.out.println("Filesave: "+ voicefilePath + voiceFileName);

				File f = new File(voicefilePath + voiceFileName); // 반환값(저장되는 파일명)
				
				f.createNewFile();
				OutputStream outputStream = new FileOutputStream(f);
				while ((read = is.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				is.close();
			} else { // 오류 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				System.out.println(response.toString());
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return voiceFileName;
	}
	
	public String clovaTextToSpeech2(String txtInput, String voice) {

		String voiceFileName = "";

		try {

			String text = URLEncoder.encode(txtInput, "UTF-8"); // 13자
			String apiURL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			// post request
			String postParams = "speaker=" + voice + "&volume=0&speed=0&pitch=0&format=mp3&text=" + text;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			
			
			if (responseCode == 200) { // 정상 호출
				InputStream is = con.getInputStream();
				int read = 0;
				byte[] bytes = new byte[1024];
				
				String tempname = Long.valueOf(new Date().getTime()).toString();

				voiceFileName = "tts_" + tempname + ".mp3";

				File f = new File(voicefilePath + voiceFileName); // 반환값(저장되는 파일명)
				
				f.createNewFile();
				OutputStream outputStream = new FileOutputStream(f);
				while ((read = is.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				is.close();
			} else { // 오류 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				System.out.println(response.toString());
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return voicefilePath + voiceFileName;
	}

	/*
	 * public String chatbotTextToSpeech(String message) {
	 * 
	 * String voiceFileName = "";
	 * 
	 * try {
	 * 
	 * String text = URLEncoder.encode(message, "UTF-8"); // 13자 String apiURL =
	 * "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts"; URL url = new
	 * URL(apiURL); HttpURLConnection con = (HttpURLConnection)url.openConnection();
	 * con.setRequestMethod("POST");
	 * con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
	 * con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret); // post request
	 * String postParams = "speaker=nara&volume=0&speed=0&pitch=0&format=mp3&text="
	 * + text; con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream()); wr.writeBytes(postParams);
	 * wr.flush(); wr.close(); int responseCode = con.getResponseCode();
	 * BufferedReader br; if(responseCode==200) { // 정상 호출 InputStream is =
	 * con.getInputStream(); int read = 0; byte[] bytes = new byte[1024]; // 랜덤한
	 * 이름으로 mp3 파일 생성 String tempname = Long.valueOf(new
	 * Date().getTime()).toString();
	 * 
	 * voiceFileName = "tts_" + tempname + ".mp3";
	 * 
	 * File f = new File("c:/ai/" + voiceFileName); //반환값(저장되는 파일명)
	 * 
	 * f.createNewFile(); OutputStream outputStream = new FileOutputStream(f); while
	 * ((read =is.read(bytes)) != -1) { outputStream.write(bytes, 0, read); }
	 * is.close(); } else { // 오류 발생 br = new BufferedReader(new
	 * InputStreamReader(con.getErrorStream())); String inputLine; StringBuffer
	 * response = new StringBuffer(); while ((inputLine = br.readLine()) != null) {
	 * response.append(inputLine); } br.close();
	 * System.out.println(response.toString()); } } catch (Exception e) {
	 * System.out.println(e); }
	 * 
	 * return voiceFileName; }
	 */

}
