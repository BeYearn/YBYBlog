package ssm.blog.util;

import javafx.geometry.Pos;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import ssm.blog.util.position.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * 1. 这里抠出的body身体周围没有像素,紧贴左上  但模板图有一定像素而且模板xml是从1,1开始的
 * 2. 看到部分只有一半的是因为扣除的图并不是完全把周边多余的都干掉(ps自动裁切发现原图有点问题)
 *
 *
 */


@SuppressWarnings("ALL")
public class SpriteImageUtils {

    /**
     * 原本是左右两张图合一张 现在只要名字,把俩名字和一个名字
     *
     * @throws IOException
     */
    public static ArrayList<String> mergeBothpartName() throws IOException {

        ArrayList<String> allPartNameList = new ArrayList<>();     //带 160/前缀的(从plist来的)

        for (Map.Entry<String, PartFrame> entry : plistMap.entrySet()) {
            allPartNameList.add(entry.getKey());
        }
        for (int i = 0; i < allPartNameList.size(); i++) {
            for (int j = 0; j < allPartNameList.size(); j++) {
                String name = allPartNameList.get(i);
                String name2 = allPartNameList.get(j);

                if (name.contains("LWing")) {
                    if (name2.contains("RWing") && name.replace("LWing", "").equals(name2.replace("RWing", ""))) {
                        allPartNameList.remove(i);
                        allPartNameList.remove(j);
                        allPartNameList.add(name + "+" + name2);
                    }
                } else if (name.contains("LEye")) {
                    if (name2.contains("REye") && name.replace("LEye", "").equals(name2.replace("REye", ""))) {
                        allPartNameList.add(name + "+" + name2);
                        allPartNameList.remove(i);
                        allPartNameList.remove(j);
                    }
                }
            }
        }
        return allPartNameList;
    }


