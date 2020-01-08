package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;


public class ElGamal {

    public static String decodeMessage(BigInteger m) {
        return new String(m.toByteArray());
    }

    public static BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base);
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    public static void main(String[] arg) {
        String filename = "input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            BigInteger p = new BigInteger(br.readLine().split("=")[1]);
            BigInteger g = new BigInteger(br.readLine().split("=")[1]);
            BigInteger y = new BigInteger(br.readLine().split("=")[1]);
            String line = br.readLine().split("=")[1];
            String date = line.split(" ")[0];
            String time = line.split(" ")[1];
            int year  = Integer.parseInt(date.split("-")[0]);
            int month = Integer.parseInt(date.split("-")[1]);
            int day   = Integer.parseInt(date.split("-")[2]);
            int hour   = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            int second = Integer.parseInt(time.split(":")[2]);
            BigInteger c1 = new BigInteger(br.readLine().split("=")[1]);
            BigInteger c2 = new BigInteger(br.readLine().split("=")[1]);
            br.close();
            BigInteger m = recoverSecret(p, g, y, year, month, day, hour, minute,
                    second, c1, c2);
            System.out.println("Recovered message: " + m);
            System.out.println("Decoded text: " + decodeMessage(m));
        } catch (Exception err) {
            System.err.println("Error handling file.");
            err.printStackTrace();
            System.exit(1);
        }
    }

    public static BigInteger recoverSecret(BigInteger p, BigInteger g,
                                           BigInteger y, int year, int month, int day, int hour, int minute,
                                           int second, BigInteger c1, BigInteger c2) {
        //Declare message m
        BigInteger m = BigInteger.ZERO;

        //first, we need to find random number r, so that c_1 = g^r, c_2 = m * h^r
        //we use the given function of finding the random number

        //convert to BI
        BigInteger bigYear = BigInteger.valueOf(year);
        BigInteger bigMonth = BigInteger.valueOf(month);
        BigInteger bigDay = BigInteger.valueOf(day);
        BigInteger bigHour = BigInteger.valueOf(hour);
        BigInteger bigMinute = BigInteger.valueOf(minute);
        BigInteger bigSecond = BigInteger.valueOf(second);

        //take proper exponent
        BigInteger base = BigInteger.valueOf(10);

        BigInteger n1 = base.pow(10);
        BigInteger n2 = base.pow(8);
        BigInteger n3 = base.pow(6);
        BigInteger n4 = base.pow(4);
        BigInteger n5 = base.pow(2);


        BigInteger r1 = bigYear.multiply(n1);
        BigInteger r2 = bigMonth.multiply(n2);
        BigInteger r3 = bigDay.multiply(n3);
        BigInteger r4 = bigHour.multiply(n4);
        BigInteger r5 = bigMinute.multiply(n5);
        BigInteger r6 = bigSecond;

        BigInteger s1 = r1.add(r2);
        BigInteger s2 = s1.add(r3);
        BigInteger s3 = s2.add(r4);
        BigInteger s4 = s3.add(r5);
        BigInteger r = s4.add(r6); //r is our random number

        //find proper milisecond and add it to random value r
        //BigInteger milisecond = BigInteger.ZERO;
        BigInteger mili;
        int i = 0;
        while (i<1000){
            mili = r.add(BigInteger.valueOf(i));
            if (c1 == g.modPow(mili, p)){
                r = mili;
                break;
            }
            i++;

        }
        /**
        while (milisecond < 1000){
            randomMili = r.add(BigInteger.valueOf(milisecond));
            if (c1 == (pow(g, randomMili).mod(p))){
                r = randomMili;
                break;
            }
            milisecond++;

        }
        */

        //BigInteger yPowR = pow(y, r);
        BigInteger yPowR = g.modPow(r, p);
        System.out.println("yPowR is " + yPowR);
        BigInteger yPowRInverse = yPowR.modInverse(p);
        System.out.println("yPowRInverse is "+ yPowRInverse);
        m = c2.multiply(yPowRInverse);
        /*
        BigInteger denominator = pow(y, r);
        BigInteger fraction = c2.divide(denominator);

        m = fraction.mod(p);
        **/


        return m;
    }



}