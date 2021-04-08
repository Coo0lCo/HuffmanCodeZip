package 算法and数据结构.数据结构.哈夫曼树;

import java.io.*;
import java.util.*;
import java.util.List;
/*
        huffmanTree的存储内容有：
            指向最优孩子的指针
            权值
            指向父亲的指针
            特点：
                想要成为huffmanTree必须要使带权路径和最小。
                即WPL最小
     */
public class Node implements Comparable<Node>{

    Node left ;
    Node right;

    //这里我们暂时只记录当前结点是否具有权值
    //哈夫曼编码
    String coding;
    //权重
    Integer weigh;
    //字节
    Byte date;
    public Node(){
        left = right = null;
        coding = null ;
    }
    public Node(Integer val){
        this.weigh =val;
    }
    public Node(Integer val,Byte date){
            this.weigh =val;
            this.date =date;
    }
    public Node(Node left , Node right){
        this.left = left;
        this.right = right;
    }
    @Override
    public int compareTo(Node o) {
        return this.weigh - o.weigh;
    }
}
class HuffmanCoding{
        //哈夫曼表
    HashMap<Byte,Integer> map = new HashMap<>();
    //记录哈夫曼编码二进制字符串的长度
    int length = 0;
    public static void main(String[] args) throws FileNotFoundException {
        long k = System.currentTimeMillis();
        String path = "/Users/lemt/Desktop/huffman/1617822271638764.mp4";
        BufferedInputStream  inputStream = null;
        byte[] bytes = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(path));
            bytes = new byte[inputStream.available()];
            int i = 0;System.out.println("文件正在获取......... ");
            while (inputStream.available() != 0){
                bytes[i++] = (byte) inputStream.read();
            }
            System.out.println(Arrays.toString(bytes));
            long k2 = System.currentTimeMillis();
            System.out.println("文件获取完毕----> 共花费  "+(k2-k)+" ms");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String s = "i like like like java do dou p  own 9''a,dllsdnk java ?";
        //System.out.println("待压缩文本：");
        //System.out.println(s);
        //bytes = s.getBytes();
        //System.out.println(Arrays.toString(bytes));
        System.out.println("哈夫曼树正在构建...... ：");
        HuffmanCoding h = new HuffmanCoding();
        Node root = h.creatHuffmanCoding(bytes);
        long k2 = System.currentTimeMillis();
        System.out.println("哈夫曼树构建完毕  ---- 共花费"+(k2-k)+" ms");
        Set<Map.Entry<Byte,Integer>> q = h.map.entrySet();
        //System.out.println(q.toString());

        //System.out.println("以下为构建出的哈夫曼树：");
        /*

         */
        HashMap<Byte,String> codetable = new HashMap<>();
        System.out.println("哈夫曼编码表正在构建...... ");
        h.Coding(root,"",codetable);
        long k3 = System.currentTimeMillis();
        System.out.println("哈夫曼编码表构建完毕 ----- 共花费 "+(k3-k2)+" ms");
       //root.pre();
        Set<Map.Entry<Byte,String>> ste = codetable.entrySet();
        //System.out.println("哈夫曼编码表如下 ：");

        assert bytes != null;

