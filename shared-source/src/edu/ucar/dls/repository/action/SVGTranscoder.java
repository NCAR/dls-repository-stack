/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.repository.action;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;



/**
  Action class for rendering the repository details page. Very simple page that just loads the
  repositories indexSummary.xml
 */
public final class SVGTranscoder extends Action {
	
	@Override
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {
		
		String output_format= req.getParameter("output_format");
		String svg_style_sheet_url= req.getParameter("svg_style_sheet_url");

		String contentType = null;
		String fileExtension = null;
		DestinationType destinationType= null;
		
		if(output_format.equals("pdf"))
		{
			contentType = "application/pdf";
			fileExtension = "pdf";
			destinationType = DestinationType.PDF;
		}
		else if(output_format.equals("png"))
		{
			contentType = "image/png";
			fileExtension = "png";
			destinationType = DestinationType.PNG;
		}
		else if(output_format.equals("jpg"))
		{
			contentType = "image/jpg";
			fileExtension = "jpg";
			destinationType = DestinationType.JPEG;
		}
		else
		{
			return null;
		}
		response.setContentType(contentType);
	    response.setHeader("Content-Disposition",
	    		String.format("attachment;filename=chart.%s",fileExtension) );
	    Source src = new StreamSource(new java.io.StringReader(req.getParameter("svg")));
	    // Save this SVG into a file (required by SVG -> PDF transformation process)
	    File svgFile = File.createTempFile("analytics-", ".svg");
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    FileOutputStream fOut = new FileOutputStream(svgFile);
	    try { transformer.transform(src, new StreamResult(fOut)); }
	    finally { fOut.close(); }
	    
	    
	    File outputFile = File.createTempFile("result", String.format(".%s", fileExtension));
	    SVGConverter converter = new SVGConverter();
	    converter.setDestinationType(destinationType);
	    if(svg_style_sheet_url!=null && svg_style_sheet_url!="")
	    {
	    	converter.setUserStylesheet(svg_style_sheet_url);
	    }
	    converter.setBackgroundColor(Color.WHITE);
	    converter.setSources(new String[] { svgFile.toString() });
	    converter.setDst(outputFile);
	    converter.execute();
	    
	    FileInputStream in = 
      		new FileInputStream(outputFile);
	    
        byte[] outputByte = new byte[4096];
        ServletOutputStream out = response.getOutputStream();
        //copy binary content to output stream
        while(in.read(outputByte, 0, 4096) != -1){
        	out.write(outputByte, 0, 4096);
        }
        
        // Close everything
        in.close();
        out.flush();
        out.close();
        return null;
	}
	
	
}

