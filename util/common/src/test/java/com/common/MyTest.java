package com.common;

import org.junit.Test;

public class MyTest {

    @Test
    public void computerNum() {

        int[] arr = {1,2,3,4,5,6,7,8,9};

        for(int a1 = 1;a1<=9;a1++){
            arr[0] =a1;
            for(int a2 = 1;a2<=9;a2++){
                if(a2==a1) continue;
                arr[1] =a2;
                for(int a3 = 1;a3<=9;a3++){
                    if(a3==a1||a3==a2) continue;
                    arr[2] =a3;
                    for(int a4 = 1;a4<=9;a4++){
                        if(a4==a1||a4==a2||a4==a3) continue;
                        arr[3] =a4;
                        for(int a5 = 1;a5<=9;a5++){
                            if(a5==a1||a5==a2||a5==a3||a5==a4) continue;
                            arr[4] =a5;
                            for(int a6 = 1;a6<=9;a6++){
                                if(a6==a1||a6==a2||a6==a3||a6==a4||a6==a5) continue;
                                arr[5] =a6;
                                for(int a7 = 1;a7<=9;a7++){
                                    if(a7==a1||a7==a2||a7==a3||a7==a4||a7==a5||a7==a6) continue;
                                    arr[6] =a7;
                                    for(int a8 = 1;a8<=9;a8++){
                                        if(a8==a1||a8==a2||a8==a3||a8==a4||a8==a5||a8==a6||a8==a7) continue;
                                        arr[7] =a8;
                                        for(int a9 = 1;a9<=9;a9++){
                                            if(checkExist(arr,8,a9))
                                            arr[8] =a9;
                                            if(check(arr)){
                                                System.out.println(a1+","+a2+","+a3+","+a4+","+a5+","+a6+","+a7+","+a8+","+a9);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean checkExist(int[] arr,final int i,final int a){
        for(int p=0;p<=i;p++){
            if(arr[p] == a)
                return true;
        }
        return false;
    }

    private boolean check( int[] arr ){

        if(arr.length!=9) return false;

        //检查是否为整数
        for(int i=0;i<arr.length-1;i++){
            int p = arr[i];
            if(p<0||p>9) return false;
        }

        //检查每个数是否为不一样
        for(int i=0;i<9;i++){
            int p1 = arr[i];
            for(int j=0;j<9;j++){
                if(i==j){
                    continue;
                }
                int p2 = arr[j];
                if(p1==p2) return false;
            }
        }

        int a1=arr[0];
        int a2=arr[1];
        int a3=arr[2];
        int a4=arr[3];
        int a5=arr[4];
        int a6=arr[5];
        int a7=arr[6];
        int a8=arr[7];
        int a9=arr[8];

        if(a1==1) return false;
        if(a5==1||a5==9) return false;
        if(a7==1) return false;

        if((a1*10+a2) -(a3*10+a4) !=(a5*10+a6)){
            return false;
        }
        if(a5*10+a6 != a7*(a8*10+a9)){
            return false;
        }
        return true;
    }


    @Test
    public void test1() throws Exception{

        int ZOO_EPHEMERAL = 1 << 0;
        int ZOO_SEQUENCE = 1 << 1;
        int flag = ZOO_EPHEMERAL | ZOO_SEQUENCE;

        System.out.println("ZOO_EPHEMERAL:"+ZOO_EPHEMERAL+",ZOO_SEQUENCE:"+ZOO_SEQUENCE+",flag:"+flag);
    }


}
