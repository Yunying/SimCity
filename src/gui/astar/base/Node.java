package gui.astar.base;
import java.util.*;

public interface Node {
    public boolean goalTest(Object o);
    public void printNode();
}