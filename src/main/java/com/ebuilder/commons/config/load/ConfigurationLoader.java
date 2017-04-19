package com.ebuilder.commons.config.load;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.ebuilder.commons.config.util.Constant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * common module to pick configurations from dynamodb
 * specific method to pick ASA configuration
 * 
 */
public class ConfigurationLoader {
	
	private DynamoDB dynamoDB;
	private final AmazonDynamoDBClient dbClient;

	public ConfigurationLoader(AWSCredentialsProvider credentialsProvider, Region region){
        dbClient = new AmazonDynamoDBClient(credentialsProvider, new ClientConfiguration());
        dbClient.setRegion(region);

        dynamoDB = new DynamoDB(dbClient);
	}
	
	public ConfigurationLoader(AWSCredentialsProvider credentialsProvider){
		this(credentialsProvider, Region.getRegion(Regions.EU_WEST_1));
	}
	
	/**
	 * genaral method to pick configuration
	 * @param tableName
	 * @param identityCol
	 * @param identityVal
	 * @param configCol
	 * @return
	 */
	public <K, V> Map<K, V> getConfiguration(String tableName, String identityCol, String identityVal, String configCol){
		Table table = dynamoDB.getTable(tableName);	    
	    ItemCollection<QueryOutcome> items = table.query(buildQuery(identityCol, identityVal));	
	    Map<K, V> config = new HashMap<K, V>();
        if (items.iterator().hasNext()) {
            Item cfgJson = items.iterator().next();
            String configStr = "{}";
            if (cfgJson != null) {
            	configStr = cfgJson.getJSON(Constant.ASA_CONFIG_COLUMN);
            }
            try {
                config = new ObjectMapper().readValue(configStr, new TypeReference<Map<K, V>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config;		
	}

	/**
	 * specific method to pick asa configurations with minimal parameters
	 * @param identityVal
	 * @return
	 */
	public <K, V> Map<K, V> getASAConfiguration(String identityVal){
		Table table = dynamoDB.getTable(Constant.ASA_CONFIG_TABLE);	    
	    ItemCollection<QueryOutcome> items = table.query(buildQuery(Constant.ASA_IDENTITY_COLUMN, identityVal));	
	    Map<K, V> config = new HashMap<K, V>();
        if (items.iterator().hasNext()) {
            Item cfgJson = items.iterator().next();
            String configStr = "{}";
            if (cfgJson != null) {
            	configStr = cfgJson.getJSON(Constant.ASA_CONFIG_COLUMN);
            }
            try {
                config = new ObjectMapper().readValue(configStr, new TypeReference<Map<K, V>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config;
	}
	
	private QuerySpec buildQuery(String identityCol, String identityVal){
		QuerySpec querySpec = new QuerySpec()
	    	    .withKeyConditionExpression(identityCol+" = :v_conf")
	    	    .withValueMap(new ValueMap().withString(":v_conf", identityVal));
		return querySpec;
	}
}