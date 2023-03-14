package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.User;

public class UserDTO {
    private String username;
    private boolean isAdmin;

    public UserDTO(User user){
        this.username = user.getUsername();
        this.isAdmin = user.isAdmin();
    }

    public UserDTO(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
