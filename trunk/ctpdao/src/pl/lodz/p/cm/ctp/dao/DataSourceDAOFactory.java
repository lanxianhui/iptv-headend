package pl.lodz.p.cm.ctp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class DataSourceDAOFactory extends DAOFactory {
	private DataSource dataSource;

    DataSourceDAOFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
