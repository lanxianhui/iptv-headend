package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.Recording;

public final class RecordingDAO {
	
	private static final String SQL_FIND_BY_ID =
        "SELECT id, programId, mode, fileName FROM Recording WHERE id = ?";
	private static final String SQL_FIND_BY_PROGRAMID = 
		"SELECT id, programId, mode, fileName FROM Recording WHERE programId = ?";
	private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, programId, mode, fileName FROM Recording ORDER BY id";
	private static final String SQL_INSERT =
        "INSERT INTO Recording (programId, mode, fileName) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE Recording SET programId = ?, mode = ?, fileName = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM Recording WHERE id = ?";
	
	private DAOFactory daoFactory;

	RecordingDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public Recording find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }
	
	public Recording findByProgramId(Long programId) throws DAOException {
		return find(SQL_FIND_BY_PROGRAMID, programId);
	}
	
	private Recording find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Recording dvrSchedule = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                dvrSchedule = mapRecording(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return dvrSchedule;
    }
	
	public List<Recording> list() throws DAOException {
		return list(SQL_LIST_ORDER_BY_ID);
	}
	
	public List<Recording> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Recording> dvrSchedules = new ArrayList<Recording>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dvrSchedules.add(mapRecording(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return dvrSchedules;
    }
	
	public void create(Recording dvrSchedule) throws IllegalArgumentException, DAOException {
        if (dvrSchedule.getId() != null) {
            throw new IllegalArgumentException("This recording is already created, the recording id is not null.");
        }

        Object[] values = {
            dvrSchedule.getProgramId(),
            dvrSchedule.getMode().toString(),
            dvrSchedule.getFileName()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating recording failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                dvrSchedule.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating recording, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(Recording recording) throws DAOException {
        if (recording.getId() == null) {
            throw new IllegalArgumentException("Recording is not created yet, the recording ID is null.");
        }

        Object[] values = {
            recording.getProgramId(),
            recording.getMode().toString(),
            recording.getFileName(),
            recording.getId()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating schedule failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public void save(Recording recording) throws DAOException {
        if (recording.getId() == null) {
            create(recording);
        } else {
            update(recording);
        }
    }
	
	public void delete(Recording recording) throws DAOException {
        Object[] values = { recording.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting schedule failed, no rows affected.");
            } else {
                recording.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	private static Recording mapRecording(ResultSet resultSet) throws SQLException {
        return new Recording(
            resultSet.getLong("id"),
            resultSet.getLong("programId"),
            Recording.Mode.valueOf(resultSet.getString("Recording.mode")),
            resultSet.getObject("fileName") != null ? resultSet.getString("fileName") : null
        );
    }
	
}
