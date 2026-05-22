package messagerie_instantanee.client.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import messagerie_instantanee.client.models.ServerBookmark;

public class ServerBookmarkService {
    private static final Path FICHIER = Path.of("data/serversBookmark.json");

    /**
     * 
     * @return
     * @throws IOException
     * 
     * Charge une liste de serverBookmark.
     */
    public List<ServerBookmark> charger() throws IOException {
        if (!Files.exists(FICHIER)) return new ArrayList<>();
        return new Gson().fromJson(
            Files.readString(FICHIER),
            new TypeToken<List<ServerBookmark>>(){}.getType()
        );
    }

    public void sauvegarder(List<ServerBookmark> liste) throws IOException {
        Files.writeString(FICHIER,
            new GsonBuilder().setPrettyPrinting().create().toJson(liste));
    }
}