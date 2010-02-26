package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.Program;

public final class ProgramDAO {

	private static final String SQL_FIND_BY_ID =
        "SELECT id, tvChannelId, title, description, begin, end FROM Program WHERE id = ?";
    private static final String SQL_LIST_BY_CHANNEL_ID_ORDER_BY_BEGIN =
        "SELECT id, tvChannelId, title, description, begin, end FROM Program WHERE tvChannelId = ? ORDER BY begin ASC";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, tvChannelId, title, description, begin, end FROM Program ORDER BY id";
    private static final String SQL_INSERT =
        "INSERT INTO Program (tvChannelId, title, description, begin, end) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE Program SET tvChannelId = ?, title = ?, description = ?, begin = ?, end = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM Program WHERE id = ?";
    private static final String SQL_DELETE_OLDER =
        "DELETE FROM Program WHERE TIMESTAMPDIFF(DAY, NOW(), Program.end) > ?";
	
	private DAOFactory daoFactory;

	ProgramDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public Program find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }
	
	private Program find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Program program = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                program = mapProgram(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return program;
    }
	
	public List<Program> list(Long channelId) throws DAOException {
		return list(SQL_LIST_BY_CHANNEL_ID_ORDER_BY_BEGIN, channelId);
	}
	
	public List<Program> list() throws DAOException {
		return list(SQL_LIST_ORDER_BY_ID);
	}
	
	public List<Program> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Program> tvChannels = new ArrayList<Program>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tvChannels.add(mapProgram(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return tvChannels;
    }
	
	public void create(Program program) throws IllegalArgumentException, DAOException {
        if (program.getId() != null) {
            throw new IllegalArgumentException("This program is already created, the program id is not null.");
        }

        Object[] values = {
            program.getTvChannelId(),
            program.getTitle(),
            program.getDescription(),
            program.getBegin(),
            program.getEnd()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_INSERT, true, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating TV channel failed, no rows affected.");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                program.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating TV channel, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(Program program) throws DAOException {
        if (program.getId() == null) {
            throw new IllegalArgumentException("Program is not created yet, the program ID is null.");
        }

        Object[] values = {
            program.getTvChannelId(),
            program.getTitle(),
            program.getDescription(),
            program.getBegin(),
            program.getEnd(),
            program.getId()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating program failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public void save(Program program) throws DAOException {
        if (program.getId() == null) {
            create(program);
        } else {
            update(program);
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
	
	public void delete(Program program) throws DAOException {
        Object[] values = { program.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting program failed, no rows affected.");
            } else {
                program.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public boolean deleteOlderThan(Long days) throws DAOException {
		Object[] values = { days };
		boolean result = false;
		
		Connection connection = null;
        PreparedStatement preparedStatement = null;
		
		try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE_OLDER, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                //throw new DAOException("Deleting program failed, no rows affected.");
            } else {
            	result = true;
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
        return result;
	}
	
	private static Program mapProgram(ResultSet resultSet) throws SQLException {
        return new Program(
            resultSet.getLong("id"),
            resultSet.getLong("tvChannelId"),
            resultSet.getString("title"),
            resultSet.getObject("description") != null ? resultSet.getString("description") : null,
            resultSet.getTimestamp("begin"),
            resultSet.getTimestamp("end")
        );
    }
}
