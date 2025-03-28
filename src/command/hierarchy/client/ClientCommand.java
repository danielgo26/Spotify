package command.hierarchy.client;

import command.hierarchy.Command;
import client.state.ClientState;
import command.structure.Action;
import command.structure.CommandResult;
import command.structure.Status;
import exception.ConnectionException;
import exception.IOChannelException;
import iochannel.ChannelReader;
import iochannel.ChannelWriter;
import validation.ObjectValidator;

import java.nio.channels.SocketChannel;

import static command.structure.Action.VISUALIZE;

public abstract class ClientCommand extends Command {

    protected final ClientState clientState;

    protected final ChannelReader channelReader;
    protected final ChannelWriter channelWriter;

    public ClientCommand(String userInput, ClientState clientState) {
        super(userInput);

        ObjectValidator.validateNotNull(clientState, "client state");

        this.clientState = clientState;
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
    }

    @Override
    public abstract CommandResult execute() throws ConnectionException, IOChannelException;

    protected CommandResult getBaseUserClientCommand(String commandName, boolean shouldBeLogged)
        throws IOChannelException {
        if (shouldBeLogged && !clientState.isClientLogged()) {
            String response =
                String.format("The user must be logged in in order to execute the %s command!", commandName);

            return new CommandResult(Status.Error, response, Action.of(VISUALIZE));
        }

        return getBaseLoggedUserCommandServerResponse();
    }

    private CommandResult getBaseLoggedUserCommandServerResponse() throws IOChannelException {
        SocketChannel clientChannel = clientState.getClientChannel();

        channelWriter.writeTo(clientChannel, userInput);
        String serverResponse = channelReader.readStringFrom(clientChannel);

        return CommandResult.of(serverResponse, Action.of(VISUALIZE));
    }

}
