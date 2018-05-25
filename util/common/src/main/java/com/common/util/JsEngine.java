package com.common.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JS规则引擎，动态调用JAVA代码
 * 
 * @author xulr
 *
 */
public class JsEngine {
	private static final Logger logger = LoggerFactory.getLogger(JsEngine.class);
	
	public class JsError{		
	}
	
	private ScriptEngine engine = null;
	
	public JsEngine(){
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("nashorn");//getEngineByExtension("js");
	}
	
	public ScriptEngine getEngine(){
		return engine;
	}
	
	/**
	 * 从字符串创建JS脚本
	 */
	public boolean buildJsScript4String(String script){
		try{
			engine.eval(script);			
			return true;
		}catch (ScriptException e) {
			logger.error("buildJsScript ScriptException:{}",e);
			logger.error("buildJsScript Script:{}",script);
		}
		
		return false;
	}	

	
	/**
	 * 从文件创建JS脚本
	 */
	public boolean buildJsScript4File(String jsFile){
		try {
			FileReader r = new FileReader(jsFile);
			engine.eval(r);			
			return true;
		} catch (FileNotFoundException e) {
			logger.error("buildJsScript4File FileNotFoundException:{}", e);
		} catch (ScriptException e) {
			logger.error("buildJsScript4File ScriptException:{}", e);
		}
		
		return false;
	}
	
	/**
	 * 执行代码
	 * 
	 * @param
	 * @return
	 */
	public Object execFunc(String func,Object... args) {
		Invocable invocable = (Invocable) engine;
		try {
			return invocable.invokeFunction(func,args);
		} catch (NoSuchMethodException e) {
			logger.debug("exec noSuchMethodException:{}", func);
			return null;
		} catch (ScriptException e) {
			logger.error("exec scriptException:{}", e);
			logger.debug("{}({})", func,args);
		} catch (Exception e) {
			logger.error("exec Exception:{}", e);
			logger.debug("{}({})", func,args);
		}
		return (new JsError());
	}
}
