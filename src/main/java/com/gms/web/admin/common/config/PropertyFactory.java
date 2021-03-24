package com.gms.web.admin.common.config;


import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderSupport;



/**
 * <pre>
 * 설정 파일에 Access 하기 위한 클래스
 * </pre>
 *
 * @author jjpark
 * @since 2014. 8. 14.
 */
public class PropertyFactory extends PropertiesLoaderSupport {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);

	/** The properties. */
	private static Properties properties = new Properties();
	
	static {
		try {
			String serverMode = StringUtils.defaultIfEmpty(System.getProperty("server.mode"), "local");
			
			String commonPath = "classpath:/properties/common/*.xml";
			String stagePath  = "classpath:/properties/" + serverMode + "/*.xml";
			
			Resource[] commonResources = new PathMatchingResourcePatternResolver().getResources(commonPath);
			Resource[] stageResources  = new PathMatchingResourcePatternResolver().getResources(stagePath);
			
			logger.info("Load {} property file(s) from {}", commonResources.length, commonPath);
			logger.info("Load {} property file(s) from {}", stageResources.length , stagePath );
			
			Resource[] resources = ArrayUtils.addAll(commonResources, stageResources);
			
			if(resources != null) {
				for(Resource resource : resources) {
					Properties currProps = new Properties();
					currProps.loadFromXML(resource.getInputStream());
					
					properties.putAll(currProps);

					logger.info("{} properties loaded from {}", currProps.size(), resource.getURI());
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
    /**
     * <pre>
     * <code>key</code>에 해당하는 value 을/를 반환한다.
     * </pre>
     *
     * @param key
     * @return property value
     */
    public static String getProperty(String key){
    	return properties.getProperty(key);
    }
    
       
        
    public static Properties getProperties() {
    	return properties;
    }
    
    /**
     * <pre>
     * <code>key</code>에 해당하는 value 을/를 반환한다.
     * </pre>
     *
     * @param key
     * @return property value
     */
    public static void setProperty(String key, String value){
    	 properties.setProperty(key, value);
    }
    

}
