package assistant.domain;

public class Location {
    public Long id;
    public Long pid;
    public String name;
    public String code;
    
    @Override
    public String toString() {
        return name;
    }
}
