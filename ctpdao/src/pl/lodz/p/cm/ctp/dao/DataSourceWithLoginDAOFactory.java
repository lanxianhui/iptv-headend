package pl.lodz.p.cm.ctp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceWithLoginDAOFactory extends DAOFactory {
	private DataSource dataSource;
    private String username;
    private String password;

    DataSourceWithLoginDAOFactory(DataSource dataSource, String username, String password) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    Connection getConnection() throws SQLException {
        return dataSource.getConnection(username, password);
    }
}
