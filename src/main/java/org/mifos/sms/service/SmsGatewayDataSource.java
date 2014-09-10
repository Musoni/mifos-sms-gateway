package org.mifos.sms.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.stereotype.Service;

@Service(value = "smsGatewayDataSource")
public class SmsGatewayDataSource extends AbstractDataSource {
	private final DataSource dataSource;
	
	@Autowired
	public SmsGatewayDataSource(final @Qualifier("smsGatewayDataSourceJndi") DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return dataSource.getConnection(username, password);
	}
}
