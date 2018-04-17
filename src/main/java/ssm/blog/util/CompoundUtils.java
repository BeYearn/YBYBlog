package ssm.blog.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CompoundUtils {

    static int imageIndex = 0;

    public static void main(String[] args) {
        try {

            //test
            //compoundImage();

            mergeBothpart();
            startCompoud();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void mergeBothpart() throws IOException {
        String sourceStr = "E:\\120ss";
        File sourceFile = new File(sourceStr);

        File[] allFiles = sourceFile.listFiles();
        System.out.println("allFiles size:" + allFiles.length);

        ArrayList<List<File>> pairWingList = new ArrayList<>();
        ArrayList<List<File>> pairEyeList = new ArrayList<>();

        for (File f : allFiles) {
            for (File f2 : allFiles) {
                String name = f.getName();
                String name2 = f2.getName();
                if (name.contains("LWing")) {
                    if (name2.contains("RWing") && name.replace("LWing", "").equals(name2.replace("RWing", ""))) {
                        ArrayList<File> pair = new ArrayList<>();
                        pair.add(f);
                        pair.add(f2);
                        pairWingList.add(pair);
                        //f.delete();
                        //f2.delete();
                    }
                } else if (name.contains("LEye")) {
                    if (name2.contains("REye") && name.replace("LEye", "").equals(name2.replace("REye", ""))) {
                        ArrayList<File> pair = new ArrayList<>();
                        pair.add(f);
                        pair.add(f2);
                        pairEyeList.add(pair);
                        //f.delete();
                        //f2.delete();
                    }
                }
            }
        }

        for (List<File> list : pairWingList) {
            String mergedWingName = list.get(0).getName().substring(0, 3) + list.get(0).getName().substring(4);
            compoundImage("E:\\120ss\\" + mergedWingName, list.get(0), list.get(1));
        }

        for (List<File> list : pairEyeList) {
            String mergedEyeName = list.get(0).getName().substring(0, 3) + list.get(0).getName().substring(4);
            compoundImage("E:\\120ss\\" + mergedEyeName, list.get(0), list.get(1));
        }

    }


    public static void startCompoud() throws IOException {
        String sourceStr = "E:\\120ss";
        File sourceFile = new File(sourceStr);

        File[] allFiles = sourceFile.listFiles();
        System.out.println("allFiles size:" + allFiles.length);

        HashMap<String, HashMap<String, List<File>>> sourceMap = new LinkedHashMap<>();

        String colors[] = {"FB", "FG", "FP", "FR", "FW", "FY"}; //6
        String parts[] = {"Body", "Wing", "Eye", "Smeller", "Tail", "Back"}; //上一步合成两个 这一步少了两个

        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < parts.length; j++) {
                HashMap<String, List<File>> partMap = new LinkedHashMap<>();
                sourceMap.put(colors[i], partMap);
            }
        }

        for (File f : allFiles) {
            if (f.isFile()) {
                String fileName = f.getName();

                if (fileName.substring(3, 4).equals("R") || fileName.substring(3, 4).equals("L")) {  //用来排除原生的左右部件
                    continue;
                }

                for (int i = 0; i < colors.length; i++) {
                    for (int j = 0; j < parts.length; j++) {
                        if (fileName.contains(colors[i])) {
                            HashMap<String, List<File>> partMap = sourceMap.get(colors[i]);
                            if (fileName.contains(parts[j])) {
                                List<File> onePartList = partMap.get(parts[j]);
                                if (onePartList == null) {
                                    ArrayList<File> list = new ArrayList<>();
                                    list.add(f);
                                    partMap.put(parts[j], list);
                                } else {
                                    onePartList.add(f);
                                }
                            }
                        }

                    }
                }
            }
        }

        //颜色,<部位<集合>>
        //这个map由于开始全文件遍历时是按字母排序的,所以里面内容的顺序一定是:fb-back body eye smeller tail wing 这个顺序
        for (Map.Entry<String, HashMap<String, List<File>>> entry : sourceMap.entrySet()) {
            String color = entry.getKey();
            HashMap<String, List<File>> partMap = entry.getValue();

            //遍历所有部位的list的集合
            ArrayList<List<File>> lists = new ArrayList<>(); //肯定有六个
            for (Map.Entry<String, List<File>> entry1 : partMap.entrySet()) {
                lists.add(entry1.getValue());
            }
            for (File file1 : lists.get(0)) {
                for (File file2 : lists.get(1)) {
                    for (File file3 : lists.get(2)) {
                        for (File file4 : lists.get(3)) {
                            for (File file5 : lists.get(4)) {
                                for (File file6 : lists.get(5)) {
                                    compoundImage("E:\\120output\\" + generateName(color, file1, file2, file3, file4, file5, file6)
                                            , file6, file2, file3, file4, file5, file1);//身体得在最下,头饰得在最上,所以这样的顺序

                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static String generateName(String color, File... files) {
        StringBuilder nameBuiler = new StringBuilder();
        //按照上面那个顺序生成对应名字
        for (File f : files) {
            String justName = f.getName().split("\\.")[0];   //干掉 后缀
            String justNameSimple = justName.substring(3);          //干掉 FB_

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
     * @param outputName E:\\120output\\" + imageIndex++ + ".png    得写全了!!!!!
     * @param files
     * @throws IOException
     */
    public static void compoundImage(String outputName, File... files) throws IOException {
        Thumbnails.Builder<File> firstBuilder = Thumbnails.of(files[0]);
        ArrayList<BufferedImage> bfImageList = new ArrayList<>();
        for (int i = 1; i < files.length; i++) {
            File file = files[i];
            BufferedImage bufferedImage = Thumbnails.of(file).scale(1.0).outputQuality(1.0).asBufferedImage();
            bfImageList.add(bufferedImage);
        }
        firstBuilder = firstBuilder.scale(1.0);
        for (int i = 0; i < bfImageList.size(); i++) {
            firstBuilder.watermark(Positions.CENTER, bfImageList.get(i), 1.0f);
        }

        firstBuilder.toFile(outputName);
    }


    public static void compoundImage() throws IOException {
        File file1 = new File("E:\\120ss\\FB_Body_1.png");
        File file2 = new File("E:\\120ss\\FB_Back_1.png");
        File file3 = new File("E:\\120ss\\FB_Smeller_1.png");
        File file4 = new File("E:\\120ss\\FB_RWing_1.png");

        Thumbnails.Builder<File> BodyBuiler = Thumbnails.of(file1);

        BufferedImage file2BfImage = Thumbnails.of(file2).scale(1.0).outputQuality(1.0).asBufferedImage();
        BufferedImage file3BfImage = Thumbnails.of(file3).scale(1.0).outputQuality(1.0).asBufferedImage();
        BufferedImage file4BfImage = Thumbnails.of(file4).scale(1.0).outputQuality(1.0).asBufferedImage();

        BodyBuiler.scale(1.0).watermark(Positions.CENTER, file2BfImage, 1.0f)
                .watermark(Positions.CENTER, file3BfImage, 1.0f)
                .watermark(Positions.CENTER, file4BfImage, 1.0f);

        BodyBuiler.toFile("E:\\testimg.png");

    }
}
