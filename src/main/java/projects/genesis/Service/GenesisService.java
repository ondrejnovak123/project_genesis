package projects.genesis.Service;

import org.springframework.stereotype.Service;
import projects.genesis.Model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static projects.genesis.GenesisApplication.*;

@Service
public class GenesisService {
    private ArrayList<User> users;

    public GenesisService(){
        this.users = new ArrayList<>();
    }

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
            statement.executeQuery("SELECT * FROM users WHERE personId = '" + personID + "';");
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
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
        sendUserToDB(user);
        return "Success.";
    }
}
