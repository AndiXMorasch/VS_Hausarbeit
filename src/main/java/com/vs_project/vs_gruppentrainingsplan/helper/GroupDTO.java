package com.vs_project.vs_gruppentrainingsplan.helper;

import com.vs_project.vs_gruppentrainingsplan.models.Group;

import java.util.Collection;
import java.util.stream.Collectors;

public class GroupDTO {
    private String name;
    private Collection<UserDTO> users;

    public GroupDTO(Group group) {
        this.name = group.getGroupname();
        this.users = group.getGroupMembers().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Collection<UserDTO> users) {
        this.users = users;
    }
}
