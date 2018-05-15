package com.mmall.util;



public class gg {
    public static void main(String[] args) {

        long startTime=System.currentTimeMillis();
        System.out.println(startTime);
        for (int i=0;i < 10101010; i++){
            final int index = i;

            System.out.println(index);

            }
        long endTime=System.currentTimeMillis();
        System.out.println(endTime);
        System.out.println("cost time:" +(endTime-startTime));
        }
    }

