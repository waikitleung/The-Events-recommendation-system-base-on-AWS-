package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, userId);
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, userId);
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			saveItem(item);
		}
		return items;

	}

	@Override
	public void saveItem(Item item) {
		if (conn == null) {
			return;
		}
		try {
			//avoid SQL injection
			// SQL injection
			// Example:
			// SELECT * FROM users WHERE username = '<username>' AND password = '<password>';
			//
			// sql = "SELECT * FROM users WHERE username = '" + username + "'
			// AND password = '" + password + "'"
			//
			// username: aoweifjoawefijwaoeifj
			// password: 123456' OR '1' = '1
			//
			// SELECT * FROM users WHERE username = 'aoweifjoawefijwaoeifj' AND password = '123456' OR '1' = '1'
						
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)"; //if the primary key exists then the insert will be ignored
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setDouble(3, item.getRating());
			stmt.setString(4, item.getAddress());
			stmt.setString(5, item.getImageUrl());
			stmt.setString(6, item.getUrl());
			stmt.setDouble(7, item.getDistance());
			stmt.execute();
			
			sql = "INSERT IGNORE INTO categories VALUES (?, ?)";
			stmt = conn.prepareStatement(sql);
			for (String category : item.getCategories()) {
				stmt.setString(1, item.getItemId());
				stmt.setString(2, category);
				stmt.execute();  //executeUpdate will return int to indicate how many items have been update
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
