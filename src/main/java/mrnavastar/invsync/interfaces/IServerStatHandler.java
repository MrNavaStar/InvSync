package mrnavastar.invsync.interfaces;

public interface IServerStatHandler {
    void writeStatData(String statData);
    String readStatData();
}
