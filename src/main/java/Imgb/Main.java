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

}
