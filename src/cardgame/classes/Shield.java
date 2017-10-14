/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.classes;

/**
 *
 * @author HortensiaX
 */
public class Shield {
    private short maxShields;
    private short currentShields;

    public short getMaxShields() {
        return maxShields;
    }

    public short getCurrentShields() {
        return currentShields;
    }

    public void setCurrentShields(short currentShields) {
        this.currentShields = currentShields;
    }
    
    Shield(byte max){
        this(max, max);
    }
    
    Shield(byte current, byte max){
        
        this.currentShields = current;
        this.maxShields = max;
        
    }
}
