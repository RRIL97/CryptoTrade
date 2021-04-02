package Code.Helpers;



public interface Strategy {
    void    calculate();
    boolean exitTrade();
    boolean isSatisfied(int candleIndex);
}
