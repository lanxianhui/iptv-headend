package pl.lodz.p.cm.ctp.dao;

import java.sql.*;
import javax.sql.*;
import javax.naming.*;

public abstract class DAOFactory {

	private static final String JNDI_ROOT = "java:comp/env/";
	
	public static DAOFactory getInstance(DatabaseConfiguration config) throws DAOConfigurationException {
		if (config == null) {
			throw new DAOConfigurationException("Database configuration is null.");
		}
		
		DAOFactory instance;
		
		if (config.databaseDriver != null) {
			try {
				Class.forName(config.databaseDriver);
			} catch (ClassNotFoundException e) {
				throw new DAOConfigurationException("Driver class '" + config.databaseDriver + "' is missing in classpath.");
			}
			instance = new DriverManagerDAOFactory(config.databaseLocator, config.userName, config.password);
		} else {
			DataSource dataSource;
			try {
				dataSource = (DataSource) new InitialContext().lookup(JNDI_ROOT + config.databaseLocator);
			} catch (NamingException e) {
				throw new DAOConfigurationException("Datasource '" + config.databaseLocator + "' is missing in JNDI.", e);
			}
			if (config.userName != null) {
				instance = new DataSourceWithLoginDAOFactory(dataSource, config.userName, config.password);
			} else {
				instance = new DataSourceDAOFactory(dataSource);
			}
		}
		
		return instance;
	}
	
	abstract Connection getConnection() throws SQLException;
	
	public ProgramDAO getProgramDAO() {
		return new ProgramDAO(this);
	}
	
	public DvrScheduleDAO getDvrScheduleDAO() {
		return new DvrScheduleDAO(this);
	}
	
	public TvChannelDAO getTvChannelDAO() {
		return new TvChannelDAO(this);
	}
	
	public ProgramDvrScheduleDAO getProgramDvrScheduleDAO() {
		return new ProgramDvrScheduleDAO(this);
	}
	
}
