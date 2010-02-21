package pl.lodz.p.cm.ctp.npvrd;

import java.io.*;
import java.net.*;

public class VlmManager {
	
	private VlmConfig myConfig;

	public VlmManager(VlmConfig config) {
		this.myConfig = config;
	}
	
	public void createNewVod(String name, String filePath) {
		name = myConfig.pvrPrefix + name;
		
		try {
			InetAddress addr = InetAddress.getByName(myConfig.host);
			SocketAddress sockAddr = new InetSocketAddress(addr, myConfig.port);
			Socket socket = new Socket();
			int timeoutMs = 2000;
			
			socket.connect(sockAddr, timeoutMs);
			
			OutputStream oStream = socket.getOutputStream();
			InputStream iStream = socket.getInputStream();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(iStream));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(oStream));
			
			char[] buf = new char[20];
			String prompt = null;
			in.read(buf, 0, 10);
			prompt = new String(buf);
			if (prompt.startsWith("Password: ")) {
				out.write(myConfig.password + "\n");
				out.flush();
				readToVoid(in, 8);
				prompt = in.readLine();
				if (prompt.startsWith("Wrong")) { // VLM Answer: Wrong password.
					Npvrd.error("VLM Manager: wrong password.");
				} else if (prompt.startsWith("Welco")) { // VLM Answer: Welcome, Master
					//Npvrd.log("VLM Manager: logged in.");
					in.read(buf, 0, 2);
					prompt = new String(buf);
					//System.out.println(prompt);
					if (prompt.startsWith("> ")) {
						out.write("new " + name + " vod enabled" + "\r\n");
						out.flush();
						if (prompt.startsWith("> ")) {
							//Npvrd.log("VLM manager: successfully added new VOD: " + name);
							//System.out.println(prompt);
							if (prompt.startsWith("> ")) {
								out.write("setup " + name + " input \"" + filePath + "\"" + "\r\n");
								out.flush();
								in.read(buf, 0, 2);
								prompt = new String(buf);
								//System.out.println(prompt);
								if (prompt.startsWith("> ")) {
									//Npvrd.log("VLM manager: successfully linked the file to " + name);
									Npvrd.log("VLM manager: successfully added new VOD: " + name);
								}
							}
						} else {
							Npvrd.error("VLM manager: strange prompt after adding VOD: " + prompt);
						}
					} else {
						Npvrd.error("VLM manager: Strange prompt: " + prompt);
					}
				} else {
					Npvrd.error("VLM manager: Strange response to password: " + prompt);
				}
			} else {
				Npvrd.error("VLM manager: unknown prompt: " + prompt);
			}
			
			out.write("quit" + "\n");
			out.flush();
			socket.close();
		} catch (UnknownHostException e) {
			Npvrd.error("VLM Manager: Unknown host address: " + e.getMessage());
		} catch (IOException e) {
			Npvrd.error("VLM Manager: Could not create socket: " + e.getMessage());
		}
		
	}
	
	private void readToVoid(Reader reader, int len) {
		char[] buf = new char[len];
		try {
			reader.read(buf, 0, len);
		} catch (IOException e) {

		}
	}
	
}
