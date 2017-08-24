package test;

import java.util.Comparator;

public class CompareProduct implements Comparator<Product>
{
    public int compare(Product p1, Product p2)
    {
        int value1 = p2.getCreated().compareTo(p1.getCreated());
        if (value1 == 0) {
            int value2 = p2.getPrice().compareTo(p1.getPrice());
            	return value2;
        }
        return value1;
    }
}
