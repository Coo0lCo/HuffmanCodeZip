package com.example.Zip_.HuffmanCode.unzep;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class deCode {
    public static void main(String[] args) throws IOException{
        long start = System.currentTimeMillis();
        System.out.print("请输入您待解压文件的路径（绝对路径）：");
        new deCode(new Scanner(System.in).next());
        long end = System.currentTimeMillis();
        System.out.println("expend time "+(end - start) +" ms");
    }
    public deCode(String path) throws IOException{
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));

        //获得文件名的长度
        int FileNameLength = getFileNameLength(bufferedInputStream);

        //获得文件名
        String FileName = getFileName(bufferedInputStream,FileNameLength);

        //创建一个解压后的文件（根据原文件的.xx后缀觉得创建的文件为什么类型）
        String Path = FileName.substring(0,FileName.lastIndexOf("/"));
        String type = FileName.substring(FileName.lastIndexOf("."));
        System.out.print("请输入您解压后文件的name : ");
        FileName = Path+"/"+new Scanner(System.in).next()+type;
        File newFile = new File(FileName);

        //创建一个文件输入流
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));
        //获得哈夫曼编码字符串的总长度
        int HuffmanCodeLength = getHuffmanCodeLength(bufferedInputStream);
        //获得value的长度数组
        int[] value_length = getvalueLength(bufferedInputStream);

        //还原反转的哈夫曼编码表
        HashMap<String,Integer> map = getHuffmanCodeMap(value_length,bufferedInputStream);

        //利用反转的map将原文件还原出来
        readFile(bufferedOutputStream,bufferedInputStream,map,HuffmanCodeLength);

        bufferedOutputStream.close();
        bufferedInputStream.close();
        map.clear();
    }

    /**
     *
     * @param inputStream 流
     * @return 返回原文件的字节总个数
     * @throws IOException io异常
     */
    private int getHuffmanCodeLength(BufferedInputStream inputStream) throws IOException {
        int value;
        int CodeLength = 0 ;
        int k = 24;
        //得到一个，将十进制的四个数转换成8个单位的二进制数拼接后，再转换成2进制的结果。（这里的算法借鉴了别人的）
        for (int i = 0 ; i < 4 ; i++){
            value = inputStream.read();
            int tt = value << k;
            k = k - 8;
            CodeLength |= tt;
        }
        return CodeLength;
    }

    /**
     *
     * @param inputStream  流
     * @return 返回一个记录哈希表value（哈夫曼编码）的长度的数组（便于后续的反转哈希表的构建）
     * @throws IOException 异常
     */
    private int[] getvalueLength(BufferedInputStream inputStream) throws IOException {
        int[] value = new int[256];
        for (int i = 0; i < 256; i++) {
            int length = inputStream.read();
            value[i] = length;
        }
        return value;
    }

    /**
     *
     * @param inputStream 流
     * @return 首先第一件事，读取文件名的长度并返回
     * @throws IOException 异常
     */
    private int getFileNameLength(BufferedInputStream inputStream) throws IOException {
        return inputStream.read();
    }

    /**
     *
     * @param inputStream 流
     * @param FileNameLength 文件名的长度
     * @return  返回原文件的名字
     * @throws IOException 异常
     */
    private String getFileName(BufferedInputStream inputStream , int FileNameLength) throws IOException {
        StringBuilder FileName = new StringBuilder();
        while (FileName.length() < FileNameLength){
            int value = inputStream.read();
            FileName.append((char) value);
        }
        return FileName.toString();
    }


    /*
            这里的list模仿一个队列，
            便于后续的每8位一读取的操作
     */
    LinkedList<Integer> list = new LinkedList<>();

    /**
     *
     * @param value_length 这就是上面我们获得的记录哈希表value（哈夫曼编码）的长度的数组
     * @param inputStream 流
     * @return 返回一个重建好的反转哈希表（便于后续还原原文件的比对操作）
     * @throws IOException 流
     */
    public HashMap<String,Integer> getHuffmanCodeMap(int[] value_length , BufferedInputStream inputStream) throws IOException{
        HashMap<String,Integer> map = new HashMap<>();
        int value;
        /*
            这里就巧妙的运用队列的特性来实现
         */
        for (int i = 0 ; i < 256 ; i ++){
            String s = "";
            if (value_length[i] != 0){
                int count = 0;
                while (count < value_length[i]){
                    if (list.size() == 0){
                        value = inputStream.read();
                        read(value);
                    }
                    s += list.get(0);
                    list.remove(0);
                    count++;
                }
                map.put(s,i);
            }
        }
        return map;
    }

    /**
     * 还原原文件-------也就是最重要的操作之一
     * @param outputStream 流
     * @param inputStream 流
     * @param map 上面构造好的反转哈希表
     * @param HuffmanCodeLength 原文件字节的总个数
     * @throws IOException 异常
     */
    private void readFile(BufferedOutputStream outputStream,BufferedInputStream inputStream,HashMap<String,Integer> map , int HuffmanCodeLength) throws IOException {
        int count = 0 ;
        String s = "";
        while (count < HuffmanCodeLength){
            if (list.size() == 0){
                int value = inputStream.read();
                if (value == -1)break;
                read(value);

            }
            s += list.get(0);
            list.remove(0);
            if (map.containsKey(s)){
                outputStream.write(map.get(s));
                s = "";
                count++;
            }
        }
    }

    /**
     * 我觉得这下面的代码，是整个代码的核心代码。
     * 作用是：将每个字节转换成8各单位的二进制01串（当然我们这里的0101串是存放在队列中的，这就是精髓的地方）
     */
    private int count = 7;
    private void read(int date){
        for (int i = 0 ; i < 8 ; i ++){
            int t = date >> count;
            count -- ;
            if (count == -1) count = 7;
            t = t & 1;
            list.add(t);
        }
    }
}
