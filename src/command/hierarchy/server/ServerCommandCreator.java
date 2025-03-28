package command.hierarchy.server;

import server.state.ServerState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static validation.ObjectValidator.validateNotNull;

public class ServerCommandCreator {

    private static final Map<String, BiFunction<String, ServerState, ServerCommand>> COMMANDS = new HashMap<>();

    static {
        loadServerCommands();
    }

    public ServerCommand newCommand(String clientInput, ServerState serverState) {
        validateNotNull(clientInput, "client input string");
        validateNotNull(serverState, "server state");

        String mainCommandAction = clientInput.split(" ")[0];
        return COMMANDS.getOrDefault(mainCommandAction, UnknownServerCommand::new).apply(clientInput, serverState);
    }

    private static void loadServerCommands() {
        COMMANDS.put("register", RegisterClientServerCommand::new);
        COMMANDS.put("login", LoginClientServerCommand::new);
        COMMANDS.put("logout", LogoutClientServerCommand::new);
        COMMANDS.put("exit", ExitServerCommand::new);
        COMMANDS.put("play", PlayMusicServerCommand::new);
        COMMANDS.put("view-songs", ShowSongsServerCommand::new);
        COMMANDS.put("songs-top", ShowSongsStatisticsServerCommand::new);
        COMMANDS.put("search-by", SearchByKeywordsServerCommand::new);
        COMMANDS.put("view-playlists", ShowPlaylistsServerCommand::new);
        COMMANDS.put("create-playlist", CreatePlaylistServerCommand::new);
        COMMANDS.put("delete-playlist", DeletePlaylistServerCommand::new);
        COMMANDS.put("view-playlist", ShowPlaylistSongsServerCommand::new);
        COMMANDS.put("add-song", AddSongToPlaylistServerCommand::new);
        COMMANDS.put("remove-song", RemoveSongFromPlaylistServerCommand::new);
        COMMANDS.put("[identify]", IdentifyChannelServerCommand::new);
        COMMANDS.put("[get-supported-song-names]", GetSupportedSongsKeysServerCommand::new);
    }

}