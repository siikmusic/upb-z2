package com.upb.z2.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@MultipartConfig
public class FileUploadHandler extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private final String UPLOADDIRECTORY = "C:\\Users\\Gabor\\Documents\\fei\\UPB";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		// process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request))
		{
			try {
//				long start = System.nanoTime();
				List<FileItem>multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				File temp = null;
				File encrypted = null;
				String key = null;
				boolean isEncrypt = true;
				String name = null;

				for(FileItem item : multiparts)
				{
					if(!item.isFormField())
					{
						name = new File(item.getName()).getName();
						temp = new File(UPLOADDIRECTORY + File.separator + name);
						item.write(temp);
					}
					else
					{
						if(item.getFieldName().equals("fname"))
						{
							key=item.getString();
						}
						else if(item.getFieldName().equals("mode")){
							if(item.getString().equals("decrypt")){
								isEncrypt = false;
							}
							else{
								isEncrypt = true;
							}
						}
					}
				}
				//String key=”k2l4u6c8j0e2t4o6”
				if(isEncrypt){
					encrypted = new File(UPLOADDIRECTORY + File.separator + name + ".enc");
					Key key2 = getKeyFromKeyGenerator("AES", 256);
					// key to string
					String encodedKey = Base64.getEncoder().encodeToString(key2.getEncoded());
					//save string key
					byte[] arr = encodedKey.getBytes();
					Path path = Paths.get(UPLOADDIRECTORY+ File.separator + "key.txt");
					Files.write( path , arr);

					CryptoUtils.encrypt(key2,temp,encrypted);
				} else{
					byte[] decodedKey = Base64.getDecoder().decode(key);
					Key originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
					name = removeExtension(name);

					encrypted = new File(UPLOADDIRECTORY + File.separator + name);
					CryptoUtils.decrypt(originalKey,temp,encrypted);
				}

//				long finish = System.nanoTime();
//				long timeElapsed = finish - start;
//				double elapsedTimeInSecond = (double) timeElapsed / 1_000_000_000;
//				System.out.println(elapsedTimeInSecond);

				//File uploaded successfully
				response.setContentType("text/plain");
				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(encrypted);
				byte[] buffer = new byte[4096];
				int length;
				while((length = in.read(buffer)) > 0)
				{
					out.write(buffer, 0, length);
				}

				in.close();
				out.flush();
				request.setAttribute("message","File Uploaded Successfully");
			}
			catch(Exception ex)
			{
				request.setAttribute("message", "File Enc/Dec Failed due to" + ex);
			}
		}
		else
		{
			request.setAttribute("message", "Sorry this Servlet only handles file upload request");
		}
	}

	private static Key getKeyFromKeyGenerator(String cipher, int keySize) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(cipher);
		keyGenerator.init(keySize);
		return keyGenerator.generateKey();
	}

	private static String removeExtension(String file){
		int extensionIndex = file.lastIndexOf(".");
		if (extensionIndex == -1)
			return file;
		return file.substring(0, extensionIndex);
	}

}
