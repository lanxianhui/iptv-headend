package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.UserRecording;

public final class UserRecordingDAO {
	
	private static final String SQL_FIND_BY_ID =
        "SELECT recordingId, userId, createdOn FROM UserRecording WHERE recordingId = ?, userId = ?";
	private static final String SQL_LIST_BY_RECORDINGID = 
		"SELECT recordingId, userId, createdOn FROM UserRecording WHERE recordingId = ?";
	private static final String SQL_LIST_DISPOSABLE =
		"SELECT UserRecording.recordingId, UserRecording.userId, UserRecording.createdOn FROM UserRecording LEFT JOIN Recording ON UserRecording.recordingId = Recording.id LEFT JOIN Program ON Recording.programId = Program.id WHERE TIMEDIFF(HOUR, createdOn, NOW()) >= ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT recordingId, userId, createdOn FROM UserRecording ORDER BY userId";
    private static final String SQL_INSERT =
        "INSERT INTO UserRecording (recordingId, userId, createdOn) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE UserRecording SET recordingId = ?, userId = ?, createdOn = ? WHERE userId = ?, recordingId = ?";
    private static final String SQL_DELETE =
        "DELETE FROM UserRecording WHERE recordingId = ?, userId = ?";
    
    private DAOFactory daoFactory;

	UserRecordingDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public List<UserRecording> listByRecordingId(Long recordingId) throws DAOException {
		return list(SQL_LIST_BY_RECORDINGID, recordingId);
	}
	
	public UserRecording find(Long recordingId, Long userId) throws DAOException {
        return find(SQL_FIND_BY_ID, recordingId, userId);
    }
	
	private UserRecording find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        UserRecording userRecording = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userRecording = mapUserRecording(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return userRecording;
    }
	
	public List<UserRecording> list() throws DAOException {
		return list(SQL_LIST_ORDER_BY_ID);
	}
	
	public List<UserRecording> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<UserRecording> users = new ArrayList<UserRecording>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(mapUserRecording(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return users;
    }
	
	public void create(UserRecording userRecording) throws IllegalArgumentException, DAOException {
		userRecording.setCreatedOn(new Date());
		
        Object[] values = {
            userRecording.getRecordingId(),
            userRecording.getUserId(),
            userRecording.getCreatedOn()
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
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(UserRecording userRecording) throws DAOException {
        Object[] values = {
                userRecording.getRecordingId(),
                userRecording.getUserId(),
                userRecording.getCreatedOn(),
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating userRecording failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public void save(UserRecording user) throws DAOException {
        if (user.getCreatedOn() == null) {
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
	
	public void delete(UserRecording userRecording) throws DAOException {
        Object[] values = { userRecording.getRecordingId(), userRecording.getUserId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            } else {
                userRecording.setRecordingId(null);
                userRecording.setUserId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	private static UserRecording mapUserRecording(ResultSet resultSet) throws SQLException {
        return new UserRecording(
            resultSet.getLong("recordingId"),
            resultSet.getLong("userId"),
            resultSet.getDate("createdOn")
        );
    }

}
