package org.server.utils;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.server.network.Connector;
import org.common.request.RequestReader;
import org.common.request.UserData;
import org.common.request.UserDataRequest;
import org.server.managers.CollectionManager;

import java.sql.SQLException;

public class UserDefinition {
    public static UserData definition(Connector connector) throws Exception {
        int requestLength = 0;
        while (true) {
            try{
                requestLength = connector.receiveInt();
                UserDataRequest userDataRequest = RequestReader.readUserDataRequest(connector.receive(requestLength));
                char type = userDataRequest.type();
                if (!(type == 'l' || type == 'r')){
                    throw new Exception("Invalid user definition");
                }
                String login = userDataRequest.login();
                String password = userDataRequest.password();
                if (type == 'l'){
                    UserData userData = CollectionManager.getInstance().searchUser(login, password);
                    if (userData == null){
                        byte[] answer = ("no user with login " + login).getBytes();
                        connector.sendInt(answer.length);
                        connector.send(answer);
                    } else if (HashPassword.hashPassword(password).equals(userData.password())){
                        connector.send("OK".getBytes());
                        return userData;
                    } else {
                        connector.send(("wrong password for user " + login).getBytes());
                    }
                }

                else if (type == 'r'){
                    UserData userData = CollectionManager.getInstance().searchUser(login, password);
                    if (userData == null){
                        userData = new UserData(login, password);
                        CollectionManager.getInstance().addUser(userData);
                        connector.send("OK".getBytes());
                        userData = new UserData(login, HashPassword.hashPassword(password));
                        return userData;
                    } else {
                        connector.send(("user with login " + login + " already exists").getBytes());
                    }
                }
            } catch (SQLException e){
                throw new SQLException("Cannot connect to database");
            }
            catch (Exception e){
                throw e;
            }

        }

    }
}
