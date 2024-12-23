package projects.genesis.Service;

import org.springframework.stereotype.Service;
import projects.genesis.Model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static projects.genesis.GenesisApplication.*;

@Service
public class GenesisService {

    public String getPersID(){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(dataPersonIdFilePath));
            String line = reader.readLine();

            while (line != null) {
                if (checkForPersonIDAvailability(line)) {
                    return line;
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    public String newUser(User user) {
        String persID = user.getPersonID();

        if (Objects.equals(persID,"") || persID == null) {
            persID = getPersID();
            user.setPersonID(persID);
        } else {
            if (!checkForPersonIDAvailability(persID)){
                persID = getPersID();
                user.setPersonID(persID);
            }
        }
        if (sendUserToDB(user)) {
            return "Success.";
        } else {
            return  "Failed.";
        }
    }

    public String convertUserToJson(User user, Boolean detail){
        if (detail) {
            return "{" + "\r\n" +
                    "id: " + user.getId() + ",\r\n" +
                    "name: " + user.getName() + ",\r\n" +
                    "surname: " + user.getSurname() + ",\r\n" +
                    "personID: " + user.getPersonID() + ",\r\n" +
                    "uuid: " + user.getUuid() + "\r\n" +
                    "}";
        } else {
            return "{" + "\r\n" +
                    "id: " + user.getId() + ",\r\n" +
                    "name: " + user.getName() + ",\r\n" +
                    "surname: " + user.getSurname() + ",\r\n" +
                    "}";
        }
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
                user.setPersonID(res.getString("PersonID"));
                user.setName(res.getString("Name"));
                user.setSurname(res.getString("Surname"));
                user.setUuid(UUID.fromString(res.getString("Uuid")));
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getUser(int ID, boolean detail){
        User user = getUserFromDB(ID);
        if (user == null){
          return "User not found.";
        } else {
           return convertUserToJson(user, detail);
        }


    }

    public String getUsers(boolean detail){
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
                user.setPersonID(res.getString("PersonID"));
                user.setName(res.getString("Name"));
                user.setSurname(res.getString("Surname"));
                user.setUuid(UUID.fromString(res.getString("Uuid")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        s = "List<";
        boolean first = true;
        for (User user: users) {
            if (!first) {
                s = s.concat(",");
            }
            s = s.concat(convertUserToJson(user,detail));
            first = false;
        }
        s = s.concat(">");
        return s;
    }

    public boolean updateUserInDB(User newUser){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();

            statement.execute("UPDATE users set Name = '" + newUser.getName()+ "', " +
                                                    "Surname = '" + newUser.getSurname() + "', " +
                                                    "PersonID = '" + newUser.getPersonID() + "', "+
                                                    "Uuid = '"+ newUser.getUuid() + "' where ID = " + newUser.getId() + ";");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateUser(User newUser){
        if (newUser.getId() != 0){
           if (updateUserInDB(newUser)) { return "Success.";}
           else { return "Failed.";}
        } else {
            return "Failed.";
        }
    }

    public boolean deleteUserFromDB(int ID){
        try (
                Connection con = DriverManager.getConnection(dbSourcePath, dbUsername, dbPassword);
        ) {
            Statement statement = con.createStatement();

            statement.execute("DELETE FROM users WHERE ID = " + ID + ";");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String deleteUser(int ID){
        if (ID != 0){
            if (deleteUserFromDB(ID)) { return "Success.";}
            else { return "Failed.";}
        } else {
            return "Failed.";
        }
    }
}
