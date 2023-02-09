package com.vguang.utils;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

/**
 * @author wangsir
 *
 * 2017年10月14日
 */
public class CustomCodehausObjectMapper extends ObjectMapper {
	
	public CustomCodehausObjectMapper(){  
        CustomSerializerFactory factory = new CustomSerializerFactory();  
        factory.addGenericMapping(Date.class, new JsonSerializer<Date>(){  
            @Override  
            public void serialize(Date value,JsonGenerator jsonGenerator,SerializerProvider provider) throws IOException, JsonProcessingException {  
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
//                jsonGenerator.writeString(sdf.format(value));  
            	
            	//ResponseBody转换时间为秒
            	jsonGenerator.writeString(String.valueOf(value.getTime()/1000));  
            }  
        });  
        this.setSerializerFactory(factory);  
    }  
}
