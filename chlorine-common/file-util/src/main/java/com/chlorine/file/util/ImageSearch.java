import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageSearch {

    static {
        // 加载OpenCV本地库，请确认路径与实际安装位置一致
        System.load("/opt/homebrew/Cellar/opencv/4.11.0_1/lib/libopencv_java4.11.dylib");
    }

    public static void main(String[] args) {
        // 加载要匹配的图片
        String targetImagePath = "/Users/chenlong/Downloads/1.png";
        Mat targetImage = Imgcodecs.imread(targetImagePath);
        if (targetImage.empty()) {
            System.out.println("目标图片加载失败: " + targetImagePath);
            return;
        }

        // 提取目标图片的颜色直方图
        Mat targetHist = extractHistogram(targetImage);

        File folder = new File("/Users/chenlong/Downloads/haibao");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定的文件夹不存在或不是一个目录: " + folder.getAbsolutePath());
            return;
        }

        for (File file : folder.listFiles()) {
            if (!file.isFile()) continue;

            Mat image = Imgcodecs.imread(file.getAbsolutePath());
            if (image.empty()) continue;

            Mat hist = extractHistogram(image);

            double compare = Imgproc.compareHist(targetHist, hist, Imgproc.CV_COMP_CORREL);
            if(compare > 0.8){ // 这里阈值可以根据实际情况调整
                System.out.println("找到相似图片: " + file.getName() + ", 相似度: " + compare);
            }
        }
    }

    private static Mat extractHistogram(Mat image){
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
        List<Mat> hsvChannels = new ArrayList<>();
        Core.split(hsvImage, hsvChannels);

        Mat hist = new Mat();
        MatOfInt histSize = new MatOfInt(50, 60); // H和S通道的bin数目
        MatOfFloat ranges = new MatOfFloat(0f, 180f, 0f, 256f); // H和S的取值范围
        MatOfInt channels = new MatOfInt(0, 1); // 计算第0和第1通道的直方图

        Imgproc.calcHist(hsvChannels, channels, new Mat(), hist, histSize, ranges);
        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        return hist;
    }
}
