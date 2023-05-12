package mrnavastar.invsync.util;

import com.google.common.io.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlacklistParser {

    public static ArrayList<String> getPlayerDataBlacklist() {
        try {
            InputStream stream = Resources.getResource("assets/invsync/blacklists/playerdata_blacklist").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            ArrayList<String> blacklist = new ArrayList<>(reader.lines().toList());
            stream.close();
            return blacklist;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}