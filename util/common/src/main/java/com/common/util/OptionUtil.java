package com.common.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此选项已经包含h(help)、c(channel_id)、s(proc_id)的处理
 * */
public class OptionUtil {
	private static Logger logger = LoggerFactory.getLogger(OptionUtil.class);
	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();  
	private CommandLine commandLine=null;
	private String moduleName="moduleName";
	private HelpFormatter formatter = new HelpFormatter();
	
	public CommandLine getCommandLine() {
		return commandLine;
	}
	/**加入不带参数的选项*/
	public void addOptWithNoArg(String opt,String longOpt,String optDescription,boolean isOptional) {
		options.addOption(Option.builder(opt).longOpt(longOpt).desc(optDescription).optionalArg(isOptional).build());
	}
	/**加入带一个参数的选项，如-n 20
	 * sep为value多值时的分隔符*/
	public void addOptWithOneArg(String opt,String longOpt,String optDescription,boolean isOptional,String argName) {
		options.addOption(Option.builder(opt).longOpt(longOpt).desc(optDescription).optionalArg(isOptional).
							hasArg().valueSeparator().argName(argName).build());
	}
	public void addOptWithOneArg(String opt,String longOpt,String optDescription,boolean isOptional,String argName,char sep) {
		options.addOption(Option.builder(opt).longOpt(longOpt).desc(optDescription).optionalArg(isOptional).
							hasArg().valueSeparator(sep).argName(argName).numberOfArgs(Option.UNLIMITED_VALUES).build());
	}
	/**加入带两个或两个以上参数的选项，如-Dkey=value,每个key=value有两个参数，一个是key,一个是value，sep为key和value的分隔符*/
	public void addOptWithMultiArg(String opt,String longOpt,String optDescription,boolean isOptional,String argName) {
		options.addOption(Option.builder(opt).longOpt(longOpt).desc(optDescription).optionalArg(isOptional).
							hasArgs().valueSeparator().argName(argName).build());
	}
	public void addOptWithMultiArg(String opt,String longOpt,String optDescription,boolean isOptional,String argName,char sep) {
		options.addOption(Option.builder(opt).longOpt(longOpt).desc(optDescription).optionalArg(isOptional).
							hasArgs().valueSeparator(sep).argName(argName).numberOfArgs(Option.UNLIMITED_VALUES).build());
	}
	public void parseOpt(String moduleName,String[] args) {
		this.moduleName=moduleName;
		addOptWithNoArg("h","help", "print help information",false);
		addOptWithOneArg("c","channelid", "module instance id",false, "channel-id");
		addOptWithOneArg("s","procid", "proccess instance id",false, "proc-id");
		
        try {
            commandLine = parser.parse( options, args );
        } catch (ParseException e) {
        	logger.error("fail to parse opt:",e);
            System.out.println(e);  
            System.exit(0);  
        }  
        if(commandLine.hasOption("h")) {
        	help();
        	System.exit(0);  
        }
	}
	public void help() {
		formatter.printHelp( moduleName, options ,true);
	}
}
