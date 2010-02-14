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

public final class ProgramDvrScheduleDAO {
	
	private static final String SQL_LIST_BY_TVCHANNELID_ORDER_BY_BEGIN =
        "SELECT Program.id, Program.tvChannelId, Program.title, Program.description, Program.begin, Program.end, " + 
        "DvrSchedule.id, DvrSchedule.mode, DvrSchedule.fileName FROM Program, DvrSchedule " +
        "WHERE Program.id = DvrSchedule.programId AND Program.tvChannelId = ? ORDER BY Program.begin ASC";
	
	private static final String SQL_LIST_BY_TVCHANNELID_OLDER_THAN_NOW_BY_BEGIN =
        "SELECT Program.id, Program.tvChannelId, Program.title, Program.description, Program.begin, Program.end, " + 
        "DvrSchedule.id, DvrSchedule.mode, DvrSchedule.fileName FROM Program, DvrSchedule " +
        "WHERE Program.id = DvrSchedule.programId AND Program.tvChannelId = ? " +
        "AND Program.begin > NOW()" +
        "ORDER BY Program.begin ASC";
	
	private DAOFactory daoFactory;

	ProgramDvrScheduleDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public List<ProgramDvrSchedule> listOlderThanNow() throws DAOException {
		return list(SQL_LIST_BY_TVCHANNELID_OLDER_THAN_NOW_BY_BEGIN);
	}
	
	public List<ProgramDvrSchedule> list() throws DAOException {
		return list(SQL_LIST_BY_TVCHANNELID_ORDER_BY_BEGIN);
	}
	
	public List<ProgramDvrSchedule> list(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ProgramDvrSchedule> dvrSchedules = new ArrayList<ProgramDvrSchedule>();

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
	
	private static ProgramDvrSchedule mapProgramDvrSchedule(ResultSet resultSet) throws SQLException {
        return new ProgramDvrSchedule(
    		new Program(
    			resultSet.getLong("Program.id"),
    			resultSet.getLong("Program.tvChannelId"),
    			resultSet.getString("Program.title"),
    			resultSet.getString("Program.description"),
    			resultSet.getTimestamp("Program.begin"),
    			resultSet.getTimestamp("Program.end")
    		),
    		new DvrSchedule(
    			resultSet.getLong("DvrSchedule.id"),
    			resultSet.getLong("Program.id"),
    			(DvrSchedule.Mode)resultSet.getObject("DvrSchedule.mode"),
                resultSet.getObject("DvrSchedule.fileName") != null ? resultSet.getString("DvrSchedule.fileName") : null
    		)
        );
    }

}
