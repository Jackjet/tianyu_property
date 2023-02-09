package com.vguang.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;




public class PictureUtil {

	public static String changePictureSuffix(String inputpath,String outputpath){
		
		BufferedImage bufferedImage;
	    
	    try {
	      //read image file
	      bufferedImage = ImageIO.read(new File(inputpath));
	 
	      // create a blank, RGB, same width and height, and a white background
	      BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
	            bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
	 
	     //TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
	 
	      newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
	 
	      // write to jpeg file
	      ImageIO.write(newBufferedImage, "jpg", new File(outputpath));
	 
	      System.out.println("Done");
	 
	    } catch (IOException e) {
	 
	      e.printStackTrace();
	 
	    }
		
		return outputpath;
		
		
		
	}
	
	public static String B64Picture(String imgFile){
	        InputStream in = null;
	        byte[] data = null;
	        //读取图片字节数组
	        try{
	            in = new FileInputStream(imgFile);
	            data = new byte[in.available()];
	            in.read(data);
	            in.close();
	        }catch (IOException e){
	            e.printStackTrace();
	        }
	        //对字节数组Base64编码
	        BASE64Encoder encoder = new BASE64Encoder();
	        //返回Base64编码过的字节数组字符串
	        return encoder.encode(data);
		
	}
	
}
