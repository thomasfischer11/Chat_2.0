package sample;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String password;
    private boolean online;
    private boolean isBanned;
    private int usernumber;
    private String room;

    public User(String name, String password, int usernumber, String room) {
        this.name = name;
        this.password = password;
        this.online = false;
        this.usernumber = usernumber;
        this.room = room;
        this.isBanned = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(int usernumber) {
        this.usernumber = usernumber;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setBanned (boolean b){
        isBanned = b;
    }

    public boolean isBanned (){
        return isBanned;
    }

    @Override
    public String toString() {
        return "name:" + name + "\npassword: " + password + "\nonline: " + online + "usernumber: " + usernumber + "room: " + room + "banned: " + isBanned;
    }
}
