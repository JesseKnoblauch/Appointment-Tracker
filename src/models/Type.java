package models;

/**
 * Public class for Type objects used by TypeReport table.
 */
public class Type {
    private int count;
    private String type;

    /**
     * Constructor for Type
     * @param count Integer to set count attribute to.
     * @param type String to set type attribute to.
     */
    public Type(int count, String type) {
        this.count = count;
        this.type = type;
    }

    /**
     * Gets count attribute.
     * @return Returns count attribute.
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets count Integer of param.
     * @param count Integer to set count to.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Gets type attribute.
     * @return Returns type attribute.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type String of param.
     * @param type String to set type to.
     */
    public void setType(String type) {
        this.type = type;
    }
}
