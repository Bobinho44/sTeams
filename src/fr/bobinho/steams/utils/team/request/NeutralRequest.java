package fr.bobinho.steams.utils.team.request;

import fr.bobinho.steams.utils.team.Team;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;

public class NeutralRequest {

    /**
     * Fields
     */
    private final Team sender;
    private final Team receiver;

    /**
     * Creates a new practice request
     *
     * @param sender   the practice sender
     * @param receiver the practice receiver
     */
    public NeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Gets the request sender
     *
     * @return the sender
     */
    public Team getSender() {
        return sender;
    }

    /**
     * Gets the request receiver
     *
     * @return the receiver
     */
    public Team getReceiver() {
        return receiver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NeutralRequest)) {
            return false;
        }
        NeutralRequest neutralRequest = (NeutralRequest) o;
        return neutralRequest.getSender().equals(getSender()) && neutralRequest.getReceiver().equals(getReceiver());
    }

}