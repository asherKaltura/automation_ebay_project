package enums;

public enum TextStyle {
    BOLD("b"),
    ITALIC("i"),
    UNDERLINE("u"),
    STRIKETHROUGH("s"),
    NONE("");

    private String str;
    private TextStyle(String str){
        this.str = str;
    }

    public String getHtmlValue(){
        return this.str;
    }
}
