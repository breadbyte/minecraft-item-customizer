package com.github.breadbyte.itemcustomizer.server.util;

public sealed interface Reason {

    String message();

    default String getMessage() {
        if (message() != null && !message().isBlank()) {
            return message();
        }

        if (this instanceof NoExp e) {
            return "This command requires at least " + e.required() + " experience level(s)!";
        } else if (this instanceof InvalidPlayer) {
            return "Command can only be called by a player.";
        } else if (this instanceof NoPermission) {
            return "You do not have permission to use this command!";
        } else if (this instanceof NoItem) {
            return "You are not holding an item!";
        } else if (this instanceof WrongOwnership) {
            return "You do not own this item!";
        } else if (this instanceof InvalidInput) {
            return "Invalid input!";
        } else if (this instanceof NoInput) {
            return "No input provided!";
        } else if (this instanceof InternalError) {
            return "An internal error occurred while executing this command!";
        } else if (this instanceof ItemLockedOwner) {
            return "Unlock the item before modifying it!";
        }

        throw new IllegalStateException("Unexpected value: " + this);
    }

    record InvalidPlayer(String message)                    implements Reason { public InvalidPlayer() { this(null); } }
    record NoPermission(String message)                     implements Reason { public NoPermission() { this(null); } }
    record NoItem(String message)                           implements Reason { public NoItem() { this(null); } }
    record ItemLockedOwner(String message)                  implements Reason { public ItemLockedOwner() { this(null); } }
    record NoExp(String message, int required, int actual)  implements Reason { public NoExp(int required, int actual) { this(null, required, actual); } }
    record WrongOwnership(String message)                   implements Reason { public WrongOwnership() { this(null); } }
    record InvalidInput(String message)                     implements Reason { public InvalidInput() { this(null); } }
    record NoInput(String message)                          implements Reason { public NoInput() { this(null); } }
    record InternalError(String message)                    implements Reason { public InternalError() { this(null); } }
    record NotAnError(String message)                       implements Reason { public NotAnError() { this(null); } }

    Reason INVALID_PLAYER = new InvalidPlayer();
    Reason NO_PERMISSION = new NoPermission();
    Reason NO_ITEM = new NoItem();
    Reason WRONG_OWNERSHIP = new WrongOwnership();
    Reason ITEM_LOCKED_OWNER = new ItemLockedOwner();
    Reason NO_INPUT = new NoInput();
}
