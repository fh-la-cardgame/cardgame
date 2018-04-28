package cardgame.classes;

import java.util.Objects;

/**
 * Bildet eine Teilstruktur einer Karte ab.
 *
 * @author BishaThan
 */
public abstract class Card implements Comparable<Card> {

    /**
     * Identifikationsnummer der Karte. *
     */
    private final int cid;
    /**
     * Name der Karte. *
     */
    private final String name;
    /**
     * Kartenbeschreibung. Zusammensetzung aus der Kartenbeschreibung und den
     * Effektbeschreibungen. *
     */
    private final String description;
    /**
     * Typ einer Karte. *
     */
    private final Type type;
    /**
     * Bild einer Karte *
     */
    private final byte[] image;

    /**
     * Konstruktor
     *
     * @param cid         Identifikationnummer
     * @param name        Name der Karte
     * @param description Kartenbeschreibung
     * @param type        Typ der Karte
     */
    protected Card(final int cid, final String name, final String description, final Type type, final byte[] image) {

        this.cid = cid;
        this.name = name;
        this.description = description;
        this.type = type;
        this.image = image;


    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public byte[] getImage() {
        return image;
    }

    public int getCid() {
        return cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cid == card.cid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }

    @Override
    public int compareTo(Card other) {
        return this.cid - other.cid;
    }

    @Override
    public String toString() {
        return getCid() + " " + getName() + " " + getDescription() + "\nWerte: " + getType().toString();
    }

}
