package fr.bobinho.steams.utils.team.request;

import fr.bobinho.steams.utils.team.Team;
import fr.bobinho.steams.utils.team.TeamManager;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RequestManager {

    /**
     * The join requests list
     */
    private static final List<JoinRequest> joinRequests = new ArrayList<>();

    /**
     * The join requests list
     */
    private static final List<AllyRequest> allyRequests = new ArrayList<>();

    /**
     * The neutral requests list
     */
    private static final List<NeutralRequest> neutralRequests = new ArrayList<>();

    /**
     * Gets all join request
     *
     * @return all join requests
     */
    private static List<JoinRequest> getJoinRequests() {
        return joinRequests;
    }

    /**
     * Gets all ally request
     *
     * @return all ally requests
     */
    private static List<AllyRequest> getAllyRequests() {
        return allyRequests;
    }

    /**
     * Gets all neutral request
     *
     * @return all neutral requests
     */
    private static List<NeutralRequest> getNeutralRequests() {
        return neutralRequests;
    }

    /**
     * Gets a specific join request
     *
     * @param receiver the join receiver
     * @return the join request if found
     */
    private static Optional<JoinRequest> getJoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        //Gets the join request
        return getJoinRequests().stream().filter(request -> request.getReceiver().equals(receiver) && request.getSender().equals(sender)).findFirst();
    }

    /**
     * Gets a specific ally request
     *
     * @param receiver the ally receiver
     * @return the ally request if found
     */
    private static Optional<AllyRequest> getAllyRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        //Gets the ally request
        return getAllyRequests().stream().filter(request -> request.getReceiver().equals(receiver) && request.getSender().equals(sender)).findFirst();
    }

    /**
     * Gets a specific neutral request
     *
     * @param receiver the neutral receiver
     * @return the neutral request if found
     */
    private static Optional<NeutralRequest> getNeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        //Gets the ally request
        return getNeutralRequests().stream().filter(request -> request.getReceiver().equals(receiver) && request.getSender().equals(sender)).findFirst();
    }

    /**
     * Checks if the player has join request
     *
     * @param receiver the receiver
     * @return if he has join request
     */
    public static boolean hasJoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        return getJoinRequest(sender, receiver).isPresent();
    }

    /**
     * Checks if the player has ally request
     *
     * @param receiver the receiver
     * @return if he has ally request
     */
    public static boolean hasAllyRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        return getAllyRequest(sender, receiver).isPresent();
    }

    /**
     * Checks if the player has neutral request
     *
     * @param receiver the receiver
     * @return if he has neutral request
     */
    public static boolean hasNeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");

        return getNeutralRequest(sender, receiver).isPresent();
    }

    /**
     * Sends a join request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void sendJoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
        Validate.notNull(sender, "senderUuid is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(!hasJoinRequest(sender, receiver), "receiver already have an invitation");

        //Creates the join request
        getJoinRequests().add(new JoinRequest(sender, receiver));
    }

    /**
     * Sends an ally request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void sendAllyRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "senderUuid is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(!hasAllyRequest(sender, receiver), "receiver already have an invitation");

        //Creates the ally request
        getAllyRequests().add(new AllyRequest(sender, receiver));
    }

    /**
     * Sends a neutral request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void sendNeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "senderUuid is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(!hasNeutralRequest(sender, receiver), "receiver already have an invitation");

        //Creates the ally request
        getNeutralRequests().add(new NeutralRequest(sender, receiver));
    }

    /**
     * Removes a join request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void removeJoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasJoinRequest(sender, receiver), "receiver dont have any request");

        //Removes the join request
        getJoinRequests().remove(getJoinRequest(sender, receiver).get());
    }

    /**
     * Removes an ally request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void removeAllyRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasAllyRequest(sender, receiver), "receiver dont have any request");

        //Removes the ally request
        getAllyRequests().remove(getAllyRequest(sender, receiver).get());
    }

    /**
     * Removes a neutral request
     *
     * @param sender   the sender
     * @param receiver the receiver
     */
    public static void removeNeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasNeutralRequest(sender, receiver), "receiver dont have any request");

        //Removes the ally request
        getNeutralRequests().remove(getNeutralRequest(sender, receiver).get());
    }

    /**
     * Accepts a join request
     *
     * @param receiver the receiver
     */
    public static void acceptJoinRequest(@Nonnull Team sender, @Nonnull UUID receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasJoinRequest(sender, receiver), "receiver dont have any request");
        Validate.isTrue(!TeamManager.isInTeam(receiver), "receiver is already in a town");

        //Accepts the join request
        TeamManager.joinTeam(sender, receiver);
        removeJoinRequest(sender, receiver);
    }

    /**
     * Accepts an ally request
     *
     * @param receiver the receiver
     */
    public static void acceptAllyRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasAllyRequest(sender, receiver), "receiver dont have any request");

        //Accepts the ally request
        TeamManager.createAlly(sender, receiver);
        removeAllyRequest(sender, receiver);
    }

    /**
     * Accepts a neutral request
     *
     * @param receiver the receiver
     */
    public static void acceptNeutralRequest(@Nonnull Team sender, @Nonnull Team receiver) {
        Validate.notNull(sender, "sender is null");
        Validate.notNull(receiver, "receiver is null");
        Validate.isTrue(hasNeutralRequest(sender, receiver), "receiver dont have any request");

        //Accepts the neutral request
        TeamManager.deleteEnemy(sender, receiver);
        removeNeutralRequest(sender, receiver);
    }

}