        byte[] D = h.zip(bytes,codetable);
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream("/Users/lemt/Desktop/huffman/new.zip"));
            outputStream.writeObject(D);
            outputStream.writeObject(codetable);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //System.out.println("压缩得到的编码如下 ： ");
        //System.out.println(Arrays.toString(D));
        /*
            解压:
                1):将压缩的byte[]数组转换成二进制的哈夫曼编码，并拼接
                2):将拼接得到的哈夫曼编码转换成原先的哈夫曼编码
                3):将得到的哈夫曼编码与先前的哈夫曼编码表比对
                4):拼接得到原先的文本
         */
        long k1 = System.currentTimeMillis();
        System.out.println("解压所花时间--- ： "+(k1-k)+" ms");
        ObjectInputStream objectInputStream = null;
        HashMap<Byte,String> map = null;
        byte[] date = null;
        Set<Map.Entry<Byte,String>> set1 = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream("/Users/lemt/Desktop/huffman/new.zip"));
            date = (byte[]) objectInputStream.readObject();
            map = (HashMap<Byte, String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("解压后的哈夫曼编码字符串：");
        assert date != null;
        String  Data= h.get0_1String(date);
        assert map != null;
        HashMap<String,Byte> Map = h.to_Value_KeyMap(map);
        //还原文本
        byte[] p = h.put(Data,Map);
        System.out.println(Arrays.toString(p));
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/lemt/Desktop/huffman/ZIP.mp4");
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(p);
            System.out.println("解压完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    /**
     * 获得压缩后的byte[]
     * @param bytes     待压缩的文件
     * @param codetable 哈夫曼编码表（每一个字节对应一个哈夫曼编码）
     * @return  返回压缩后的byte[]数组
     */
    public byte[] zip(byte[] bytes,HashMap<Byte,String> codetable){
        StringBuilder stringBuilder = new StringBuilder();
        for(byte date : bytes){
            stringBuilder.append(codetable.get(date));
        }
        //
        this.length = stringBuilder.length();
        //System.out.println("哈夫曼编码字符串 ：");
        //System.out.println(stringBuilder.toString());
        //接下来我们开始设计我们的byte[]数组的长度
        int byte_length = stringBuilder.length()%8 == 0 ? stringBuilder.length()/8 : stringBuilder.length()/8+1;
        byte[] date = new byte[byte_length];
        //接下来开始每8位一转换,存储就存储进byte[]数组中,
        int index = 0 ;
        for (int i = 0; i < stringBuilder.length() ; index++) {
            if((stringBuilder.length() - i) < 8){
                date[index] = (byte) Integer.parseInt(stringBuilder.substring(i,stringBuilder.length()),2);
                i = stringBuilder.length();
            }else {
                date[index] = (byte) Integer.parseInt(stringBuilder.substring(i,i+8),2);
                i += 8 ;
            }
        }
        return date;
    }
        //获得哈夫曼编码，这里必须通过层序遍历获得哈夫曼编码
    public void Coding(Node root,String coding,HashMap<Byte,String> codetable){
        if(root == null)return;
        if(root.left == null && root.right == null){
            root.coding = coding;
            codetable.put(root.date,root.coding);
        }
        Coding(root.left,coding+"0",codetable);
        Coding(root.right, coding + "1",codetable);
    }
    public  Set<Map.Entry<Byte,Integer>> getValue(byte[] s){
        for (byte b : s) {
            if (!map.containsKey(b)) {
                map.put(b, 1);
            } else {
                map.put(b, (map.get(b) + 1));
            }
        }
        return map.entrySet();
    }
    public List<Node> getNode(Set<Map.Entry<Byte, Integer>> k){
        List<Node> list = new ArrayList<>();
        for (Map.Entry<Byte,Integer> e : k){
            list.add(new Node(e.getValue(),e.getKey()));
        }
        return list;
    }
    public Node creatHuffmanCoding(byte[] s){
        Set<Map.Entry<Byte, Integer>> coding_number = getValue(s);
        List<Node> HNode = getNode(coding_number);
        while (HNode.size() > 1){
            Collections.sort(HNode);
            Node left_node = HNode.remove(0);
            Node right_node = HNode.remove(0);
            Integer k1 = left_node.weigh;
            Integer k2 = right_node.weigh;
            Node node = new Node((k1+k2));
            node.left = left_node;
            node.right = right_node;
            HNode.add(node);
        }
        return HNode.get(0);
    }

    /**
     *
     * 进行步骤一将压缩好的byte[]数组反编译成二进制的字符串（哈夫曼编码字符串）
     * @param date      待解压的压缩byte[]数组
     * @return  返回二进制的字符串（哈夫曼编码字符串）
     */
    public String get0_1String(byte[] date){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0 ; i  < date.length ; i++){
            byte v = date[i];
            String s = Integer.toBinaryString(v);
            if(s.length() >= 8){
                s = new StringBuilder(s).substring(s.length()-8,s.length());
                stringBuilder.append(s);
            }else {
                StringBuilder str = new StringBuilder();
                if (i == date.length-1){
                    int length = this.length - (this.length / 8)*8;
                    while (str.length() != length - s.length()){
                        str.append("0");
                    }
                }else {
                    while (str.length() != (8 - s.length())) {
                        str.append("0");
                    }
                }
                str.append(s);
                stringBuilder.append(str.toString());
            }
        }
        return stringBuilder.toString();
    }

    /**
     *
     * 反转key and value 后得到新的Map<Byte,String>集合
     * @param codeTable 待转换的Map集合
     * @return  返还转换好的map集合
     */
    public HashMap<String,Byte> to_Value_KeyMap(HashMap<Byte,String> codeTable){
        HashMap<String,Byte> map = new HashMap<>();
        for (Map.Entry<Byte,String> v : codeTable.entrySet()){
            map.put(v.getValue(),v.getKey());
        }
        return map;
    }
    //直接得到原样文本对应的字节
    public byte[] put(String date , HashMap<String,Byte> codeTable){
        StringBuilder stringBuilder = new StringBuilder(date);
        LinkedList<Byte> list = new LinkedList<>();
        int i = 0 , j = 1;
        while (i != stringBuilder.length()){
            String s = "";
            s = stringBuilder.substring(i,j);
            if(codeTable.get(s) != null){
                list.add(codeTable.get(s));
                i = j;
            }
            j++;
        }
        byte[] d = new byte[list.size()];
        int k = 0 ;
        for (Byte g : list){
            d[k++] = g;
        }
        return d;
    }
}

