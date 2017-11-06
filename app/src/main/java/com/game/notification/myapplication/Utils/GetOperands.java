package com.game.notification.myapplication.Utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Abhishek on 06-11-2017.
 */

public class GetOperands
{
    private static Random random;

    static
    {
        random = new Random();
    }

    public static Operands addition(int result)
    {
        int left = random.nextInt(result);
        int right = result - left;
        return new Operands(left, right);
    }

    public static Operands subtraction(int result)
    {
        int extra = random.nextInt(result) + 1;
        int left = result + extra;
        int right = extra;

        return new Operands(left, right);
    }

    public static Operands multiplication(int result)
    {
        int left = getFactor(result);
        int right = result / left;

        return  new Operands(left, right);
    }

    public static Operands division(int result)
    {
        int divisor = random.nextInt(7) + 1;
        int left = result * divisor;
        int right = divisor;

        return new Operands(left, right);
    }

    private static int getFactor(int result)
    {
        List<Integer> factors = new ArrayList<>();

        for(int i = 1; i <= Math.sqrt(result); ++i)
        {
            if(result % i == 0)
            {
                factors.add(i);
                factors.add(result / i);
            }
        }

        try
        {
            int size = factors.size();
            if(size == 2)
            {
                Log.v("DEBUG", "Returning 1 here");
                return 1;
            }

            int index = random.nextInt(factors.size());

            if(index == 1 || index == 2)
                index = 3;

            return factors.get(index);
        }

        catch(Exception e)
        {
            Log.e("List", "", e);
            return 1;
        }
    }


}
