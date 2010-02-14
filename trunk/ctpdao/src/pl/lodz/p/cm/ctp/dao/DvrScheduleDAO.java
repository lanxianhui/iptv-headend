package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.DvrSchedule;

public final class DvrScheduleDAO {
	
	private static final String SQL_FIND_BY_ID =
        "SELECT id, programId, mode, fileName FROM DvrSchedule WHERE id = ?";
	private static final String SQL_FIND_BY_PROGRAMID = 
		"SELECT id, programId, mode, fileName FROM DvrSchedule WHERE programId = ?";
	private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, programId, mode, fileName FROM DvrSchedule ORDER BY id";
	private static final String SQL_INSERT =
        "INSERT INTO DvrSchedule (programId, mode, fileName) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE Program SET programId = ?, mode = ?, fileName = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM Program WHERE id = ?";
	
	private DAOFactory daoFactory;

	DvrScheduleDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public DvrSchedule find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }
	
	public DvrSchedule findByProgramId(Long programId) throws DAOException {
		return find(SQL_FIND_BY_PROGRAMID, programId);
	}
	
	private DvrSchedule find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        DvrSchedule dvrSchedule = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                dvrSchedule = mapDvrSchedule(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return dvrSchedule;
    }
	
	public List<DvrSchedule> list() throws DAOException {
		return list(SQL_LIST_ORDER_BY_ID);
	}
	
	public List<DvrSchedule> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<DvrSchedule> dvrSchedules = new ArrayList<DvrSchedule>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dvrSchedules.add(mapDvrSchedule(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return dvrSchedules;
    }
	
	public void create(DvrSchedule dvrSchedule) throws IllegalArgumentException, DAOException {
        if (dvrSchedule.getId() != null) {
            throw new IllegalArgumentException("This schedule is already created, the schedule id is not null.");
        }

        Object[] values = {
            dvrSchedule.getProgramId(),
            dvrSchedule.getMode(),
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
                throw new DAOException("Creating schedule channel failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                dvrSchedule.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating schedule channel, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(DvrSchedule dvrSchedule) throws DAOException {
        if (dvrSchedule.getId() == null) {
            throw new IllegalArgumentException("Schedule is not created yet, the schedule ID is null.");
        }

        Object[] values = {
            dvrSchedule.getProgramId(),
            dvrSchedule.getMode(),
            dvrSchedule.getFileName(),
            dvrSchedule.getId()
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
	
	public void save(DvrSchedule dvrSchedule) throws DAOException {
        if (dvrSchedule.getId() == null) {
            create(dvrSchedule);
        } else {
            update(dvrSchedule);
        }
    }
	
	public void delete(DvrSchedule dvrSchedule) throws DAOException {
        Object[] values = { dvrSchedule.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting schedule failed, no rows affected.");
            } else {
                dvrSchedule.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	private static DvrSchedule mapDvrSchedule(ResultSet resultSet) throws SQLException {
        return new DvrSchedule(
            resultSet.getLong("id"),
            resultSet.getLong("programId"),
            (DvrSchedule.Mode)resultSet.getObject("mode"),
            resultSet.getObject("fileName") != null ? resultSet.getString("fileName") : null
        );
    }
	
}
