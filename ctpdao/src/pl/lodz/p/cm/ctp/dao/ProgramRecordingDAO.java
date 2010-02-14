package pl.lodz.p.cm.ctp.dao;

import static pl.lodz.p.cm.ctp.dao.DAOUtil.close;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.*;

public final class ProgramRecordingDAO {
	
	private static final String SQL_LIST_BY_TVCHANNELID_ORDER_BY_BEGIN =
        "SELECT Program.id, Program.tvChannelId, Program.title, Program.description, Program.begin, Program.end, " + 
        "Recording.id, Recording.mode, Recording.fileName FROM Program, Recording " +
        "WHERE Program.id = Recording.programId AND Program.tvChannelId = ? ORDER BY Program.begin ASC";
	
	private static final String SQL_LIST_BY_TVCHANNELID_OLDER_THAN_NOW_BY_BEGIN =
        "SELECT Program.id, Program.tvChannelId, Program.title, Program.description, Program.begin, Program.end, " + 
        "Recording.id, Recording.mode, Recording.fileName FROM Program, Recording " +
        "WHERE Program.id = Recording.programId AND Program.tvChannelId = ? " +
        "AND Program.end > NOW() AND Recording.mode = 'WAITING'" +
        "ORDER BY Program.begin ASC";
	
	private DAOFactory daoFactory;

	ProgramRecordingDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public List<ProgramRecording> listOlderThanNow(long tvChannelId) throws DAOException {
		return list(SQL_LIST_BY_TVCHANNELID_OLDER_THAN_NOW_BY_BEGIN, tvChannelId);
	}
	
	public List<ProgramRecording> list(long tvChannelId) throws DAOException {
		return list(SQL_LIST_BY_TVCHANNELID_ORDER_BY_BEGIN, tvChannelId);
	}
	
	public List<ProgramRecording> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ProgramRecording> dvrSchedules = new ArrayList<ProgramRecording>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dvrSchedules.add(mapProgramDvrSchedule(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return dvrSchedules;
    }
	
	private static ProgramRecording mapProgramDvrSchedule(ResultSet resultSet) throws SQLException {
        return new ProgramRecording(
    		new Program(
    			resultSet.getLong("Program.id"),
    			resultSet.getLong("Program.tvChannelId"),
    			resultSet.getString("Program.title"),
    			resultSet.getString("Program.description"),
    			resultSet.getTimestamp("Program.begin"),
    			resultSet.getTimestamp("Program.end")
    		),
    		new Recording(
    			resultSet.getLong("Recording.id"),
    			resultSet.getLong("Program.id"),
    			Recording.Mode.valueOf(resultSet.getString("Recording.mode")),
                resultSet.getObject("Recording.fileName") != null ? resultSet.getString("Recording.fileName") : null
    		)
        );
    }

}
