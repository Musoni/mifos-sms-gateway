package org.mifos.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifos.sms.data.ConfigurationData;
import org.mifos.sms.exception.ConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/** 
 * Implementation of the read configuration service interface.
 * Fetches configuration data object(s) from the configuration table 
 * 
 * @author Emmanuel Nnaa
 **/
@Service
public class ReadConfigurationServiceImpl implements ReadConfigurationService {
	private final JdbcTemplate jdbcTemplate;
	private final ConfigurationMapper configurationMapper;
	
	/** 
	 * ReadConfigurationServiceImpl constructor 
	 * 
	 * @param jdbcTemplate JdbcTemplate object
	 * @return void
	 **/
	@Autowired
	public ReadConfigurationServiceImpl(SmsGatewayDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.configurationMapper = new ConfigurationMapper();
	}
	
	/** 
	 * Maps a resultset to SmsOutboundMessageResponseData object
	 **/
	private static final class ConfigurationMapper implements RowMapper<ConfigurationData> {
		final String queryString;
		final String tableName = "configuration";
		
		public ConfigurationMapper() {
			final StringBuilder stringBuilder = new StringBuilder(300);
			stringBuilder.append("t1.* from " + tableName + " t1");
			
			queryString = stringBuilder.toString();
		}
		
		/** 
		 * @return sql query string 
		 **/
		public String getQueryString() {
			return queryString;
		}

		@Override
		public ConfigurationData mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
			final String name = rs.getString("name");
			final String value = rs.getString("value");
			
			return ConfigurationData.getInstance(name, value);
		}
	}

	@Override
	public Collection<ConfigurationData> getAll() {
		
		try {
			return this.jdbcTemplate.query("select " + configurationMapper.getQueryString(), configurationMapper);
		}
		
		catch(Exception e) {
			throw new ConfigurationNotFoundException("Query for configuration returned an empty resultset");
		}
	}

	@Override
	public ConfigurationData get(String name) {
		
		try {
			final String queryString = "select " + configurationMapper.getQueryString() + " where name = ?";
			
			return this.jdbcTemplate.queryForObject(queryString, configurationMapper, new Object[] { name });
		}
		
		catch(Exception e) {
			throw new ConfigurationNotFoundException("Query for configuration with name '" + name + "', returned an empty resultset.");
		}
	}

}
