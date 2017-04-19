package com.ebuilder.commons.config.load;

import java.util.Map;

import org.junit.Test;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

/*
 * unit test class for configuration loader
 *
 */
public class ConfigurationLoaderTest {
	
	/**
	 * test get configuration method
	 */
	@Test
	public void testGetConfiguration(){
		ConfigurationLoader loader = new ConfigurationLoader(new DefaultAWSCredentialsProviderChain(), Region.getRegion(Regions.EU_WEST_1));
		Map<String, Map<String, String>> result = loader.getConfiguration("ASAConfiguration", "configName", "mule-bulk-createreturn", "config");
		result.entrySet().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
	}
	
	/**
	 * test asa get configuration method
	 */
	@Test
	public void testASAGetConfiguration(){
		ConfigurationLoader loader = new ConfigurationLoader(new DefaultAWSCredentialsProviderChain(), Region.getRegion(Regions.EU_WEST_1));
		Map<String, Map<String, String>> result = loader.getASAConfiguration("mule-bulk-createreturn");
		result.entrySet().forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
	}
}