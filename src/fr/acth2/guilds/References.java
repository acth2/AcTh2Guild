package fr.acth2.guilds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class References {
    private static final String DB_URL =
            "YOUR_DB_LINK";
    private static final String DB_USER = "YOUR_DB_USER";
    private static final String DB_PASSWORD = "YOUR_DB_PASSWD";

    public boolean isGuildChat(String uuid) {
        String sql = "SELECT isChat FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int col3Value = rs.getInt("isChat");
                    return (col3Value == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertPlayer(String uuid, String id, int admin, int isPublic, int isGuildChatting) {
        String sql = "INSERT INTO playerList (uuid, id, col3, publicCol, isChat) VALUES (?, ?, ?, ?, ?)";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            stmt.setString(2, id);
            stmt.setInt(3, admin);
            stmt.setInt(4, isPublic);
            stmt.setInt(5, isGuildChatting);

            int rowsInserted = stmt.executeUpdate();
            return (rowsInserted > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePlayer(String uuid) {
        String sql = "DELETE FROM playerList WHERE uuid = ?";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            int rowsDeleted = stmt.executeUpdate();

            return (rowsDeleted > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean searchGuild(String id) {
        String sql = "SELECT id FROM playerList WHERE id = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAllPlayersInGuild(String guildId) {
        List<String> uuidList = new ArrayList<>();
        String sql = "SELECT uuid FROM playerList WHERE id = ?";

        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    uuidList.add(rs.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuidList;
    }


    public boolean isAdmin(String uuid) {
        String sql = "SELECT col3 FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int col3Value = rs.getInt("col3");
                    return (col3Value == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isGuildChatting(String uuid) {
        String sql = "SELECT isChat FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int col3Value = rs.getInt("isChat");
                    return (col3Value == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerInDB(String uuid) {
        String sql = "SELECT uuid FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getId(String uuid) {
        String sql = "SELECT id FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPublic(String uuid) {
        String sql = "SELECT publicCol FROM playerList WHERE uuid = ?";
        try (
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, uuid);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int publicValue = rs.getInt("publicCol");
                    return (publicValue == 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}