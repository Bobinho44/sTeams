package fr.bobinho.steams.utils.team.request;

import fr.bobinho.steams.utils.team.Team;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.UUID;

public class JoinRequest {

    /**
     * Fields
     */
    private final Team sender;
    private final UUID receiver;

    /**
     * Creates a new practice request
     *
     * @param sender   the practice sender
     * @param receiver the practice receiver
     */
    public JoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
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
    public UUID getReceiver() {
        return receiver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JoinRequest)) {
            return false;
        }
        JoinRequest testedPracticeRequest = (JoinRequest) o;
        return testedPracticeRequest.getSender().equals(getSender()) && testedPracticeRequest.getReceiver().equals(getReceiver());
    }

}
