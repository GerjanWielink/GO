package main.GoProtocol;

public enum ClientCommand {
    HANDSHAKE("HANDSHAKE"),
    SET_CONFIG("SET_CONFIG"),
    MOVE("MOVE"),
    PASS("PASS"),
    EXIT("EXIT");

    private String tag;

    ClientCommand(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return this.tag;
    }
}