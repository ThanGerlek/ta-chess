package server.services;

import dataAccess.*;

abstract class ServiceTest {
    protected AuthDAO authDAO;
    protected GameDAO gameDAO;
    protected UserDAO userDAO;

    void initDAOs() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO(userDAO);
        gameDAO = new MemoryGameDAO(userDAO);
    }

}