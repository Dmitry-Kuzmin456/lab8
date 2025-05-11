package org.server.managers;



import model.*;
import org.server.network.Connector;
import org.common.request.UserData;
import org.common.request.UserDataRequest;
import org.server.utils.*;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.LogManager;

/**
 * Класс, управляющий коллекцией элементов
 */
public class CollectionManager {
    private HashMap<Long, CityWithUser> collection;
    private static CollectionManager instance;
    private Connection conn;
    private final ZonedDateTime creationTime = ZonedDateTime.now();
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();

    private CollectionManager() throws SQLException {
        String url = "jdbc:postgresql://localhost:5433/studs";
//        String url = "jdbc:postgresql://pg:5432/studs";
        String user = "s466402";
        String password = "oemGJFjonEIr5Jr0";



        conn = DriverManager.getConnection(url, user, password);
        collection = getCollectionFromDB();
        MyLogger.info("connection established");
    }

    public synchronized static CollectionManager getInstance() throws SQLException {
        if (instance == null){
            instance = new CollectionManager();
        }
        return instance;
    }

    public void addElementByID(long id, City city, UserInfo userInfo){
        lock.writeLock().lock();

        String insertCity = "INSERT INTO city (id, name, area, population, metersAboveSeaLevel, climate, government, standardOfLiving, user_id) VALUES (?, ?, ?, ?, ?, CAST(? AS climate), CAST(? AS government), CAST(? AS standard_of_living), ?)";
        String insertCoordinates = "INSERT INTO coordinates (city_id, x, y) VALUES (?, ?, ?)";
        String insertHuman = "INSERT INTO human (city_id, age) VALUES (?, ?)";



        try (PreparedStatement pstmt = conn.prepareStatement(insertCity)) {
            pstmt.setLong(1, id);
            pstmt.setString(2, city.getName());

            pstmt.setLong(3, city.getArea());
            pstmt.setInt(4, city.getPopulation());
            pstmt.setFloat(5, city.getMetersAboveSeaLevel());
            pstmt.setString(6, city.getClimate().toUpperCase());
            pstmt.setString(7, city.getGovernment().toUpperCase());
            pstmt.setString(8, city.getStandardOfLiving().toUpperCase());
            pstmt.setInt(9, userInfo.id());



            pstmt.executeUpdate();

            try (PreparedStatement ps = conn.prepareStatement(insertCoordinates)) {
                ps.setLong(1, id);
                ps.setDouble(2, city.getCoordinates().getX());
                ps.setDouble(3, city.getCoordinates().getY());

                ps.executeUpdate();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }

            try(PreparedStatement ps = conn.prepareStatement(insertHuman)) {
                ps.setLong(1, id);
                ps.setLong(2, city.getGovernor().getAge());

                ps.executeUpdate();
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
            collection.put(id, new CityWithUser(city, userInfo.id()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }


    }

    public void removeElement(long id, UserInfo userInfo) throws PermissionException {
        lock.writeLock().lock();
        String check = "SELECT 1 FROM city WHERE id = ? AND user_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(check)){
            ps.setLong(1, id);
            ps.setInt(2, userInfo.id());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                throw new PermissionException("You haven't permission on updating this element");
            }
        } catch (PermissionException e) {
            throw e;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        String deleteCity = "DELETE FROM city WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteCity)){
            ps.setLong(1, id);
            ps.executeUpdate();
            collection.remove(id);
            System.out.println("Element was removed");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }

    }

    public HashMap<Long, CityWithUser> getCollection() {
        lock.readLock().lock();
        try{
            return collection;
        } finally {
            lock.readLock().unlock();
        }
    }

    public HashMap<Long, CityWithUser> getCollectionFromDB() {
        lock.readLock().lock();
        HashMap<Long, CityWithUser> collection = new HashMap<>();
        String selectCity = "SELECT * FROM city";
        String selectCoordinates = "SELECT x, y FROM coordinates where city_id = ?";
        String selectHuman = "SELECT age FROM human where city_id = ?";

        try(PreparedStatement ps = conn.prepareStatement(selectCity)){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                City.CityBuilder builder = new City.CityBuilder();
                builder.setId(rs.getLong("id"));
                builder.setName(rs.getString("name"));
                try(PreparedStatement pst = conn.prepareStatement(selectCoordinates)){
                    pst.setLong(1, rs.getLong("id"));
                    ResultSet rs2 = pst.executeQuery();
                    rs2.next();
                    builder.setCoordinates(new Coordinates(rs2.getDouble("x"), rs2.getFloat("y")));
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                builder.setArea(rs.getLong("area"));
                builder.setPopulation(rs.getInt("population"));
                builder.setMetersAboveSeaLevel(rs.getFloat("metersabovesealevel"));
                builder.setClimate(Climate.fromDescription(rs.getString("climate").toLowerCase()));
                builder.setGovernment(Government.fromDescription(rs.getString("government").toLowerCase()));
                builder.setStandardOfLiving(StandardOfLiving.fromDescription(rs.getString("standardofliving")));


                try (PreparedStatement pst = conn.prepareStatement(selectHuman)){
                    pst.setLong(1, rs.getLong("id"));
                    ResultSet rs2 = pst.executeQuery();
                    rs2.next();
                    builder.setGovernor(new Human(rs2.getLong("age")));
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                collection.put(rs.getLong("id"), new CityWithUser(builder.build(), rs.getInt("user_id")));
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            lock.readLock().unlock();
        }
        return collection;
    }

    public String getInfo(){
        lock.readLock().lock();

        int size = getCollection().size();

        String time = String.valueOf(creationTime.getDayOfMonth()) + " " +
                String.valueOf(creationTime.getMonth()).toLowerCase() + " " +
                creationTime.getHour() + "h " + creationTime.getMinute() + "m";
        try {
            return "HashMap collection with City objects\ncreation time: " + time +
                    "\nnumber of elements is: " + size;
        } finally {
            lock.readLock().unlock();
        }


    }

    public void clearCollection(String login) {
        lock.writeLock().lock();
        String deleteCity = "DELETE FROM city WHERE user_id = (SELECT id FROM users WHERE login = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteCity)){
            pstmt.setString(1, login);
            pstmt.executeUpdate();
            collection = getCollectionFromDB();
            System.out.println("cleared collection");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public UserData searchUser(String login, String password){
        lock.writeLock().lock();
        String searchUser = "SELECT login, password FROM users WHERE login = ?";
        try(PreparedStatement ps = conn.prepareStatement(searchUser)){
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String userLogin = rs.getString(1);
                String userPassword = rs.getString(2);
                return new UserData(userLogin, userPassword);
            }

        } catch (Exception e){
            System.out.println(e.getMessage() + "!!!");
        } finally {
            lock.writeLock().unlock();
        }
        return null;
    }

    public boolean checkUser(String login, String password, UserInfo userInfo){
//        System.out.println(login + "\n" + HashPassword.hashPassword(password) + "\n" + userInfo.login() + "\n" + userInfo.password());
        return login.equals(userInfo.login()) && HashPassword.hashPassword(password).equals(userInfo.password());
    }

    public void addUser(UserData userData){
        lock.writeLock().lock();
        String addUser = "INSERT INTO users (login, password) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(addUser)){
            ps.setString(1, userData.login());
            ps.setString(2, HashPassword.hashPassword(userData.password()));
            ps.executeUpdate();
        }catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getUserId(String login){
        lock.readLock().lock();
        try{
            String searchUserID = "SELECT id FROM users WHERE login = ?";
            try (PreparedStatement ps = conn.prepareStatement(searchUserID)){
                ps.setString(1, login);
                ResultSet rs = ps.executeQuery();
                rs.next();
                int r = rs.getInt(1);
                return r;
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } finally {
            lock.readLock().unlock();
        }
        return 0;
    }
}
