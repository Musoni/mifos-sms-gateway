package org.mifos.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.mifos.sms.data.SmsOutboundMessageResponseData;
import org.mifos.sms.exception.SmsOutboundMessageNotFoundException;
import org.mifos.sms.helper.HelperClass.Jdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ReadSmsOutboundMessageServiceImpl implements ReadSmsOutboundMessageService {
	private final JdbcTemplate jdbcTemplate;
	private final SmsOutboundMessageMapper smsOutboundMessageMapper;
	
	@Autowired
	public ReadSmsOutboundMessageServiceImpl(SmsGatewayDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.smsOutboundMessageMapper = new SmsOutboundMessageMapper();
	}
	
	/** 
	 * Maps a resultset to SmsOutboundMessageResponseData object
	 **/
	private static final class SmsOutboundMessageMapper implements RowMapper<SmsOutboundMessageResponseData> {
		
		final String queryString;
		final String tableName = "smsOutboundMessage";
		
		public SmsOutboundMessageMapper() {
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
		public SmsOutboundMessageResponseData mapRow(ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
			final Long id = Jdbc.getLong(rs, "internalId");
			final Long externalId = Jdbc.getLong(rs, "id");
			final String addedOnDate = rs.getString("addedOnDate");
			final String deliveredOnDate = rs.getString("deliveredOnDate");
			final Integer deliveryStatus = Jdbc.getInteger(rs, "deliveryStatus");
			
			return SmsOutboundMessageResponseData.getInstance(id, externalId, addedOnDate, deliveredOnDate, deliveryStatus, false, "");
		}
	}
	
	@Override
	public Collection<SmsOutboundMessageResponseData> getAll(List<Long> externalIds, String mifosTenantIdentifier) {
		
		try {
			final String externalIdsDelimitedString = StringUtils.collectionToDelimitedString(externalIds, ", ");
			final String queryString = "select " + smsOutboundMessageMapper.getQueryString() + " where t1.id in (" + externalIdsDelimitedString + ") and mifosTenantIdentifier = ?";
			
			return this.jdbcTemplate.query(queryString, smsOutboundMessageMapper, new Object[] { mifosTenantIdentifier }); 
		}
		
		catch(Exception e) {
			throw new SmsOutboundMessageNotFoundException("Query for mifosTenantIdentifier: " + mifosTenantIdentifier + 
					" and list of external ids: " + externalIds.toString() + " returned an empty result set");
		}
	}
}
