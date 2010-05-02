package pl.lodz.p.cm.ctp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import pl.lodz.p.cm.ctp.dao.model.TvChannel;
import static pl.lodz.p.cm.ctp.dao.DAOUtil.*;

public final class TvChannelDAO {
	
	private static final String SQL_FIND_BY_ID =
        "SELECT id, name, ipAdress, port, unicastUrl, lcn, icon, enabled FROM TvChannel WHERE id = ?";
    private static final String SQL_FIND_BY_NAME =
        "SELECT id, name, ipAdress, port, unicastUrl, lcn, icon, enabled FROM TvChannel WHERE name = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT id, name, ipAdress, port, unicastUrl, lcn, icon, enabled FROM TvChannel ORDER BY id";
    private static final String SQL_INSERT =
        "INSERT INTO TvChannel (name, ipAdress, port, unicastUrl, lcn, icon, enabled) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
        "UPDATE TvChannel SET name = ?, ipAdress = ?, port = ?, unicastUrl = ?, lcn = ?, icon = ?, enabled = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM TvChannel WHERE id = ?";
    private static final String SQL_EXIST_NAME =
        "SELECT id FROM TvChannel WHERE name = ?";
	
	private DAOFactory daoFactory;

	TvChannelDAO(DAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public TvChannel find(Long id) throws DAOException {
        return find(SQL_FIND_BY_ID, id);
    }
	
	public TvChannel find(String name) throws DAOException {
		return find(SQL_FIND_BY_NAME, name);
	}
	
	private TvChannel find(String sql, Object... values) throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        TvChannel tvChannel = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, sql, false, values);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tvChannel = mapTvChannel(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return tvChannel;
    }
	
	public List<TvChannel> list() throws DAOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<TvChannel> tvChannels = new ArrayList<TvChannel>();

        try {
            connection = daoFactory.getConnection();
            preparedStatement = connection.prepareStatement(SQL_LIST_ORDER_BY_ID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tvChannels.add(mapTvChannel(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return tvChannels;
    }
	
	public void create(TvChannel tvChannel) throws IllegalArgumentException, DAOException {
        if (tvChannel.getId() != null) {
            throw new IllegalArgumentException("This TV channel is already created, the channel ID is not null.");
        }

        Object[] values = {
            tvChannel.getName(),
            tvChannel.getIpAdress(),
            tvChannel.getPort(),
            tvChannel.getUnicastUrl(),
            tvChannel.getLCN(),
            tvChannel.getIcon(),
            tvChannel.getEnabled().toString().toUpperCase()
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
                tvChannel.setId(generatedKeys.getLong(1));
            } else {
                throw new DAOException("Creating TV channel, no generated key obtained.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement, generatedKeys);
        }
    }

	public void update(TvChannel tvChannel) throws DAOException {
        if (tvChannel.getId() == null) {
            throw new IllegalArgumentException("TV channel is not created yet, the TV channel ID is null.");
        }

        Object[] values = {
            tvChannel.getName(),
            tvChannel.getIpAdress(),
            tvChannel.getPort(),
            tvChannel.getUnicastUrl(),
            tvChannel.getLCN(),
            tvChannel.getIcon(),
            tvChannel.getEnabled().toString().toUpperCase(),
            tvChannel.getId()
        };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_UPDATE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating TV channel failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	public void save(TvChannel tvChannel) throws DAOException {
        if (tvChannel.getId() == null) {
            create(tvChannel);
        } else {
            update(tvChannel);
        }
    }
	
	public boolean exist(String name) throws DAOException {
		return exist(SQL_EXIST_NAME, name);
	}
	
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
	
	public void delete(TvChannel tvChannel) throws DAOException {
        Object[] values = { tvChannel.getId() };

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = daoFactory.getConnection();
            preparedStatement = prepareStatement(connection, SQL_DELETE, false, values);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting TV channel failed, no rows affected.");
            } else {
                tvChannel.setId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            close(connection, preparedStatement);
        }
    }
	
	private static TvChannel mapTvChannel(ResultSet resultSet) throws SQLException {
        return new TvChannel(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("ipAdress"),
            resultSet.getInt("port"),
            resultSet.getString("unicastUrl"),
            resultSet.getInt("lcn"),
            resultSet.getObject("icon") != null ? resultSet.getString("icon") : null,
            resultSet.getString("enabled").equals("TRUE") ? true : false
        );
    }

}