    public static void startCompoud(ArrayList<String> allPartNameList) throws IOException {

        HashMap<String, HashMap<String, List<String>>> sourceMap = new LinkedHashMap<>();//颜色,<部位<集合>>

        String colors[] = {"FB", "FG", "FP", "FR", "FW", "FY"}; //6
        String parts[] = {"Body", "Wing", "Eye", "Smeller", "Tail", "Back"}; //上一步合成两个 这一步少了两个

        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < parts.length; j++) {
                HashMap<String, List<String>> partMap = new LinkedHashMap<>();
                partMap.put(parts[j], new ArrayList<String>());
                sourceMap.put(colors[i], partMap);
            }
        }

        for (String name : allPartNameList) {
            for (int i = 0; i < colors.length; i++) {
                for (int j = 0; j < parts.length; j++) {
                    if (name.contains(colors[i])) {
                        HashMap<String, List<String>> partMap = sourceMap.get(colors[i]);
                        if (name.contains(parts[j])) {
                            List<String> onePartList = partMap.get(parts[j]);
                            if (onePartList == null) {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(name);
                                partMap.put(parts[j], list);
                            } else {
                                onePartList.add(name);
                            }
                        }
                    }

                }
            }
        }

        for (Map.Entry<String, HashMap<String, List<String>>> entry : sourceMap.entrySet()) {
            String color = entry.getKey();
            HashMap<String, List<String>> partMap = entry.getValue();

            //遍历所有部位的list的集合
            ArrayList<List<String>> lists = new ArrayList<>(); //肯定有六个
            for (Map.Entry<String, List<String>> entry1 : partMap.entrySet()) {
                lists.add(entry1.getValue());
            }
            for (String name1 : lists.get(0)) {
                for (String name2 : lists.get(1)) {
                    for (String name3 : lists.get(2)) {
                        for (String name4 : lists.get(3)) {
                            for (String name5 : lists.get(4)) {
                                for (String name6 : lists.get(5)) {

                                    HashMap<String, PartFrame> framMap = new HashMap<>();
                                    dealwithName(framMap, name1);
                                    dealwithName(framMap, name2);
                                    dealwithName(framMap, name3);
                                    dealwithName(framMap, name4);
                                    dealwithName(framMap, name5);
                                    dealwithName(framMap, name6);
                                    getSpriteImage("E:\\SpriteOut\\" + generateName(color, name1, name2, name3, name4, name5, name6), framMap);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static void dealwithName(HashMap<String, PartFrame> framMap, String name) {
        if (name.contains("+")) {
            String[] split = name.split("\\+");
            framMap.put(split[0], getPartFrame(split[0]));
            framMap.put(split[1], getPartFrame(split[1]));
        } else {
            framMap.put(name, getPartFrame(name));
        }
    }


    public static String generateName(String color, String... names) {
        StringBuilder nameBuiler = new StringBuilder();
        //按照上面那个顺序生成对应名字
        for (String f : names) {
            String justName = f.split("\\.")[0];   //干掉 后缀
            String justNameSimple = justName.substring(7);//干掉 160/ FB_

            String justNameSimple2 = "";
            if (justNameSimple.contains("Back")) {
                justNameSimple2 = justNameSimple.replace("Back", "ba");
            } else if (justNameSimple.contains("Body")) {
                justNameSimple2 = justNameSimple.replace("Body", "bo");
            } else if (justNameSimple.contains("Eye")) {
                justNameSimple2 = justNameSimple.replace("Eye", "e");
            } else if (justNameSimple.contains("Smeller")) {
                justNameSimple2 = justNameSimple.replace("Smeller", "s");
            } else if (justNameSimple.contains("Tail")) {
                justNameSimple2 = justNameSimple.replace("Tail", "t");
            } else if (justNameSimple.contains("Wing")) {
                justNameSimple2 = justNameSimple.replace("Wing", "w");
            }
            nameBuiler.append(justNameSimple2).append("-");
        }
        String name = nameBuiler.toString();
        return color + "-" + name.substring(0, name.length() - 1) + ".png";
    }


    /**
     * @param outputName   E:\\120output\\" + imageIndex++ + ".png    得写全了!!!!!
     * @param partFrameMap
     * @throws IOException
     */
    public static void getSpriteImage(String outputName, Map<String, PartFrame> partFrameMap) throws IOException {

        //空图
        File emptyFile = new File("E:\\fish_backup\\sprite_backup\\empty.png");
        //合图
        File hetuFile = new File("E:\\fish_backup\\sprite_backup\\160hetu\\Plist.png");

        Thumbnails.Builder<File> spriteBuiler = Thumbnails.of(emptyFile);

        HashMap<String, BufferedImage> partBfImageMap = new HashMap<>();
        for (Map.Entry<String, PartFrame> entry : partFrameMap.entrySet()) {  //先将各部位抠出来存起来
            BufferedImage partBfImage = Thumbnails.of(hetuFile).sourceRegion(entry.getValue().x, entry.getValue().y, entry.getValue().with, entry.getValue().height).scale(1.0).asBufferedImage();
            partBfImageMap.put(entry.getKey(), partBfImage);
        }
        Thumbnails.Builder<File> finalSpriteBuilder = spriteBuiler.scale(1.0);
        //开始往上打水印啦
        for (Map.Entry<String, BufferedImage> entry : partBfImageMap.entrySet()) {
            Position position = null;
            if (entry.getKey().contains("Body")) {
                position = new BodyPos();
            } else if (entry.getKey().contains("Back")) {
                position = new BackPos();
            } else if (entry.getKey().contains("LEye")) {
                position = new EyeLPos();
            } else if (entry.getKey().contains("LWing")) {
                position = new WingLPos();
            } else if (entry.getKey().contains("RWing")) {
                position = new WingRPos();
            } else if (entry.getKey().contains("REye")) {
                position = new EyeRPos();
            } else if (entry.getKey().contains("Smeller")) {
                position = new SmellerPos();
            } else if (entry.getKey().contains("Tail")) {
                position = new TailPos();
            }
            finalSpriteBuilder.watermark(position, entry.getValue(), 1.0f);
        }

        finalSpriteBuilder.toFile(outputName);
    }


    /**
     * 从合图中获取部件的位置和大小
     *
     * @param partName
     * @return
     */
    public static PartFrame getPartFrame(String partName) {

        PartFrame partFrame = null;
        try {
            partFrame = plistMap.get(partName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partFrame;
    }

    static HashMap<String, PartFrame> plistMap;

    /**
     * 现将plist这个xml的信息对应起来
     * key---dict
     */
    public static void arrangePlist() {
        try {
            SAXReader saxReader = new SAXReader();
            //读该plist的时候注意修改(删掉除frames之外的层级,去掉不必要的key)
            Document document = saxReader.read(new File("E:\\fish_backup\\sprite_backup\\160hetu\\Plist.plist"));

            List<Element> keyList = document.selectNodes("/plist/dict/dict/key");
            List<Element> dictList = document.selectNodes("/plist/dict/dict/dict");

            plistMap = new HashMap<>();

            for (int i = 0; i < keyList.size(); i++) {  //keyList和dictList的size是相同的

                Element element = dictList.get(i);
                Element firstString = element.element("string");
                String position = firstString.getText();
                String replace1 = position.replace("{", "").replace("}", "");
                String[] posArr = replace1.split(",");
                PartFrame partFrame = new PartFrame(Integer.parseInt(posArr[0]), Integer.parseInt(posArr[1]), Integer.parseInt(posArr[2]), Integer.parseInt(posArr[3]));
                plistMap.put(keyList.get(i).getText(), partFrame);
            }
            System.out.println("plist整理完毕 size：" + plistMap.size());

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            arrangePlist();
            ArrayList<String> allNames = mergeBothpartName();
            startCompoud(allNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}