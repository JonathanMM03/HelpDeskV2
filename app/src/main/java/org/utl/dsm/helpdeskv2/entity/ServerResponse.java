package org.utl.dsm.helpdeskv2.entity;

public class ServerResponse {
    private int estatus;
    private String msg;
    private String timestamp;

    public ServerResponse() {}

    public ServerResponse(int estatus, String msg, String timestamp) {
        this.estatus = estatus;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
