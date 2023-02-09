/**
 * 
 */
package com.vguang.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.octo.captcha.service.image.ImageCaptchaService;


/**
 * @author Dingjz
 *
 * @date 2018年7月9日
 */
@Component
public class CaptchaManager {
	private final Logger log = LoggerFactory.getLogger(CaptchaManager.class);
//	@Autowired
//	private ImageCaptchaService captchaService;
	@Autowired
	private ImageCaptchaService captchaService;
	/**
	 * 生成且保存验证码
	 * 
	 * @param captchaId
	 * @return
	 * @throws IOException
	 */
	@Transactional
	public String generateCaptchaImage(String captchaId) throws IOException {
		BufferedImage buffImg = captchaService.getImageChallengeForID(captchaId);

		//String path = "C:\\nginx-1.12.2\\html\\DistributionCard\\captcha\\upgrade-test\\" + captchaId + ".png";
		String path = "/usr/local/nginx/html/captcha/upgrade-test/" + captchaId + ".png";
		log.info("验证码图片保存本地地址为：{}", path);
		ImageIO.write(buffImg, "png", new File(path));
		String reqPath = "captcha\\upgrade-test\\" + captchaId + ".png";
		log.info("返回的相对地址：{}", reqPath);
		return reqPath;
	}

	/**
	 * 校验验证码
	 * 
	 * @param captChaId
	 * @param value
	 * @return
	 */
	@Transactional
	public boolean validateValue(String captChaId, String value) {
		return captchaService.validateResponseForID(captChaId, value);
	}

}
