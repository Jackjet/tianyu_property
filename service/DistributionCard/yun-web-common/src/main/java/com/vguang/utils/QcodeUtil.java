package com.vguang.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vguang.system.SpringContextUtil;
import com.vguang.system.SystemConfigs;
import com.vguang.utils.auth.AuthManager;
import com.vguang.utils.encrypt.B64Util;
import com.vguang.utils.encrypt.CRCUtil;
import com.vguang.utils.encrypt.HMACUtil;

public class QcodeUtil {
	private static final Logger log = LoggerFactory.getLogger(QcodeUtil.class);
	@Autowired
	static
	AuthManager authManager;
	/**
	 * 生成二维码图片
	 * @param content
	 * @param format 图片格式 png、jpeg
	 * @param width
	 * @param height
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] generate(String content,String format, int width, int height) throws UnsupportedEncodingException{
		Hashtable<Object, Object> hints = new Hashtable<Object, Object>(); 
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); 
		String str = new String(content.getBytes("UTF-8"),"iso-8859-1");
    	BitMatrix byteMatrix = null;
    	byte[] bytes = null;
		try {
			byteMatrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE, width, height);
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
	    	MatrixToImageWriter.writeToStream(byteMatrix, format, bao);
	    	System.out.println(bao.toByteArray());
	    	
	    	bytes = bao.toByteArray();
		} catch (WriterException | IOException e) {
			log.error("===>生成二维码失败" + e);
			e.printStackTrace();
		}
    	
    	return bytes;
	}
	
	/**
	 * 生成通行二维码内容
	 * @param version
	 * @param personid 	用户id
	 * @param btime 	开始时间
	 * @param exptime 	二维码有效时间
	 * @param akey 		用户认证密钥
	 * @param vtype 	3:在线优先 
	 * @param custdata 	自定义数据长度
	 * @param json 
	 * @return
	 */
	public static String genQrcode(short version, long personid, 
			long btime, int exptime, 
			String akey, short vtype, short custLenth, String custDesc){
		String qrcode = null;
		log.info("begintime:{}",btime);
		log.info("exptime:{}",exptime);
		byte[] bversion, buid, bbtime, bexptime, bvtype, bcust, hbytes, crcbytes, bcrc,byteArray;
		bversion = ByteUtil.short2Hbytes(version);
		buid = ByteUtil.long2H6bytes(personid);
		bbtime = ByteUtil.long2H6bytes(btime);
		bexptime = ByteUtil.int2Hbytes(exptime);
		bvtype = ByteUtil.short2Hbytes(vtype);
		bcust = ByteUtil.short2Hbytes(custLenth);
		byteArray = custDesc.getBytes();
		//拼接成HMAC加密的字节数组
		hbytes = ByteUtil.concatBytes(bversion, buid, bbtime, bexptime, bvtype, bcust,byteArray);
		
		byte[] hmacs = null;
		try {
			hmacs = HMACUtil.encrypt(hbytes, akey);
			//拼接成CRC校验的字节数组,HMAC填补4字节0
			crcbytes = ByteUtil.concatBytes(hbytes, hmacs, ByteUtil.int2Hbytes(0));
			long crc = CRCUtil.encrypt(crcbytes);
			bcrc = ByteUtil.long2H4bytes(crc);
			log.info("hmacs==============="+hmacs);
			
			String cc = HexUtil.bytesToHexFun3(bcrc);
			String dd = HexUtil.bytesToHexFun3(crcbytes);
			String vv = HexUtil.bytesToHexFun3(hbytes);
			String gg = HexUtil.bytesToHexFun3(hmacs);
			log.info("bcrc======================"+cc);
			log.info("crcbytes=================="+dd);
			log.info("hbytes===================="+vv);
			log.info("hmacs===================="+gg);
			//门禁二维码格式：&mj
			qrcode = "&mj" + B64Util.encode(ByteUtil.concatBytes(crcbytes, bcrc)) + "@123";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return qrcode;
	}
	
	public static String offLineCode(int Secretkeynum,String OrgNumber,String CodeSystem,
			String personId,long btime, long etime, byte[]  hexdevicenumber,String authKey, int applyType){
		String offCode= null;
		byte[] bSecretkeynum, bOrgNumberLength, bOrgNumber, bCodeSystemLength, bCodeSystem, bpersonIdLength, bpersonId, bbtime, betime,hbytes
		,bhexdevicenumberLength,bapplyType;
		
		 bSecretkeynum = ByteUtil.int2Hbytes1byte(Secretkeynum);//秘钥号
		
		 bOrgNumberLength = ByteUtil.int2Hbytes1byte(OrgNumber.getBytes().length);//组织编号长度
		 bOrgNumber= ByteUtil.hexStr2Bytes(ByteUtil.str2HexStr(OrgNumber));//组织编号
		
		 bCodeSystemLength = ByteUtil.int2Hbytes1byte(CodeSystem.getBytes().length);//组织编号长度
		 bCodeSystem= ByteUtil.hexStr2Bytes(ByteUtil.str2HexStr(CodeSystem));//组织类型
		
		 bpersonIdLength= ByteUtil.int2Hbytes1byte(personId.getBytes().length);//组织编号长度
		 bpersonId= ByteUtil.hexStr2Bytes(ByteUtil.str2HexStr(personId));//秘钥号
		
	 	 bbtime = ByteUtil.unlong2H4bytes(btime);//生成时间（访客中的开始时间）
		 betime = ByteUtil.unlong2H4bytes(etime);//结束时间（访客中的到期时间）
		 
		 bapplyType= ByteUtil.int2Hbytes1byte(applyType);//访客方式
		 
		 bhexdevicenumberLength= ByteUtil.int2Hbytes1byte(hexdevicenumber.length);//组织编号长度
		 //合并数组
		hbytes = ByteUtil.concatBytes(bSecretkeynum, bOrgNumberLength, bOrgNumber, bCodeSystemLength, bCodeSystem, bpersonIdLength, bpersonId, bbtime, betime,bhexdevicenumberLength,hexdevicenumber,bapplyType);
		log.info("hbytes=="+ByteUtil.byte2Hex(hbytes));
		byte[] hmacs = null;
		log.info("authKey==="+authKey);
			try {
				hmacs = HMACUtil.encrypt1(hbytes, authKey);
				log.info("hmacs=="+ByteUtil.byte2Hex(hmacs));
				log.info("合并后=="+ByteUtil.byte2Hex(ByteUtil.concatBytes(hbytes,hmacs)));
				int random = StringUtil.random();
				log.info("random===="+random);
				offCode = "vg://v104" + B64Util.encode(ByteUtil.concatBytes(hbytes,hmacs)) + random;
				log.info("offCode==="+offCode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		return offCode;
	}

	public static String getVis_102_Qrcode(short version, long personid, long btime, int exptime, String akey,
			short vtype, short v102_custdata, int dataType, int visitorApplyId_102, int orgLength, String org,
			int jurisdictionLength, byte[] bytes) {
		String qrcode = null;
		log.info("begintime:{}",btime);
		log.info("exptime:{}",exptime);
		log.info("visitorApplyId_102:{}",visitorApplyId_102);	log.info("orgLength:{}",orgLength);	log.info("org:{}",org);
		log.info("jurisdictionLength:{}",jurisdictionLength);log.info("bytes:{}",bytes);
		byte[] bversion, buid, bbtime, bexptime, bvtype, hbytes, crcbytes, bcrc,
		bcust,bdataType,borgLength,borg,bjurisdictionLength;
		//
//		byte[] bcust = new byte[0];
//		byte[] bdataType = new byte[0];
		byte[] bvisitorApplyId = new byte[6];
//		byte[] borgLength = new byte[0];
//		byte[] borg = new byte[org.length()];
//		byte[] bjurisdictionLength = new byte[0];
		//
		
		bversion = ByteUtil.short2Hbytes(version);
		buid = ByteUtil.long2H6bytes(personid);
		bbtime = ByteUtil.long2H6bytes(btime);
		bexptime = ByteUtil.int2Hbytes(exptime);
		bvtype = ByteUtil.short2Hbytes(vtype);
		
		bcust = ByteUtil.short2Hbytes(v102_custdata);
		
		bdataType = ByteUtil.int2Hbytes1byte(dataType);
		bvisitorApplyId = ByteUtil.long2H6bytes(visitorApplyId_102);
		borgLength = ByteUtil.int2Hbytes1byte(orgLength);
		//borg = ByteUtil.long2H6bytes(Integer.valueOf(org));
		borg =org.getBytes();
		bjurisdictionLength = ByteUtil.int2Hbytes1byte(jurisdictionLength);
		
		log.info("bcust：："+ByteUtil.byte2Hex(bcust));
		log.info("bdataType：："+ByteUtil.byte2Hex(bdataType));
		log.info("bvisitorApplyId：："+ByteUtil.byte2Hex(bvisitorApplyId));
		log.info("borgLength：："+ByteUtil.byte2Hex(borgLength));
		log.info("borg：："+ByteUtil.byte2Hex(borg));
		log.info("bjurisdictionLength：："+ByteUtil.byte2Hex(bjurisdictionLength));
		
		//拼接成HMAC加密的字节数组
		hbytes = ByteUtil.concatBytes(bversion, buid, bbtime, bexptime, bvtype, bcust,bdataType,bvisitorApplyId,borgLength,borg,bjurisdictionLength,bytes);
		
		byte[] hmacs = null;
		try {
			hmacs = HMACUtil.encrypt(hbytes, akey);
			//拼接成CRC校验的字节数组,HMAC填补4字节0
			crcbytes = ByteUtil.concatBytes(hbytes, hmacs, ByteUtil.int2Hbytes(0));
			long crc = CRCUtil.encrypt(crcbytes);
			bcrc = ByteUtil.long2H4bytes(crc);
			log.info("hmacs==============="+hmacs);
			
			String cc = HexUtil.bytesToHexFun3(bcrc);
			String dd = HexUtil.bytesToHexFun3(crcbytes);
			String vv = HexUtil.bytesToHexFun3(hbytes);
			String gg = HexUtil.bytesToHexFun3(hmacs);
			log.info("bcrc======================"+cc);
			log.info("crcbytes=================="+dd);
			log.info("hbytes===================="+vv);
			log.info("hmacs===================="+gg);
			//门禁二维码格式：&mj
			qrcode = "&mj" + B64Util.encode(ByteUtil.concatBytes(crcbytes, bcrc)) + "@123";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return qrcode;
	}

}
