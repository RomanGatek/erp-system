package cz.syntaxbro.erpsystem.constants;

public enum Permissions {
    READ_REPORTS("READ_REPORTS"),
    APPROVE_BUDGETS("APPROVE_BUDGETS"),
    VIEW_PROFILE("VIEW_PROFILE");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return this.permission;
    }
}