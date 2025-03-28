package command.hierarchy.client;

import client.state.ClientState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static validation.ObjectValidator.validateNotNull;

public class ClientCommandCreator {

    private static final Map<String, BiFunction<String, ClientState, ClientCommand>> COMMANDS = new HashMap<>();
    private static final Map<String, String> COMMANDS_FORMAT = new HashMap<>();

    static {
        loadClientCommands();
        loadClientCommandsFormat();
    }

    public ClientCommand newCommand(String userInput, ClientState clientState) {
        validateNotNull(userInput, "user input string");
        validateNotNull(clientState, "client state");

        String mainCommandAction = userInput.split(" ")[0];
        return COMMANDS.getOrDefault(mainCommandAction, UnknownClientCommand::new).apply(userInput, clientState);
    }

    public static Map<String, String> getAvailableCommandFormats() {
        return COMMANDS_FORMAT;
    }

    private static void loadClientCommands() {
        COMMANDS.put("register", RegisterClientCommand::new);
        COMMANDS.put("login", LoginClientCommand::new);
        COMMANDS.put("logout", LogoutClientCommand::new);
        COMMANDS.put("exit", ExitClientCommand::new);
        COMMANDS.put("play", PlayMusicClientCommand::new);
        COMMANDS.put("stop", StopMusicClientCommand::new);
        COMMANDS.put("view-songs", ShowSongsClientCommand::new);
        COMMANDS.put("songs-top", ShowSongsStatisticsClientCommand::new);
        COMMANDS.put("search-by", SearchByKeywordsClientCommand::new);
        COMMANDS.put("view-playlists", ShowPlaylistsClientCommand::new);
        COMMANDS.put("create-playlist", CreatePlaylistClientCommand::new);
        COMMANDS.put("delete-playlist", DeletePlaylistClientCommand::new);
        COMMANDS.put("view-playlist", ShowPlaylistSongsClientCommand::new);
        COMMANDS.put("add-song", AddSongToPlaylistClientCommand::new);
        COMMANDS.put("remove-song", RemoveSongFromPlaylistClientCommand::new);
        COMMANDS.put("help", HelpClientCommand::new);
    }

    private static void loadClientCommandsFormat() {
        COMMANDS_FORMAT.put("register", "<email> <password>");
        COMMANDS_FORMAT.put("login", "<email> <password>");
        COMMANDS_FORMAT.put("logout", "");
        COMMANDS_FORMAT.put("exit", "");
        COMMANDS_FORMAT.put("play", "<song key>");
        COMMANDS_FORMAT.put("stop", "");
        COMMANDS_FORMAT.put("view-songs", "");
        COMMANDS_FORMAT.put("songs-top", "<count>");
        COMMANDS_FORMAT.put("search-by", "<keyword1> <keyword2> ...");
        COMMANDS_FORMAT.put("view-playlists", "");
        COMMANDS_FORMAT.put("create-playlist", "<name>");
        COMMANDS_FORMAT.put("delete-playlist", "<name>");
        COMMANDS_FORMAT.put("view-playlist", "<name>");
        COMMANDS_FORMAT.put("add-song", "<song name> <playlist name>");
        COMMANDS_FORMAT.put("remove-song", "<song name> <playlist name>");
        COMMANDS_FORMAT.put("help", "");
    }

}