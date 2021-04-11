package com.example.Zip_.HuffmanCode.zip;

import java.io.*;
import java.util.*;

/*
    这里我会将压缩的文件写入 xxx.zip文件中（创建File对象用于创建次文件）
 */
public class Zip {
    public static void main(String[] args) throws IOException{
        long start = System.currentTimeMillis();
        new Zip(new Scanner(System.in).next());
        long end = System.currentTimeMillis();
        System.out.println("expend time -----> "+(end - start)+" ms");
    }
    public Zip(String path) throws IOException {
        getResources resources = new getResources(path);

        int[] ints = resources.getInts();

        BufferedInputStream bufferedInputStream = resources.getFile();

        BufferedOutputStream bufferedOutputStream = getOutputStream(path);
        //首先写入文件名字
        writeFileName(bufferedOutputStream);

        writeCodeLength(resources.length,bufferedOutputStream);



        List<Node> nodes = resources.getNodes(ints);

        Huffman huffman = new Huffman(nodes);

        Node root = huffman.root;

        HashMap<Integer,String> map = new HashMap<>();
        huffman.pre(root,map,"");
        writeHasMap(map,bufferedOutputStream);
        writeHuffmanCodeString(bufferedInputStream,bufferedOutputStream,map);

        bufferedInputStream.close();
        bufferedOutputStream.close();
        map.clear();
        nodes.clear();
        root.clear();
    }
    private String FileName = "";
    private BufferedOutputStream getOutputStream(String path) throws FileNotFoundException {
        int index = path.lastIndexOf(".");
        this.FileName = path ;
        path = path.substring(0,index);
        path += ".zip";
        new File(path+".zip");
        return new BufferedOutputStream(new FileOutputStream(path));
    }
    private void writeFileName(BufferedOutputStream outputStream) throws IOException {
        outputStream.write(FileName.length());
        for (int i = 0 ; i  < FileName.length() ; i ++){
            outputStream.write(FileName.charAt(i));
        }
    }
    private void writeCodeLength(int CodeLength , BufferedOutputStream outputStream) throws IOException {
        String Code = "";
        /*
            注意我这里存进去是32位的二进制
            在我解压的时候我就可以按照这样的规则拿到 length（截取前32位的01字符）
         */
        for (int i = 0x80000000 ; i != 0 ; i >>>= 1){
            Code += (CodeLength & i) == 0 ? '0' : '1';
        }

        for (int j = 0 ; j < Code.length() ; j ++){
            writeBit(Code.charAt(j)-'0',outputStream);
        }
    }
    private void writeHasMap(HashMap<Integer,String> map , BufferedOutputStream outputStream) throws IOException {
        //首先写入哈希表中每个value的长度
        for (int i = 0 ; i < 256 ; i ++){
            if (map.containsKey(i)){
                String s = map.get(i);
                outputStream.write((byte) s.length());
            }else {
                outputStream.write((byte)0);
            }
        }
        //然后写入哈希表的value
        for (int j = 0 ; j < 256 ; j++){
            if (map.containsKey(j)){
                String code = map.get(j);
                for (int i = 0 ; i < code.length() ; i ++){
                    char ch = code.charAt(i);
                    writeBit(ch-'0',outputStream);
                }
            }
        }
    }
    public void writeHuffmanCodeString(BufferedInputStream inputStream , BufferedOutputStream outputStream , HashMap<Integer,String> map) throws IOException {
        int value = inputStream.read();
        while (value != -1){
            String code = map.get(value);
            for (int i = 0; i < code.length(); i++) {
                char ch = code.charAt(i);
                writeBit(ch-'0',outputStream);
            }
            value = inputStream.read();
        }
        //注意如果剩下的不满足八个01串，我们要全部写入
        if (count != -1)outputStream.write(buffer);
    }

    private int count = 7 ;
    private int buffer = 0 ;
    public void writeBit(int date , BufferedOutputStream bufferedOutputStream) throws IOException {
        int k = date << count ;
        buffer = buffer | k ;
        count--;
        if (count == -1){
            bufferedOutputStream.write(buffer);
            count = 7 ;
            buffer = 0 ;
        }
    }
}
class getResources{
    String path = "";
    int length ;
    public getResources(String path){
        this.path = path ;
    }

    public BufferedInputStream getFile(){
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bufferedInputStream;
    }
    public int[] getInts() throws IOException {
        int[] ints = new int[256];
        BufferedInputStream bufferedInputStream = getFile();
        int value = bufferedInputStream.read();
        while (value != -1){
            this.length++;
            ints[value] ++;
            value = bufferedInputStream.read();
        }
        bufferedInputStream.close();
        return ints;
    }

    public List<Node> getNodes(int[] ints){

        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] != 0){
                nodes.add(new Node(ints[i],i));
            }
        }
        return nodes;
    }
}

class Node implements Comparable<Node>{
    Node left ;

    Node right;
    //权重
    int weight ;
    //字节
    int date;
    public Node(int weight){
        this.weight = weight;
    }
    public Node(int weight , int date){
        this.weight = weight;
        this.date =date ;
    }
    @Override
    public int compareTo(Node o) {
        return this.weight - o.weight;
    }
    public void clear(){
        left = null;
        right = null;
        weight = 0 ;
        date = 0 ;
    }
}

class Huffman{
    Node root = null ;

    /**
     * 构建哈夫曼树
     * @param nodes 叶子结点集合
     */
    public Huffman(List<Node> nodes){
        Collections.sort(nodes);
        while (nodes.size() != 1){
            Collections.sort(nodes);
            Node left = nodes.remove(0);
            Node right = nodes.remove(0);
            root = new Node(left.weight+right.weight);
            root.left = left;
            root.right = right;
            nodes.add(root);
        }
        this.root = nodes.remove(0);
    }

    /**
     * 构建哈夫曼表（前序遍历获得）
     * @param root 哈夫曼树
     * @param map 待添加键值对哈夫曼编码表
     * @param str 用于拼接字符串
     */
    public void pre(Node root, Map<Integer,String> map, String str){
        if(root == null)return;
        if(root.left == null && root.right == null){
            map.put(root.date,str);
            return;
        }
        if(root.left != null){
            pre(root.left,map,str+"0");
        }if(root.right != null){
            pre(root.right,map,str+"1");
        }
    }
}
