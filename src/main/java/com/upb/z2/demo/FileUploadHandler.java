package com.upb.z2.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadHandler extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private final String UPLOADDIRECTORY = "/usr/local/apache−tomcat−9.0.12/uploads";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// process only if its multipart content
		if(ServletFileUpload.isMultipartContent(request))
		{
			try {
				List<FileItem>multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				File temp = null;
				File encrypted = null;
				String key = null;
				
				for(FileItem item : multiparts)
				{
					if(!item.isFormField())
					{
						String name = new File(item.getName()).getName();
						temp = new File(UPLOADDIRECTORY + File.separator + name);
						item.write(temp);
						encrypted = new File(UPLOADDIRECTORY + File.separator + name + ".enc");
					}
					else
					{
						if(item.getFieldName().equals("fname"))
						{
							key=item.getString();
						}
					}
				}
				//String key=”k2l4u6c8j0e2t4o6”
				CryptoUtils.encrypt(key,temp,encrypted);
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
}
