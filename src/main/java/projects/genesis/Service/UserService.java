package projects.genesis.Service;

import org.springframework.stereotype.Service;
import projects.genesis.Model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import static projects.genesis.GenesisApplication.*;

@Service
public class UserService {

    public boolean checkPersIDInList(String persID){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(dataPersonIdFilePath));
            String line = reader.readLine();

            while (line != null) {
                if (line.equals(persID)) {
                    return true;
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean checkForPersonIDAvailability(String personID) {
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();
            statement.executeQuery("SELECT * FROM users WHERE PersonID = '" + personID + "';");
            ResultSet res = statement.getResultSet();
            return !res.isBeforeFirst();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean sendUserToDB(User user){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();

            statement.execute("INSERT INTO users (name, surname, personid, uuid) " +
                                  "VALUES ('" + user.getName() + "','" +
                                               user.getSurname()     + "','" +
                                               user.getPersonID()     + "','" +
                                               user.getUuid()  + "');");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean newUser(User user) {
        String persID = user.getPersonID();

        if (!checkPersIDInList(persID)) {
            return false;
        }
        if (checkForPersonIDAvailability(persID)) {
            return sendUserToDB(user);
        }
        return false;
    }

    public User getUserFromDB(int ID){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();
            statement.executeQuery("SELECT * FROM users WHERE ID = '" + ID + "';");
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                User user = new User();
                user.setId(res.getInt(1));
                user.setName(res.getString("Name"));
                user.setSurname(res.getString("Surname"));
                user.setPersonID(res.getString("PersonID"));
                user.setUuid(UUID.fromString(res.getString("Uuid")));
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<>();
        String s = "";
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();
            statement.executeQuery("SELECT * FROM users");
            ResultSet res = statement.getResultSet();
            while (res.next()) {
                User user = new User();
                user.setId(res.getInt(1));
                user.setName(res.getString("Name"));
                user.setSurname(res.getString("Surname"));
                user.setPersonID(res.getString("PersonID"));
                user.setUuid(UUID.fromString(res.getString("Uuid")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public boolean updateUserInDB(User newUser){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();

            int rowsAffected = statement.executeUpdate("UPDATE users set Name = '" + newUser.getName()+ "', " +
                                                    "Surname = '" + newUser.getSurname() + "', " +
                                                    "Uuid = '"+ newUser.getUuid() + "' where ID = " + newUser.getId() + ";");

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUser(User newUser){
        if (newUser.getId() != 0){
            return updateUserInDB(newUser);
        } else {
            return false;
        }
    }

    public boolean deleteUserFromDB(int ID){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();

            int rowsAffected = statement.executeUpdate("DELETE FROM users WHERE ID = " + ID + ";");
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean deleteUser(int ID){
        if (ID != 0){
           return deleteUserFromDB(ID);
        } else {
            return false;
        }
    }

    public static class UserShort {
        private int id;

        private String name;
        private String surname;

        public UserShort(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }


    }
}
