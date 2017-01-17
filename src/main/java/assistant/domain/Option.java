package assistant.domain;

public class Option {
    public String text;
    public Object value;

    public Option() {
        super();
    }
    public Option(String text, Object value) {
        super();
        this.text = text;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        Option that = (Option)obj;
        if(this.text == null || that.text == null)
            return this.value.equals(that.value);
        else
            return this.text.equals(that.text)
                && this.value.equals(that.value);
    }
    
    @Override
    public String toString() {
        return text;
    }
}
