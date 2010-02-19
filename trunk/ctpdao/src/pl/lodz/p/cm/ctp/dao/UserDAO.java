package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.User;

public final class UserDAO {
	
	private static final String SQL_FIND_BY_ID =
        "SELECT id, userName, password, authToken, quota, enabled, fullName, createdOn, lastLogin FROM User WHERE id = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, userName, password, authToken, quota, enabled, fullName, createdOn, lastLogin FROM User ORDER BY id";
    private static final String SQL_INSERT =
        "INSERT INTO User (userName, password, authToken, quota, enabled, fullName, createdOn, lastLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE User SET userName = ?, password = ?, authToken = ?, quota = ?, enabled = ?, fullName = ?, createdOn = ?, lastLogin = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM User WHERE id = ?";
    
    private DAOFactory daoFactory;

	UserDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public User find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }
	
	private User find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = mapUser(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return user;
    }
	
	public List<User> list() throws DAOException {
		return list(SQL_LIST_ORDER_BY_ID);
	}
	
	public List<User> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<User>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return users;
    }
	
	public void create(User user) throws IllegalArgumentException, DAOException {
        if (user.getId() != null) {
            throw new IllegalArgumentException("This user is already created, the user id is not null.");
        }

        Object[] values = {
            user.getUserName(),
            user.getPassword(),
            user.getAuthToken(),
            user.getQuota(),
            user.getEnabled() ? "TRUE" : "FALSE",
            user.getCreatedOn(),
            user.getLastLogin()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating user, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(User user) throws DAOException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User is not created yet, the user ID is null.");
        }

        Object[] values = {
                user.getUserName(),
                user.getPassword(),
                user.getAuthToken(),
                user.getQuota(),
                user.getEnabled() ? "TRUE" : "FALSE",
                user.getCreatedOn(),
                user.getLastLogin()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public void save(User user) throws DAOException {
        if (user.getId() == null) {
            create(user);
        } else {
            update(user);
        }
    }
	
	@SuppressWarnings("unused")
	private boolean exist(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean exist = false;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            exist = resultSet.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return exist;
    }
	
	public void delete(User user) throws DAOException {
        Object[] values = { user.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            } else {
                user.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	private static User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getLong("id"),
            resultSet.getString("userName"),
            resultSet.getObject("password") != null ? resultSet.getString("description") : null,
            resultSet.getObject("authToken") != null ? resultSet.getString("authToken") : null,
            resultSet.getInt("quota"),
            resultSet.getString("enabled") == "TRUE" ? true : false,
            resultSet.getString("fullName"),
            resultSet.getDate("createdOn"),
            resultSet.getObject("lastLogin")  != null ? resultSet.getDate("lastLogin") : null
        );
    }

}
