package Imgb;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.util.encoders.Hex;

import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        args = new String[]{
                "C:\\Users\\neben\\Desktop\\untitled2\\src\\main\\resources"
        };
        List<Path> allFiles = getAllFiles(args[0]);
        //List<Integer> interesting = Arrays.asList(15, 21, 22, 29, 30, 31);
        //printInterestingF(allFiles, interesting);
        //prettyPrintF(allFiles, 180);
        AtomicReference<Path> imgb = new AtomicReference<>();
        allFiles.forEach(x-> {
            if(x.getFileName().toString().contains("4373bd7d.imgb")){
                imgb.set(x);
                System.out.println("**Found!"+x);
            }
        });
        Path imgbPath = imgb.get();

        System.out.println("Trying revenge on "+imgbPath);
        revEngEImage(imgbPath);

        allFiles.forEach(x->{
            try {
                System.out.println("Trying revenge on "+imgbPath);
                revEngEImage(x);
            } catch (Exception e){
                e.printStackTrace();
            }
        });


        //prettyPrintFile(imgbPath);

        /*
        dim[0] = height;
        dim[1] = width;
         */
        /*
        allFiles.forEach(x->{
            try {
                int[] dim = getDimensions(Files.readAllBytes(x));
                System.out.println(x.toString()+" w:"+dim[1]+" h:"+dim[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        */
        /*
        int sample = new Random().nextInt(allFiles.size());
        Path p = allFiles.get(sample);
        System.out.println("Trying revenge on "+p);
        revEngEImage(p);

        //prettyPrintFile(p);
        */
    }

    private static void prettyPrintFile(Path p) {
        try {
            byte[] bytes = Files.readAllBytes(p);
            prettyPrint(bytes,160, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void revEngEImage(Path p) {
        try {
            byte[] bytes = Files.readAllBytes(p);
            int[] dim = getDimensions(bytes);
            int width = dim[1];
            int height = dim[0];

            int samplesPerPixel = 4;
            int[] bandOffsets = {0, 1, 2, 3}; // BGRA order

            byte[] rgbaPixelData = new byte[width * height * samplesPerPixel];
            rgbaPixelData = Arrays.copyOfRange(bytes, 32, bytes.length);;


            prettyPrint(bytes,3, true);
            //prettyPrint(bytes,2, false);

            System.out.println("h:"+height+"\t"+"w:"+width);
            System.out.println("bytes length: "+bytes.length+"\trgbaPixDat length: "+rgbaPixelData.length);
            DataBuffer buffer = new DataBufferByte(rgbaPixelData, rgbaPixelData.length);
            WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, samplesPerPixel * width, samplesPerPixel, bandOffsets, null);

            ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                    true,
                    false,
                    Transparency.TRANSLUCENT,
                    DataBuffer.TYPE_BYTE);

            BufferedImage image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

            System.out.println("image: " + image); // Should print: image: BufferedImage@<hash>: type = 0 ...

            String pathname = "output/new";
            StringBuilder temp = new StringBuilder();
            Arrays.stream(new Random().ints(5, 0, 9).toArray()).forEach(x->{
                temp.append(x);
            });
            pathname += temp.toString();
            pathname += ".png";
            ImageIO.write(image, "PNG", new File(pathname));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static int[] getDimensions(byte[] bytes) {
        int[] dim = new int[2];
        String s = Hex.toHexString(bytes);
        StringBuilder sb2 = new StringBuilder();
        for (int j = 0; j < s.length(); j++) {
            if(j%4 == 0){
                sb2.append(" ");
            }
            if(j%32 == 0){
                sb2.append("\n");
            }
            sb2.append(s.charAt(j));
        }
        String[] byte32 = sb2.toString().split("\n");
        List<String[]> byte32_8 = new ArrayList<>();
        Arrays.stream(byte32).forEach(x-> byte32_8.add(x.split(" ")));
        int width = Integer.parseInt(byte32_8.get(1)[5], 16);
        dim[1] = width;
        dim[0] = bytes.length / width / 4;
        System.out.println("h:"+dim[0]+"\t"+"w:"+dim[1]);
        return dim;
    }

    private static List<Path> getAllFiles(String arg) {
        List<Path> allFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(arg))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(allFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allFiles;
    }

    private static void prettyPrintF(List<Path> allFiles, int i) {
        printVirginLine();
        allFiles.forEach(x -> {
            try {
                byte[] bytes = Files.readAllBytes(x);
                prettyPrint(bytes, i, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void prettyPrint(byte[] bytes, int i, boolean b) {
        StringBuilder sb = new StringBuilder();
        String s = new Hex().toHexString(bytes);
        StringBuilder sb2 = new StringBuilder();
        for (int j = 0; j < s.length(); j++) {
            if(j%4 == 0){
                sb2.append(" ");
            }
            if(j%32 == 0){
                sb2.append("\n");
            }
            sb2.append(s.charAt(j));
        }
        String[] byte32 = sb2.toString().split("\n");
        List<String[]> byte32_8 = new ArrayList<>();
        Arrays.stream(byte32).forEach(x-> byte32_8.add(x.split(" ")));

        byte32_8.stream().limit(i).forEach(x-> {
            Arrays.stream(x).forEach(y-> {
                if(b){
                    System.out.print(Integer.parseInt(y, 16)+" ");
                } else {
                    System.out.print(y+" ");
                }
            });
            System.out.println();
        });
    }

    void misc(){
        String s = "lel";
        int i=1;
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1];
        s.chars().skip(0).limit(i).forEachOrdered(x -> {
            char c = (char) x;
            switch (c) {
                case '0':
                    sb.append(c);
                    //sb.append(" ");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        });
        sb.append("\tfsize: ").append(bytes.length).append("\t");
        //System.out.println(sb.toString());
    }

    private static void printInterestingF(List<Path> allFiles, List<Integer> interesting) {
        printInterestingHeader(interesting);
        allFiles.forEach(x -> {
            try {
                byte[] bytes = Files.readAllBytes(x);
                printInteresting(bytes, interesting);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void printInterestingHeader(List<Integer> interesting) {
        StringBuilder sb = new StringBuilder();
        interesting.forEach(x -> {
            sb.append(x.intValue()).append("\t");
        });
        System.out.println(sb.toString());
    }

    private static void printInteresting(byte[] bytes, List<Integer> li) {
        StringBuilder sb = new StringBuilder();
        String s = new Hex().toHexString(bytes);
        li.forEach(x -> {
            sb.append(s.charAt(x.intValue())).append("\t");
        });
        System.out.println(sb.toString());
    }

    private static void printVirginLine() {
        System.out.println("          11111111112222222222333333333344444444445555555555");
        System.out.println("012345678901234567890123456789012345678901234567890123456789");

    }

}
