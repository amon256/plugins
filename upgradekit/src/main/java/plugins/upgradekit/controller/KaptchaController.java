/**
 * 
 */
package plugins.upgradekit.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;

/**
 * @author fengmengyue
 *
 */
@Controller
public class KaptchaController {

	@Autowired
	private Producer producer;
	
	@RequestMapping(value="/kaptcha")
	public void kaptcha(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setDateHeader("Expires", 0); 
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");  
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");  
        response.setHeader("Pragma", "no-cache");  
        response.setContentType("image/jpeg");  
        String capText = producer.createText();  
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);  
        BufferedImage bi = producer.createImage(capText);  
        ServletOutputStream out = response.getOutputStream();  
        ImageIO.write(bi, "jpg", out);  
	}
}
