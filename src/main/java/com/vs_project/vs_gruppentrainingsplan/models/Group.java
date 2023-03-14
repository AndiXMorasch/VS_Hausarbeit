package com.vs_project.vs_gruppentrainingsplan.models;

import java.util.ArrayList;
import java.util.List;

public class Group {
    String groupname;
    List<User> groupMembers = new ArrayList<>();

    public Group(String groupname) {
        this.groupname = groupname;
    }

    public void addGroupMember(User member) {
        this.groupMembers.add(member);
    }

    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public String getGroupname() {
        return this.groupname;
    }
}
