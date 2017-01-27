package objects;

/**
 * Created by Arizel on 19.01.2017.
 */
public class Debtor {
    private String name;
    private long credit;

    public Debtor(String name, long credit ) {
        this.name = name;
        this.credit = credit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public String getName() {

        return name;
    }

    public long getCredit() {
        return credit;
    }

}
