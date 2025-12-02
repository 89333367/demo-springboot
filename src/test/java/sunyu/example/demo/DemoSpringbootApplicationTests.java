package sunyu.example.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.ttzero.excel.reader.Drawings;
import org.ttzero.excel.reader.ExcelReader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class DemoSpringbootApplicationTests {
    Log log = LogFactory.get();

    @Test
    void 读取二维码图片() {
        try {
            String result = QrCodeUtil.decode(FileUtil.file("d:/tmp/二维码.png"));
            log.info("识别内容: " + result);
        } catch (Exception e) {
            log.error("二维码识别失败: " + e.getMessage());
        }
    }

    @Test
    void 读取excel里的二维码图片() {
        try (ExcelReader reader = ExcelReader.read(Paths.get("d:/tmp/测试二维码.xlsx"))) {
            // 按行读取第1个Sheet并打印
            reader.sheet(0).rows().forEach(System.out::println);
            List<Drawings.Picture> pictures = reader.sheet(0).listPictures();
            for (Drawings.Picture pic : pictures) {
                try {
                    String result = QrCodeUtil.decode(pic.localPath.toFile());
                    log.info("识别内容: " + result);
                } catch (Exception e) {
                    log.error("二维码识别失败: " + e.getMessage());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

}