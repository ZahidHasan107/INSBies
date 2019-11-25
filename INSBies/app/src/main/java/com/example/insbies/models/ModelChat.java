package com.example.insbies.models;

public class ModelChat {
    String messege,reciever,sender,timestamp;
    boolean isSeen;

    public ModelChat() {

    }

    public ModelChat(String messege, String reciever, String sender, String timestamp, boolean isSeen) {
        this.messege = messege;
        this.reciever = reciever;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessge(String message) {
        this.messege = message;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciver(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
