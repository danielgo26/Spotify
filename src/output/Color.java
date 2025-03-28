package output;

public enum Color {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    WHITE("\u001B[0m");

    private final String colorCode;

    Color(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }
}
