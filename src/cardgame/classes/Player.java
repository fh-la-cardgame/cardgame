package cardgame.classes;

import java.util.LinkedList;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Klasse zur Abbildung eines Spielers.
 *
 * @author BishaThan
 */
public class Player {

    /**
     * Maximale Anzahl der Schutzschilder.
     **/
    private static final int MAX_SHIELD = 3;

    /**
     * Identifikationsnummer des Spielers.
     **/
    private final int id;
    /**
     * Name des Spielers.
     **/
    private final String name;

    /**
     * Schutzschilder des Spielers.
     **/
    private final Shield shields;

    /**
     * Konstruktor
     *
     * @param name Spielername
     */
    public Player(final String name) {
        this(0, name);
    }

    /**
     * Konstruktor
     *
     * @param id   Spielerid
     * @param name Spielername
     */
    public Player(final int id, final String name) {
        this.id = id;
        this.name = name;
        this.shields = new Shield(MAX_SHIELD);
    }

    public Player(Player player){
        this.id = player.id;
        this.name = player.name;
        this.shields = new Shield(player.shields);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Shield getShields() {
        return shields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                Objects.equals(shields, player.shields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shields);
    }
}
